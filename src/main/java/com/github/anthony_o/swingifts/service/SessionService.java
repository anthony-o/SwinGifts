package com.github.anthony_o.swingifts.service;

import com.github.anthony_o.swingifts.entity.Person;
import com.github.anthony_o.swingifts.service.dao.SessionDao;
import com.github.anthony_o.swingifts.util.ServiceUtils;
import com.google.inject.Singleton;

import javax.ws.rs.ForbiddenException;
import java.security.SecureRandom;

@Singleton
public class SessionService {
    public static final int TOKEN_LENGTH = 66;

    public SessionDao getSessionDao() {
        return ServiceUtils.attachIfTransactionElseOnDemand(SessionDao.class);
    }


    public byte[] createWithPersonIdReturningToken(long personId) throws Exception {
        return ServiceUtils.inTransaction(() -> {
            byte[] token = new byte[TOKEN_LENGTH];
            new SecureRandom().nextBytes(token); // not using getInstanceStrong() because it was too long and useless according to https://tersesystems.com/2015/12/17/the-right-way-to-use-securerandom/
            SessionDao sessionDao = getSessionDao();
            sessionDao.createWithPersonIdAndToken(personId, token);
            sessionDao.deleteOldWithPersonId(personId);
            return token;
        });
    }

    public boolean touchSession(long personId, byte[] token) throws ForbiddenException {
        return getSessionDao().updateLastUseWithPersonIdAndToken(personId, token) == 1;
    }

    public void deleteWithPersonIdAndToken(long personId, byte[] token) {
        getSessionDao().deleteWithPersonIdAndToken(personId, token);
    }
}
