package com.github.anthony_o.swingifts.service.dao.mapper;

import com.github.anthony_o.swingifts.entity.WishList;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class WishListMapper implements ResultSetMapper<WishList> {

    @Override
    public WishList map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        WishList wishList = new WishList();
        ResultSetMetaData metaData = r.getMetaData();
        int columnCount = metaData.getColumnCount();
        for (int i = 1 ; i <= columnCount; i++) { // iterate on column names thanks to http://stackoverflow.com/a/3599960/535203
            switch (metaData.getColumnLabel(i)) {
                case "ID":
                    wishList.setId(r.getObject(i, Long.class));
                    break;
                case "EVENT_ID":
                    wishList.setEventId(r.getObject(i, Long.class));
                    break;
                case "PERSON_ID":
                    wishList.setPersonId(r.getObject(i, Long.class));
                    break;
                case "IS_PERSON_EVENT_ADMIN":
                    wishList.setIsPersonEventAdmin(r.getObject(i, Boolean.class));
                    break;
                case "IS_PERSON_PARTICIPATES_IN_CIRCLE_GIFT":
                    wishList.setIsPersonParticipatesInCircleGift(r.getObject(i, Boolean.class));
                    break;
                case "CIRCLE_GIFT_TARGET_PERSON_ID":
                    wishList.setCircleGiftTargetPersonId(r.getObject(i, Long.class));
                    break;
                case "IS_CIRCLE_GIFT_TARGET_PERSON_ID_READ":
                    wishList.setIsCircleGiftTargetPersonIdRead(r.getObject(i, Boolean.class));
                    break;
            }
        }
        return wishList;
    }
}
