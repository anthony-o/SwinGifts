package com.github.anthony_o.swingifts.util;

import com.github.anthony_o.swingifts.service.dao.WishItemDao;
import com.github.anthony_o.swingifts.service.dao.WishListDao;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.TransactionStatus;
import org.skife.jdbi.v2.exceptions.CallbackFailedException;

import javax.ws.rs.ForbiddenException;

public class ServiceUtils {
    private static ThreadLocal<TransactionHolder> transactionHolders = new ThreadLocal<>();

    public interface TransactionCallback<ReturnType> {
        ReturnType inTransaction() throws Exception;
    }

    private static class TransactionHolder {
        Handle handle;
        TransactionStatus transactionStatus;

        public TransactionHolder(Handle handle, TransactionStatus transactionStatus) {
            this.handle = handle;
            this.transactionStatus = transactionStatus;
        }
    }

    public static <ReturnType> ReturnType inTransaction(TransactionCallback<ReturnType> callback) throws Exception {
        TransactionHolder transactionHolder = transactionHolders.get();
        if (transactionHolder == null) {
            // create new transaction
            try {
                return getDBI().inTransaction((handle, transactionStatus) -> {
                    try {
                        transactionHolders.set(new TransactionHolder(handle, transactionStatus));
                        return callback.inTransaction();
                    } finally {
                        transactionHolders.remove();
                    }
                });
            } catch (CallbackFailedException e) {
                throw (Exception) e.getCause();
            }
        } else {
            // already in transaction, let's just call the callback
            return callback.inTransaction();
        }
    }


    private static WishListDao getWishListDao() {
        return attachIfTransactionElseOnDemand(WishListDao.class);
    }

    private static WishItemDao getWishItemDao() {
        return attachIfTransactionElseOnDemand(WishItemDao.class);
    }


    public static DBI getDBI() {
        return InjectUtils.getInstance(DBI.class);
    }

    public static <SqlObjectType> SqlObjectType attachIfTransactionElseOnDemand(Class<SqlObjectType> sqlObjectType) {
        TransactionHolder transactionHolder = transactionHolders.get();
        if (transactionHolder == null) {
            return getDBI().onDemand(sqlObjectType);
        } else {
            return transactionHolder.handle.attach(sqlObjectType);
        }
    }

    public static void checkThatAskerIsInEventWithEventIdAndAskerPersonId(long eventId, long askerPersonId) throws ForbiddenException {
        checkThatAskerIsInEventWithEventIdAndAskerPersonId(eventId, askerPersonId, getWishListDao());
    }

    public static void checkThatAskerIsInEventWithEventIdAndAskerPersonId(long eventId, long askerPersonId, WishListDao wishListDao) throws ForbiddenException {
        if (wishListDao.countWithEventIdAndPersonId(eventId, askerPersonId) < 1) {
            throw new ForbiddenException("Asker must be in the event.");
        }
    }

    public static void checkThatAskerIsInEventAndDoesntOwnTheWishItemSWishListOrOwnsWishItemWithWishItemIdAndAskerPersonId(long wishItemId, long askerPersonId, WishItemDao wishItemDao) throws ForbiddenException {
        if (wishItemDao.countAskerIsInEventAndDoesntOwnTheWishItemSWishListOrOwnsWishItemWithIdAndAskerPersonId(wishItemId, askerPersonId) < 1) {
            throw new ForbiddenException("Asker must be in the same event of the wishItem owner or must be the owner, but not the owner of the wishList.");
        }
    }

    public static void checkThatAskerIsInEventWithWishListIdAndAskerPersonId(Long wishListId, long askerPersonId, WishListDao wishListDao) {
        if (wishListDao.countWithIdAndAskerPersonId(wishListId, askerPersonId) < 1) {
            throw new ForbiddenException("Asker must be in the event.");
        }
    }

    public static void checkThatAskerIsAdminWithEventIdAndAskerPersonId(long eventId, long askerPersonId, WishListDao wishListDao) {
        if (wishListDao.countAdminWithEventIdAndPersonId(eventId, askerPersonId) < 1) {
            throw new ForbiddenException("Asker must be admin.");
        }
    }

    public static void checkThatAskerIsInEventOrOwnsWishItemOrShareEventWithWishItemOwnerWithWishItemIdAndAskerPersonId(long wishItemId, long askerPersonId) {
        if (getWishItemDao().countAskerIsInEventOrOwnsWishItemOrShareEventWithWishItemOwnerWithIdAndAskerPersonId(wishItemId, askerPersonId) < 1) {
            throw new ForbiddenException("Asker must be in the same event or share an event with the wishItem owner or must be the owner.");
        }
    }

    public static void checkThatAskerIsInEventOrShareEventWithWishItemOwnerWithWishItemIdAndAskerPersonId(long wishItemId, long askerPersonId) {
        if (getWishItemDao().countAskerIsInEventOrShareEventWithWishItemOwnerWithIdAndAskerPersonId(wishItemId, askerPersonId) < 1) {
            throw new ForbiddenException("Asker must be in the event or share an event with the wishItem owner.");
        }
    }
}
