package com.github.anthonyo.swingifts.service;

import com.github.anthonyo.swingifts.domain.*;
import com.github.anthonyo.swingifts.repository.EventRepository;
import com.github.anthonyo.swingifts.repository.GiftDrawingRepository;
import com.github.anthonyo.swingifts.repository.ParticipationRepository;
import com.github.anthonyo.swingifts.repository.UserRepository;
import com.github.anthonyo.swingifts.service.errors.EntityNotFoundException;
import com.github.anthonyo.swingifts.service.util.RandomUtil;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing {@link Event}.
 */
@Service
@Transactional
public class EventService {

    private final Logger log = LoggerFactory.getLogger(EventService.class);

    private final EventRepository eventRepository;

    private final GiftDrawingRepository giftDrawingRepository;

    private final UserRepository userRepository;

    private final ParticipationRepository participationRepository;

    public EventService(EventRepository eventRepository, GiftDrawingRepository giftDrawingRepository, UserRepository userRepository, ParticipationRepository participationRepository) {
        this.eventRepository = eventRepository;
        this.giftDrawingRepository = giftDrawingRepository;
        this.userRepository = userRepository;
        this.participationRepository = participationRepository;
    }

    /**
     * Save a event.
     *
     * @param event the entity to save.
     * @return the persisted entity.
     */
    public Event save(Event event, String requesterUserLogin) throws EntityNotFoundException {
        log.debug("Request to save Event : {}", event);
        boolean isModifying = event.getId() != null;
        boolean isCreating = !isModifying;
        User currentUser = null;
        if (isModifying) {
            // Modification mode: can't edit the publicKey
            Event dbEvent = eventRepository.findById(event.getId()).orElseThrow(() -> new EntityNotFoundException("Event not found"));
            checkRequesterUserLoginIsEventAdmin(dbEvent, requesterUserLogin); // Only admin can modify the event
            event.setPublicKey(dbEvent.getPublicKey());
            // Can't modify admin
            event.setAdmin(dbEvent.getAdmin());
        } else {
            // Creation mode: can't edit the publicKey
            event.setPublicKey(null);
            // Admin is the creator
            currentUser = userRepository.findOneByLogin(requesterUserLogin).orElseThrow(() -> new EntityNotFoundException("Current user not found"));
            event.setAdmin(currentUser);
        }
        if (BooleanUtils.isTrue(event.isPublicKeyEnabled()) && event.getPublicKey() == null) {
            // Want to enable publicKey, but publicKey is still null: must generate a new one
            event.setPublicKey(RandomUtil.generateEventPublicKey());
        }
        Event savedEvent = eventRepository.save(event);
        if (isCreating) {
            // Add the admin as a participant
            event.addParticipations(participationRepository.save(
                new Participation().userAlias(currentUser.getLogin()).event(event).user(currentUser)
            ));
        }
        return savedEvent;
    }


    /**
     * Get all the events.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<Event> findForRequesterUserLogin(String requesterUserLogin) {
        log.debug("Request to get all Events");
        return eventRepository.findByParticipationsUserLoginIsOrAdminLoginIs(requesterUserLogin);
    }


    /**
     * Get one event by id.
     *
     * @param id the id of the entity.
     * @param requesterUserLogin the login of the user who asks for this event
     * @return the entity.
     * @throws AccessDeniedException if the given requesterUserLogin is not the admin or a participant of the event
     */
    @Transactional(readOnly = true)
    public Optional<Event> findOneForRequesterUserLogin(Long id, String requesterUserLogin) throws AccessDeniedException {
        log.debug("Request to get Event : {}", id);
        checkEventIdAllowedForRequesterUserLogin(id, requesterUserLogin);
        return eventRepository.findById(id).map(
            event -> event.myGiftDrawing(
                event.getGiftDrawings().stream()
                    .filter(giftDrawing -> requesterUserLogin.equals(giftDrawing.getDonor().getUserLoginIgnoringNull()))
                    .collect(Collectors.toSet()
                )
            )
        );
    }

    public void checkEventIdAllowedForRequesterUserLogin(Long eventId, String requesterUserLogin) {
        if (!eventIdAllowedForRequesterUserLogin(eventId, requesterUserLogin)) {
            throw new AccessDeniedException("User is not allowed to access this event");
        }
    }

    private boolean eventIdAllowedForRequesterUserLogin(Long eventId, String requesterUserLogin) {
        return eventRepository.existsByIdAndParticipationsUserLoginOrAdminLogin(eventId, requesterUserLogin);
    }

