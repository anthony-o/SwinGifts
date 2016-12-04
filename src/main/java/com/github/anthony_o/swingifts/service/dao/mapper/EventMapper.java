package com.github.anthony_o.swingifts.service.dao.mapper;

import com.github.anthony_o.swingifts.entity.Event;
import com.github.anthony_o.swingifts.util.Base64Utils;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class EventMapper implements ResultSetMapper<Event> {
    @Override
    public Event map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        Event event = new Event();
        ResultSetMetaData metaData = r.getMetaData();
        int columnCount = metaData.getColumnCount();
        for (int i = 1 ; i <= columnCount; i++) { // iterate on column names thanks to http://stackoverflow.com/a/3599960/535203
            switch (metaData.getColumnLabel(i)) {
                case "ID":
                    event.setId(r.getObject(i, Long.class));
                    break;
                case "NAME":
                    event.setName(r.getString(i));
                    break;
                case "KEY":
                    event.setKey(Base64Utils.convertFromBytesToBase64RFC4648(r.getBytes(i)));
                    break;
                case "IS_OPENED":
                    event.setIsOpened(r.getObject(i, Boolean.class));
                    break;
                case "IS_CIRCLE_GIFT_LAUNCHED_ONCE":
                    event.setIsCircleGiftLaunchedOnce(r.getObject(i, Boolean.class));
                    break;
            }
        }
        return event;
    }
}
