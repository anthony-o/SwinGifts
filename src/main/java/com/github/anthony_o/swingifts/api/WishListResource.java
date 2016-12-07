package com.github.anthony_o.swingifts.api;

import com.github.anthony_o.swingifts.entity.WishList;
import com.github.anthony_o.swingifts.service.WishListService;
import com.github.anthony_o.swingifts.util.InjectUtils;
import com.github.anthony_o.swingifts.util.SessionUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/wishLists")
public class WishListResource {

    private WishListService getWishListService() {
        return InjectUtils.getInstance(WishListService.class);
    }


    @GET
    public Response findOneWithEventIdAndPersonIdAndLoadPersonAndCountWishItems(@QueryParam("eventId") long eventId, @QueryParam("personId") Long personId, @QueryParam("loadPerson") Boolean loadPerson, @QueryParam("countWishItems") Boolean countWishItems) {
        if (personId != null) {
            // A person is asked, so we will return a single wishList for that person and load its wishItems & reservations
            return Response.ok(
                    getWishListService().findOneWithEventIdAndPersonIdAndAskerPersonIdLoadingWishItemsAndReservations(eventId, personId, SessionUtils.getSessionOrFail().getPersonId())
            ).build();
        } else {
            // No specific person asked, we will return all the wishLists for that event, loading its person & count its wishItems
            return Response.ok(
                    getWishListService().findWithEventIdAndAskerPersonIdLoadingPersonAndCountingWishItemsOrderByPersonName(eventId, SessionUtils.getSessionOrFail().getPersonId())
            ).build();
        }
    }

    @POST
    @Path("/{id}/readCircleGiftTargetPersonId")
    public void readCircleGiftTargetPersonIdWithId(@PathParam("id") long id) {
        getWishListService().readCircleGiftTargetPersonIdWithIdAndAskerPersonId(id, SessionUtils.getSessionOrFail().getPersonId());
    }

    @PUT
    @Path("/{id}")
    public void updateWithWishListAndId(WishList wishList, @PathParam("id") long id) {
        wishList.setId(id);
        getWishListService().updateWithWishListAndAskerPersonId(wishList, SessionUtils.getSessionOrFail().getPersonId());
    }
}