    /**
     * Delete the event by id.
     *
     * @param requesterUserLogin
     * @param id the id of the entity.
     * @return
     */
    public Event delete(Long id, String requesterUserLogin) throws EntityNotFoundException {
        log.debug("Request to delete Event : {}", id);
        Event event = eventRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Event not found"));
        checkRequesterUserLoginIsEventAdmin(event, requesterUserLogin); // Only admin can do this
        eventRepository.deleteById(id);
        return event;
    }

    /**
     *
     * @param id
     * @param requesterUserLogin
     * @throws EntityNotFoundException if event does not exist
     * @throws AccessDeniedException if given user is not the admin of the event
     */
    public void drawGifts(Long id, String requesterUserLogin) throws EntityNotFoundException, AccessDeniedException {
        Event event = eventRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Event not found"));
        checkRequesterUserLoginIsEventAdmin(event, requesterUserLogin); // Only admin can do this
        // check that number of gifts to give equals the one expected to be received
        int numberOfGiftsToDonate = 0;
        int numberOfGiftsToReceive = 0;
        for (Participation participation : event.getParticipations()) {
            Integer nbOfGiftToDonate = participation.getNbOfGiftToDonate();
            if (nbOfGiftToDonate != null) {
                numberOfGiftsToDonate += nbOfGiftToDonate;
            }
            Integer nbOfGiftToReceive = participation.getNbOfGiftToReceive();
            if (nbOfGiftToReceive != null) {
                numberOfGiftsToReceive += nbOfGiftToReceive;
            }
        }
        if (numberOfGiftsToDonate != numberOfGiftsToReceive) {
            throw new IllegalStateException(String.format("Number of gifts to donate (%s) must equal the number of gifts to receive (%s)", numberOfGiftsToDonate, numberOfGiftsToReceive));
        }
        if (numberOfGiftsToDonate <= 0) {
            throw new IllegalArgumentException("Number of gifts must be superior to 0");
        }
        // We have the same number of gifts excepted to be donated & received, we can process
        // First try to have only distinct participants to offer a gift to
        LinkedHashMap<Participation, LinkedHashSet<Participation>> participationToParticipationToDonateGiftsTo = new LinkedHashMap<>();
        // First every participation can donate to every others
        for (Participation participation : event.getParticipations()) {
            // Only target the ones that want to give gifts
            if (participation.getNbOfGiftToDonate() != null && participation.getNbOfGiftToDonate() > 0) { // Only the participant who want to donate
                for (Participation otherParticipation : event.getParticipations()) {
                    if (
                        !participation.equals(otherParticipation) // One can't draw a gift for oneself
                            && otherParticipation.getNbOfGiftToReceive() != null && otherParticipation.getNbOfGiftToReceive() > 0 // Only target the ones that want to receive gifts
                    ) {
                        participationToParticipationToDonateGiftsTo.computeIfAbsent(participation, participationKey -> new LinkedHashSet<>()).add(otherParticipation);
                    }
                }
            }
        }
        // Now apply the exclusion groups
        for (DrawingExclusionGroup drawingExclusionGroup : event.getDrawingExclusionGroups()) {
            Set<Participation> participations = drawingExclusionGroup.getParticipations();
            for (Participation participation : participations) {
                participationToParticipationToDonateGiftsTo.get(participation).removeAll(participations);
            }
        }
        // Try 500 times to compute a result
        Map<Participation, List<Participation>> participationToDonationList = null;
        for (int i = 0; i < 500 && (participationToDonationList = drawGifts(numberOfGiftsToDonate, participationToParticipationToDonateGiftsTo, false)) == null; i++);
        if (participationToDonationList == null) {
            // Try to draw allowing a single donor to donate multiple times to the same receiver
            for (int i = 0; i < 500 && (participationToDonationList = drawGifts(numberOfGiftsToDonate, participationToParticipationToDonateGiftsTo, true)) == null; i++);
        }
        if (participationToDonationList == null) {
            throw new IllegalStateException("Could not manage to compute a correct gift drawing.");
        } else {
            // Save the result to the database
            // First remove the previous ones
            for (GiftDrawing giftDrawing : event.getGiftDrawings()) {
                giftDrawingRepository.delete(giftDrawing);
            }
            event.getGiftDrawings().clear();
            // Now add the new ones
            for (Map.Entry<Participation, List<Participation>> entry : participationToDonationList.entrySet()) {
                for (Participation receiver : entry.getValue()) {
                    GiftDrawing giftDrawing = new GiftDrawing().donor(entry.getKey()).recipient(receiver);
                    event.addGiftDrawing(giftDrawing);
                    giftDrawingRepository.save(giftDrawing);
                }
            }
        }
    }

