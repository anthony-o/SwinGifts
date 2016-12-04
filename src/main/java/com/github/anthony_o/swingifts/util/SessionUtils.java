package com.github.anthony_o.swingifts.util;

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.core.NewCookie;

public class SessionUtils {
    public static final String SESSION_COOKIE_NAME = "SWINGIFTS_TOKEN";

    private static ThreadLocal<Session> sessions = new ThreadLocal<>();

    public static class Session {
        private Long personId;

        public Long getPersonId() {
            return personId;
        }

        public void setPersonId(Long personId) {
            this.personId = personId;
        }
    }

    public static Session getSession() {
        return sessions.get();
    }

    public static Session getSessionOrFail() {
        Session session = sessions.get();
        if (session == null) {
            throw new NotAuthorizedException("Must be authenticated.");
        }
        return session;
    }

    public static Session getOrCreateSession() {
        Session session = sessions.get();
        if (session == null) {
            session = new Session();
            sessions.set(session);
        }
        return session;
    }

    public static NewCookie createSessionCookie(long personId, byte[] token) {
        // We will set the cookie to / so that the javascript scripts can retrieve them (by default, they were set to "/api/" and could be retrieved from app.js)
        return new NewCookie(SESSION_COOKIE_NAME, personId + ":" + Base64Utils.convertFromBytesToBase64RFC4648(token), "/", null, null, NewCookie.DEFAULT_MAX_AGE, false);
    }
}
