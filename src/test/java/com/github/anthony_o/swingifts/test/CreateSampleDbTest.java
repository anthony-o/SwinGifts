package com.github.anthony_o.swingifts.test;

import com.github.anthony_o.swingifts.util.Base64Utils;
import com.github.anthony_o.swingifts.util.DbUtils;
import com.github.anthony_o.swingifts.util.SessionUtils;
import com.github.anthony_o.swingifts.util.TestUtils;
import org.junit.After;
import org.junit.Before;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;

public abstract class CreateSampleDbTest {
    private Path sampleDbPath;

    protected long firstEventId = 1;
    protected String firstEventKeyBase64RFC4648 = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    protected byte[] firstEventKey = Base64Utils.convertFromBase64RFC4648ToBytes(firstEventKeyBase64RFC4648);
    protected long alicePersonId = 1;
    protected long bobPersonId = 2;
    protected long charliePersonId = 3;
    protected long aliceWishListId = 1;
    protected long bobWishListId = 2;
    protected long deathStarForAliceWishItemId = 1;
    protected long barbyForAliceReservedByBobReservationId = 1;
    protected long testPersonalWishItem1ForAliceWishItemId = 4;
    protected long testPersonalWishItem1ForBobWishItemId = 6;
    protected long testPersonalWishItem2ForBobWishItemId = 7;
    protected long bobSEventId = 2;
    protected String bobSEventKeyBase64RFC4648 = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAB";
    protected byte[] bobSEventKey = Base64Utils.convertFromBase64RFC4648ToBytes(bobSEventKeyBase64RFC4648);
    protected long charlieSEventId = 3;

    protected int numberOfEventsThatAliceIsIn = 3;
    protected int numberOfEventsThatBobIsIn = 3;

    protected int numberOfPersonsInBobSEvent = 2;
    protected int numberOfPersonsInFirstEvent = 2;
    protected int numberOfWishItemsInAliceSWishListInFirstEventVisibleByAlice = 2;
    protected int numberOfWishItemsInAliceSWishListInFirstEventVisibleByBob = 4;

    protected long aliceWishListIdInCharlieSEvent = 6;

    protected long daveWishListIdInCharliSEvent = 8;
    protected long davePersonId = 4;

    @Before
    public void setUp() throws Exception {
        sampleDbPath = TestUtils.createSampleDb();
    }

    @After
    public void tearDown() throws IOException, SQLException {
        DbUtils.deleteSampleDb(sampleDbPath);
        SessionUtils.clearSession();
    }
}
