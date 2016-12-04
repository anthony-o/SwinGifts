package com.github.anthony_o.swingifts.jaxrs;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {
    @Override
    public Response toResponse(Throwable exception) {
        exception.printStackTrace(); //TODO handle this better with Exception UUID sent to the client and logged correctly on the server side
        if (exception instanceof WebApplicationException) {
            return ((WebApplicationException) exception).getResponse();
        } else {
            return new InternalServerErrorException(exception).getResponse();
        }
    }
}
