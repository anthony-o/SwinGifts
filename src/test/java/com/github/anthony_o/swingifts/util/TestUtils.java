package com.github.anthony_o.swingifts.util;

import java.nio.file.Path;

public class TestUtils {

    public static Path createSampleDb() throws Exception {
        InjectUtils.reInit(); // Reinit Guice Injector
        return DbUtils.createSampleDb();
    }

}
