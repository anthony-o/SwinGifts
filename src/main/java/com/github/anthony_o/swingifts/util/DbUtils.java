package com.github.anthony_o.swingifts.util;

import com.github.anthony_o.swingifts.guice.DatabaseModule;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;

public class DbUtils {
    private static Logger LOG = LoggerFactory.getLogger(DbUtils.class);

    public static Path createSampleDbDeleteOnShutdown() throws Exception {
        final Path sampleDbPath = createSampleDb();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                deleteSampleDb(sampleDbPath);
            } catch (IOException | SQLException e) {
                throw new IllegalStateException("Problem while deleting recursively the sample db directory", e);
            }
        }));
        return sampleDbPath;
    }

    public static void deleteSampleDb(Path sampleDbPath) throws IOException, SQLException {
        InjectUtils.getInstance(Connection.class).createStatement().execute("SHUTDOWN"); // first close all the H2 connections in order to release the handles to files in the created directory thanks to http://stackoverflow.com/a/40762283/535203
        //InjectUtils.getInstance(JdbcConnectionPool.class).dispose(); // this doesn't work because only a SHUTDOWN or a System exist will correctly do the things http://stackoverflow.com/a/9972725/535203
        // ServiceUtils.getDBI().useHandle((handle) -> handle.execute("SHUTDOWN")); // this doesn't work neither because JDBI tries to retrieve data, and H2 fails with "JdbcSQLException: Database is already closed"
        FileUtils.deleteDirectory(sampleDbPath.toFile());
    }

    public static Path createSampleDb() throws Exception {
        Path tempDirectory = Files.createTempDirectory("swingifts.sample.db.");
        DatabaseModule.setDbPath(tempDirectory.resolve("sampledb"));
        ServiceUtils.getDBI().inTransaction((handle, transactionStatus) -> {
            handle.execute("INSERT INTO EVENT (ID, NAME, KEY, IS_OPENED, IS_CIRCLE_GIFT_LAUNCHED_ONCE) VALUES (1, 'First event', X'000000000000000000000000000000000000000000000000000000000000000000', true, true)");
            handle.execute("INSERT INTO PERSON (ID, NAME, LOGIN, PASSWORD_HASH, SALT) VALUES (1, 'Alice', 'alice', X'C053670FA6996F43F50BF9B4CDED00ABBEA35DE6145C46561DA9682BA8BC735563FDB758FD787F6C66B80C00E39AB6192EE941D3DF9B8B4FB341BB13CF59CF9EC27F', X'000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000')");
            handle.execute("INSERT INTO PERSON (ID, NAME) VALUES (2, 'Bob')");
            handle.execute("INSERT INTO PERSON (ID, NAME, LOGIN, PASSWORD_HASH, SALT) VALUES (3, 'Charlie', 'charlie', X'C053670FA6996F43F50BF9B4CDED00ABBEA35DE6145C46561DA9682BA8BC735563FDB758FD787F6C66B80C00E39AB6192EE941D3DF9B8B4FB341BB13CF59CF9EC27F', X'000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000')");
            handle.execute("INSERT INTO PERSON (ID, NAME) VALUES (4, 'Dave')");
            handle.execute("INSERT INTO WISH_LIST (ID, PERSON_ID, EVENT_ID, IS_PERSON_EVENT_ADMIN, IS_PERSON_PARTICIPATES_IN_CIRCLE_GIFT, CIRCLE_GIFT_TARGET_PERSON_ID) VALUES (1, 1, 1, true, true, 2)");
            handle.execute("INSERT INTO WISH_LIST (ID, PERSON_ID, EVENT_ID, IS_PERSON_PARTICIPATES_IN_CIRCLE_GIFT, CIRCLE_GIFT_TARGET_PERSON_ID) VALUES (2, 2, 1, true, 1)");
            handle.execute("INSERT INTO WISH_ITEM (ID, WISH_LIST_ID, CREATED_BY_PERSON_ID, NAME, URL) VALUES (1, 1, 2, 'Death star', 'https://shop.lego.com/en-US/Death-Star-75159')");
            handle.execute("INSERT INTO WISH_ITEM (ID, WISH_LIST_ID, CREATED_BY_PERSON_ID, NAME) VALUES (2, 1, 2, 'Barby plain')");
            handle.execute("INSERT INTO WISH_ITEM (ID, WISH_LIST_ID, CREATED_BY_PERSON_ID, NAME, URL) VALUES (3, 2, 1, 'Roll for the Galaxy', 'https://www.boardgamegeek.com/boardgame/132531/roll-galaxy')");
            handle.execute("INSERT INTO WISH_ITEM (ID, PERSON_ID, NAME) VALUES (4, 1, 'Test personal item 1')");
            handle.execute("INSERT INTO WISH_ITEM (ID, PERSON_ID, NAME) VALUES (5, 1, 'Test personal item 2')");
            handle.execute("INSERT INTO WISH_ITEM (ID, PERSON_ID, NAME) VALUES (6, 2, 'Test personal item 1 for person Bob')");
            handle.execute("INSERT INTO WISH_ITEM (ID, PERSON_ID, NAME) VALUES (7, 2, 'Test personal item 2 for person Bob')");
            handle.execute("INSERT INTO RESERVATION (ID, PERSON_ID, WISH_ITEM_ID) VALUES (1, 2, 2)");
            handle.execute("INSERT INTO RESERVATION (ID, PERSON_ID, WISH_ITEM_ID) VALUES (2, 2, 4)");
            handle.execute("INSERT INTO RESERVATION (ID, PERSON_ID, WISH_ITEM_ID) VALUES (3, 1, 7)");
            handle.execute("INSERT INTO EVENT (ID, NAME, KEY, IS_OPENED) VALUES (2, 'Bob''s event', X'000000000000000000000000000000000000000000000000000000000000000001', false)");
            handle.execute("INSERT INTO WISH_LIST (ID, PERSON_ID, EVENT_ID, IS_PERSON_EVENT_ADMIN) VALUES (3, 2, 2, true)");
            handle.execute("INSERT INTO WISH_LIST (ID, PERSON_ID, EVENT_ID) VALUES (4, 1, 2)");
            handle.execute("INSERT INTO EVENT (ID, NAME, KEY) VALUES (3, 'Charlie''s event', X'000000000000000000000000000000000000000000000000000000000000000002')");
            handle.execute("INSERT INTO WISH_LIST (ID, PERSON_ID, EVENT_ID, IS_PERSON_EVENT_ADMIN, IS_PERSON_PARTICIPATES_IN_CIRCLE_GIFT) VALUES (5, 3, 3, true, true)");
            handle.execute("INSERT INTO WISH_LIST (ID, PERSON_ID, EVENT_ID, IS_PERSON_PARTICIPATES_IN_CIRCLE_GIFT) VALUES (6, 1, 3, false)");
            handle.execute("INSERT INTO WISH_LIST (ID, PERSON_ID, EVENT_ID, IS_PERSON_PARTICIPATES_IN_CIRCLE_GIFT) VALUES (7, 2, 3, true)");
            handle.execute("INSERT INTO WISH_LIST (ID, PERSON_ID, EVENT_ID, IS_PERSON_PARTICIPATES_IN_CIRCLE_GIFT) VALUES (8, 4, 3, true)");
            handle.execute("INSERT INTO PERSON (ID, NAME) VALUES (5, 'Eve')");
            handle.execute("INSERT INTO EVENT (ID, NAME, KEY) VALUES (4, 'Eve''s event', X'000000000000000000000000000000000000000000000000000000000000000003')");
            handle.execute("INSERT INTO WISH_LIST (ID, PERSON_ID, EVENT_ID, IS_PERSON_EVENT_ADMIN) VALUES (9, 5, 4, true)");
            handle.execute("INSERT INTO WISH_LIST (ID, PERSON_ID, EVENT_ID) VALUES (10, 3, 4)");
            return null;
        });
        LOG.info("Sample DB created at {}", tempDirectory);
        return tempDirectory;
    }

}
