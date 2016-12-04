package com.github.anthony_o.swingifts.service.dao.mapper;

import com.github.anthony_o.swingifts.entity.Reservation;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class ReservationMapper implements ResultSetMapper<Reservation> {

    @Override
    public Reservation map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        Reservation reservation = new Reservation();
        ResultSetMetaData metaData = r.getMetaData();
        int columnCount = metaData.getColumnCount();
        for (int i = 1 ; i <= columnCount; i++) { // iterate on column names thanks to http://stackoverflow.com/a/3599960/535203
            switch (metaData.getColumnLabel(i)) {
                case "ID":
                    reservation.setId(r.getObject(i, Long.class));
                    break;
                case "PERSON_ID":
                    Long personId = r.getObject(i, Long.class);
                    reservation.setPersonId(personId);
                    break;
            }
        }
        return reservation;
    }
}
