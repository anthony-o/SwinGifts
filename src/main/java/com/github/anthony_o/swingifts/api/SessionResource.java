package com.github.anthony_o.swingifts.api;

import com.github.anthony_o.swingifts.service.SessionService;
import com.github.anthony_o.swingifts.util.Base64Utils;
import com.github.anthony_o.swingifts.util.InjectUtils;

import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

@Path("/sessions")
public class SessionResource {

    @DELETE
    @DoNotNeedSession
    public void deleteWithPersonIdAndToken(@QueryParam("personId") long personId, @QueryParam("token") String token) {
        InjectUtils.getInstance(SessionService.class).deleteWithPersonIdAndToken(personId, Base64Utils.convertFromBase64RFC4648ToBytes(token));
    }

}
