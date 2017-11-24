package com.github.anthony_o.swingifts.service.dao.mapper;

import com.github.anthony_o.swingifts.entity.WishItem;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class WishItemMapper implements ResultSetMapper<WishItem> {

    @Override
    public WishItem map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        WishItem wishItem = new WishItem();
        ResultSetMetaData metaData = r.getMetaData();
        int columnCount = metaData.getColumnCount();
        for (int i = 1 ; i <= columnCount; i++) { // iterate on column names thanks to http://stackoverflow.com/a/3599960/535203
            switch (metaData.getColumnLabel(i)) {
                case "ID":
                    wishItem.setId(r.getObject(i, Long.class));
                    break;
                case "NAME":
                    wishItem.setName(r.getString(i));
                    break;
                case "URL":
                    wishItem.setUrl(r.getString(i));
                    break;
                case "WISH_LIST_ID":
                    wishItem.setWishListId(r.getObject(i, Long.class));
                    break;
                case "PERSON_ID":
                    wishItem.setPersonId(r.getObject(i, Long.class));
                    break;
                case "CREATED_BY_PERSON_ID":
                    wishItem.setCreatedByPersonId(r.getObject(i, Long.class));
                    break;
                case "CREATION_DATE":
                    wishItem.setCreationDate(r.getTimestamp(i));
                    break;
                case "MODIFICATION_DATE":
                    wishItem.setModificationDate(r.getTimestamp(i));
                    break;
            }
        }
        return wishItem;
    }
}
