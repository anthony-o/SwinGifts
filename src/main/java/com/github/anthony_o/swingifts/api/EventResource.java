package com.github.anthony_o.swingifts.api;

import com.github.anthony_o.swingifts.entity.Event;
import com.github.anthony_o.swingifts.entity.WishList;
import com.github.anthony_o.swingifts.service.EventService;
import com.github.anthony_o.swingifts.util.Base64Utils;
import com.github.anthony_o.swingifts.util.InjectUtils;
import com.github.anthony_o.swingifts.util.SessionUtils;

import javax.ws.rs.*;
import java.util.List;

@Path("/events")
public class EventResource {

    @GET
    public List<Event> find() {
        EventService eventService = getEventService();
        return eventService.findWithAskerPersonId(SessionUtils.getSessionOrFail().getPersonId());
    }

    @GET
    @Path("/{id}")
    @DoNotNeedSession
    public Event findOneWithIdAndKey(@PathParam("id") long id, @QueryParam("key") String key) {
        EventService eventService = getEventService();
        if (key != null) {
            return eventService.findOneWithIdAndKey(id, Base64Utils.convertFromBase64RFC4648ToBytes(key));
        } else {
            // Here we need a person asker
            return eventService.findOneWithIdAndKeyAndAskerPersonId(id, SessionUtils.getSessionOrFail().getPersonId());
        }
    }

    @POST
    public Event createWithEvent(Event event) throws Exception {
        EventService eventService = getEventService();
        return eventService.createThenCreateAdminWishListWithEventAndAskerPersonId(event, SessionUtils.getSessionOrFail().getPersonId());
    }

    @POST
    @Path("/{id}/launchCircleGift")
    public WishList launchCircleGiftWithId(@PathParam("id") long id) throws Exception {
        return getEventService().launchCircleGiftWithIdAndAskerPersonIdReturningAskersWishList(id, SessionUtils.getSessionOrFail().getPersonId());
    }

    private EventService getEventService() {
        return InjectUtils.getInstance(EventService.class);
    }
}
