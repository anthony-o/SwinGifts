package com.github.anthony_o.swingifts.api;

import com.github.anthony_o.swingifts.entity.WishItem;
import com.github.anthony_o.swingifts.service.WishItemService;
import com.github.anthony_o.swingifts.util.InjectUtils;
import com.github.anthony_o.swingifts.util.SessionUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/wishItems")
public class WishItemResource {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public long createWithWishItem(WishItem wishItem) {
        return InjectUtils.getInstance(WishItemService.class).createWithWishItemAndAskerPersonId(wishItem, SessionUtils.getSessionOrFail().getPersonId());
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void createWithIdAndWishItem(@PathParam("id") long id, WishItem wishItem) throws Exception {
        Long wishItemId = wishItem.getId();
        if (wishItemId == null || wishItemId != id) {
            wishItem.setId(id);
        }
        InjectUtils.getInstance(WishItemService.class).updateWithWishItemAndAskerPersonId(wishItem, SessionUtils.getSessionOrFail().getPersonId());
    }

    @GET
    @Path("/{id}")
    public WishItem findWithId(@PathParam("id") long id) {
        return InjectUtils.getInstance(WishItemService.class).findWithIdAndAskerPersonIdLoadingReservations(id, SessionUtils.getSessionOrFail().getPersonId());
    }

    @DELETE
    @Path("/{id}")
    public void deleteWithId(@PathParam("id") long id) throws Exception {
        InjectUtils.getInstance(WishItemService.class).deleteWithIdAndAskerPersonId(id, SessionUtils.getSessionOrFail().getPersonId());
    }
}