    private static class ParticipationContext {
        private Participation participation;
        private LinkedHashSet<Participation> potentialReceivers;
        private List<Participation> receivers;
        private List<Participation> donors;
    }

    private Map<Participation, List<Participation>> drawGifts(int numberOfGiftsToDonate, LinkedHashMap<Participation, LinkedHashSet<Participation>> participationToParticipationToDonateGiftsTo, boolean allowDonateToTheSameReceiver) {
        // Initialize the results
        Random random = new Random();
        Map<Participation, ParticipationContext> participationContexts = new HashMap<>();
        for (Map.Entry<Participation, LinkedHashSet<Participation>> entry : participationToParticipationToDonateGiftsTo.entrySet()) {
            Participation donor = entry.getKey();
            getParticipationContextOrCreate(donor, participationContexts).potentialReceivers = new LinkedHashSet<>(entry.getValue());
        }
        Set<Participation> donors = new HashSet<>(participationContexts.keySet());
        Participation previousReceiver = randomlyPickParticipation(
            donors,
            random);
        for (int i = 0; i < numberOfGiftsToDonate; i++) {
            // Compute who will be the next donor
            if (donors.isEmpty()) {
                return null; // No more donors available
            }
            Participation donor;
            if (donors.contains(previousReceiver)) {
                donor = previousReceiver; // as the previous receiver can donate, let him / her to be the next donor
            } else {
                donor = randomlyPickParticipation(
                    donors,
                    random);
            }
            LinkedHashSet<Participation> potentialReceivers = participationContexts.get(donor).potentialReceivers;
            if (potentialReceivers.isEmpty()) {
                return null;
            }
            Participation receiver = randomlyPickParticipation(potentialReceivers, random);
            if (!allowDonateToTheSameReceiver) {
                potentialReceivers.remove(receiver);
            }
            // Do the donation
            ParticipationContext donorContext = getParticipationContextOrCreate(donor, participationContexts);
            donorContext.receivers.add(receiver);
            ParticipationContext receiverContext = getParticipationContextOrCreate(receiver, participationContexts);
            receiverContext.donors.add(donor);
            // Update the contexts
            if (donorContext.receivers.size() == donor.getNbOfGiftToDonate()) {
                // This donor has donate all his/her gifts, remove him/her
                donors.remove(donor);
            }
            if (receiverContext.donors.size() == receiver.getNbOfGiftToReceive()) {
                // This receiver has received enough, remove him/her
                for (Participation otherDonor : donors) {
                    getParticipationContextOrCreate(otherDonor, participationContexts).potentialReceivers.remove(receiver);
                }
            }
            previousReceiver = receiver;
        }
        // There is one solution, convert it to the expected result format
        Map<Participation, List<Participation>> result = new HashMap<>();
        for (ParticipationContext participationContext : participationContexts.values()) {
            if (!participationContext.receivers.isEmpty()) {
                result.put(participationContext.participation, participationContext.receivers);
            }
        }
        return result;
    }

    private ParticipationContext getParticipationContextOrCreate(Participation participation, Map<Participation, ParticipationContext> participationContexts) {
        return participationContexts.computeIfAbsent(participation, donorKey -> {
            ParticipationContext participationContext = new ParticipationContext();
            participationContext.participation = donorKey;
            participationContext.donors = new ArrayList<>();
            participationContext.receivers = new ArrayList<>();
            return participationContext;
        });
    }

    /**
     * Randomly pick one of the participant thanks to https://stackoverflow.com/a/45982130/535203
      */
    private Participation randomlyPickParticipation(Collection<Participation> participationIterable, Random random) {
        if (participationIterable.size() == 1) {
            return participationIterable.iterator().next();
        } else {
            return participationIterable.stream().skip(random.nextInt(participationIterable.size())).findFirst().get();
        }
    }

    private void checkRequesterUserLoginIsEventAdmin(Event dbEvent, String requesterUserLogin) {
        if (!requesterUserLogin.equals(dbEvent.getAdmin().getLogin())) {
            throw new AccessDeniedException("User is not admin of this event");
        }
    }

    @Transactional(readOnly = true)
    public Optional<Event> findOneByPublicKeyAndPublicKeyEnabledIsTrue(String publicKey) {
        return eventRepository.findOneByPublicKeyAndPublicKeyEnabledIsTrue(publicKey).map(event -> {
            // Only keep public information
            event.setParticipations(event.getParticipations().stream()
                .filter(participation -> participation.getUser() == null) // Only keep participations that have not yet a real user associated with
                .collect(Collectors.toSet())
            );
            return event;
        });
    }
}
