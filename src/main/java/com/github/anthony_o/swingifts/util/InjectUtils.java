package com.github.anthony_o.swingifts.util;

import com.github.anthony_o.swingifts.guice.DatabaseModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class InjectUtils {
    protected static Injector injector;

    public static <T> T getInstance(Class<T> klass) {
        return getInjector().getInstance(klass);
    }

    private static Injector getInjector() {
        if (injector == null) {
            synchronized (InjectUtils.class) {
                if (injector == null) {
                    injector = Guice.createInjector(new DatabaseModule());
                }
            }
        }
        return injector;
    }

    protected static void reInit() {
        injector = null;
    }
}
