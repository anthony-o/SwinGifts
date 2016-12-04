package com.github.anthony_o.swingifts.api;

import com.github.anthony_o.swingifts.entity.WishList;
import com.github.anthony_o.swingifts.service.WishListService;
import com.github.anthony_o.swingifts.util.InjectUtils;
import com.github.anthony_o.swingifts.util.SessionUtils;

import javax.ws.rs.*;

@Path("/wishLists")
public class WishListResource {

    private WishListService getWishListService() {
        return InjectUtils.getInstance(WishListService.class);
    }


    @GET
    public WishList findOneWithEventIdAndPersonIdLoadingWishItems(@QueryParam("eventId") long eventId, @QueryParam("personId") long personId) {
        return getWishListService().findOneWithEventIdAndPersonIdAndAskerPersonIdLoadingWishItemsAndReservations(eventId, personId, SessionUtils.getSessionOrFail().getPersonId());
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
