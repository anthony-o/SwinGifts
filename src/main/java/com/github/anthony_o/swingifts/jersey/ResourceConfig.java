package com.github.anthony_o.swingifts.jersey;

import com.github.anthony_o.swingifts.api.PersonResource;
import com.github.anthony_o.swingifts.jaxrs.GenericExceptionMapper;
import com.github.anthony_o.swingifts.util.DbUtils;
import org.apache.commons.lang3.BooleanUtils;

import javax.ws.rs.ApplicationPath;

@ApplicationPath("api")
public class ResourceConfig extends org.glassfish.jersey.server.ResourceConfig {

    public ResourceConfig() {
        packages(PersonResource.class.getPackage().getName());
        packages(GenericExceptionMapper.class.getPackage().getName());

        if (BooleanUtils.isTrue(BooleanUtils.toBooleanObject(System.getProperty("swingifts.createSampleDb")))) {
            try {
                DbUtils.createSampleDbDeleteOnShutdown();
            } catch (Exception e) {
                throw new IllegalStateException("Problem while creating the sample DB");
            }
        }
    }

}
