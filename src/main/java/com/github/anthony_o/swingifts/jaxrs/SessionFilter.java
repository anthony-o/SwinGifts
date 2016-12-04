package com.github.anthony_o.swingifts.jaxrs;

import com.github.anthony_o.swingifts.api.DoNotNeedSession;
import com.github.anthony_o.swingifts.service.SessionService;
import com.github.anthony_o.swingifts.util.Base64Utils;
import com.github.anthony_o.swingifts.util.InjectUtils;
import com.github.anthony_o.swingifts.util.SessionUtils;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

public class SessionFilter implements ContainerRequestFilter  {

    @Context
    private ResourceInfo resourceInfo;

    @Provider
    public static class SessionFilterDynamicFeature implements DynamicFeature {
        @Override
        public void configure(ResourceInfo resourceInfo, FeatureContext context) {
            String thisPackage = getClass().getPackage().getName();
            int lastIndexOfDot = thisPackage.lastIndexOf('.');
            String parentPackage = thisPackage.substring(0, lastIndexOfDot);
            if (resourceInfo.getResourceClass().getPackage().getName().startsWith(parentPackage)) {
                context.register(SessionFilter.class);
            }
        }
    }


    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        Cookie cookie = requestContext.getCookies().get(SessionUtils.SESSION_COOKIE_NAME);
        boolean needSession = resourceInfo.getResourceMethod().getAnnotation(DoNotNeedSession.class) == null;
        if (cookie == null) {
            if (needSession) {
                // only fails if cookie session is missing
                throw new NotAuthorizedException("Missing the session cookie");
            }
        } else {
            String[] sessionCookieParts = cookie.getValue().split(":");
            long personId = Long.parseLong(sessionCookieParts[0]);
            byte[] token = Base64Utils.convertFromBase64RFC4648ToBytes(sessionCookieParts[1]);
            if (InjectUtils.getInstance(SessionService.class).touchSession(personId, token)) {
                SessionUtils.getOrCreateSession().setPersonId(personId);
            } else if(needSession) {
                throw new ForbiddenException("Session does not exist");
            }
        }
    }

    /*
    @Override
    public Object aroundReadFrom(ReaderInterceptorContext context) throws IOException, WebApplicationException {
        Annotation[] annotations = context.getAnnotations();
        boolean needSession = true;
        if (annotations != null) {
            for (Annotation annotation : annotations) {
                if (annotation instanceof DoNotNeedSession) {
                    needSession = false;
                    break;
                }
            }
        }
        if (needSession) {
            context.
        }
        return null;
    }
    */
}
