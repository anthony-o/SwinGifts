package com.github.anthony_o.swingifts.api;

import com.github.anthony_o.swingifts.entity.Reservation;
import com.github.anthony_o.swingifts.service.ReservationService;
import com.github.anthony_o.swingifts.util.InjectUtils;
import com.github.anthony_o.swingifts.util.SessionUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/reservations")
public class ReservationResource {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public long createWithReservation(Reservation reservation) {
        return InjectUtils.getInstance(ReservationService.class).createWithReservationAndAskerPersonId(reservation, SessionUtils.getSessionOrFail().getPersonId());
    }

    @DELETE
    @Path("/{id}")
    public void delete(@PathParam("id") long id) {
        InjectUtils.getInstance(ReservationService.class).deleteWithIdAndAskerPersonId(id, SessionUtils.getSessionOrFail().getPersonId());
    }
}
