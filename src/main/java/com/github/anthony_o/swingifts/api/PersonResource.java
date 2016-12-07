package com.github.anthony_o.swingifts.api;

import com.github.anthony_o.swingifts.entity.Person;
import com.github.anthony_o.swingifts.entity.PersonWithWishList;
import com.github.anthony_o.swingifts.service.PersonService;
import com.github.anthony_o.swingifts.service.SessionService;
import com.github.anthony_o.swingifts.util.Base64Utils;
import com.github.anthony_o.swingifts.util.InjectUtils;
import com.github.anthony_o.swingifts.util.ServiceUtils;
import com.github.anthony_o.swingifts.util.SessionUtils;
import org.apache.commons.lang3.BooleanUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import java.util.List;

@Path("/persons")
public class PersonResource {

    @GET
    @DoNotNeedSession
    public List<Person> findWithEventIdAndEventKeyOrderByName(@QueryParam("eventId") long eventId, @QueryParam("eventKey") String eventKey) {
        if (eventKey != null) {
            return getPersonService().findWithEventIdAndEventKeyOrderByName(eventId, Base64Utils.convertFromBase64RFC4648ToBytes(eventKey));
        } else {
            // Here we need a person asker
            Long askerPersonId = SessionUtils.getOrCreateSession().getPersonId();
            if (askerPersonId == null) {
                throw new ForbiddenException("Must be authenticated");
            }
            return getPersonService().findWithEventIdAndAskerPersonIdOrderByName(eventId, askerPersonId);
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @DoNotNeedSession
    public Response createWithPersonAndEventIdAndEventKeyThenCreateSession(Person person, @QueryParam("eventId") Long eventId, @QueryParam("eventKey") String eventKey, @QueryParam("authenticate") Boolean authenticate) throws Exception {
        return ServiceUtils.inTransaction(() -> {
            PersonService personService = getPersonService();
            PersonWithWishList personWithWishList = null;
            Long personId = null;
            if (eventKey != null) {
                personWithWishList = personService.createWithPersonThenCreateWishListWithEventIdAndEventKey(person, eventId, Base64Utils.convertFromBase64RFC4648ToBytes(eventKey));
            } else {
                // We do not have any eventKey, must be authenticated and in that case, be a part of the event
                if (eventId != null) {
                    personWithWishList = personService.createWithPersonThenCreateWishListWithEventIdAndAskerPersonId(person, eventId, SessionUtils.getSessionOrFail().getPersonId());
                } else {
                    // We do not have eventId so it's a user creation without event
                    personId = personService.createWithPerson(person);
                }
            }
            ResponseBuilder responseBuilder = Response.ok();
            if (personWithWishList != null) {
                responseBuilder.entity(personWithWishList);
                personId = personWithWishList.getId();
            } else {
                responseBuilder.entity(personId);
            }
            if (BooleanUtils.isTrue(authenticate)) {
                // the user wants to log in as well as creating its own account
                byte[] token = InjectUtils.getInstance(SessionService.class).createWithPersonReturningToken(person);
                responseBuilder.cookie(SessionUtils.createSessionCookie(personId, token));
            }
            return responseBuilder.build();
        });
    }

    @Path("/{id}")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @DoNotNeedSession
    public Response updateWithPersonAndPersonIdThenCreateSession(Person person, @PathParam("id") final long id, @QueryParam("authenticate") Boolean authenticate) throws Exception {
        if (BooleanUtils.isTrue(authenticate)) {
            return ServiceUtils.inTransaction(() -> {
                PersonService personService = getPersonService();

                return updateWithPersonAndPersonIdThenCreateSession(person, id, personService);
            });
        } else {
            return Response.status(Response.Status.BAD_REQUEST).entity("Must set authenticate to true").build();
        }
    }

    public Response updateWithPersonAndPersonIdThenCreateSession(Person person, Long id, PersonService personService) throws Exception {
        person.setId(id);

        personService.checkOrCreatePasswordThenUpdateWithPerson(person);
        byte[] token = InjectUtils.getInstance(SessionService.class).createWithPersonReturningToken(person);
        person = personService.findOneWithId(person.getId());
        return Response.ok().entity(person).cookie(SessionUtils.createSessionCookie(id, token)).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @DoNotNeedSession
    public Response updateWithPersonAndPersonWithoutIdThenCreateSession(Person person, @QueryParam("authenticate") Boolean authenticate) throws Exception {
        if (BooleanUtils.isTrue(authenticate)) {
            return ServiceUtils.inTransaction(() -> {
                PersonService personService = getPersonService();

                Long id = personService.findOneIdWithLogin(person.getLogin());

                if (id != null) {
                    // The user already exists, let's authenticate him/her
                    return updateWithPersonAndPersonIdThenCreateSession(person, id, personService);
                } else {
                    // TODO support user creation without event
                    return Response.status(Response.Status.BAD_REQUEST).entity("User creation without event is not yet supported").build();
                }
            });
        } else {
            return Response.status(Response.Status.BAD_REQUEST).entity("Must set authenticate to true").build();
        }
    }

    private PersonService getPersonService() {
        return InjectUtils.getInstance(PersonService.class);
    }

    @Path("/{id}")
    @GET
    @DoNotNeedSession
    public Person findWithIdAndSessionToken(@PathParam("id") long id, @QueryParam("sessionToken") String sessionToken) {
        //TODO secure this path in a way that a same IP cannot test multiple id & sessionTokens
        return getPersonService().findWithIdAndSessionToken(id, Base64Utils.convertFromBase64RFC4648ToBytes(sessionToken));
    }

}
