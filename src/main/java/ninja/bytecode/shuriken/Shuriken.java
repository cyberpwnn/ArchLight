/*
 * Copyright (c) 2021.
 * Arcane Arts Inc. All rights reserved.
 * Proprietary. Do not distribute outside MPower Me LLC or Arcane Arts Inc.
 */

package ninja.bytecode.shuriken;

import ninja.bytecode.shuriken.bench.Profiler;
import ninja.bytecode.shuriken.service.IService;

import java.lang.reflect.InvocationTargetException;

public class Shuriken {
    public static String DIR = System.getenv("APPDATA") + "/Shuriken";
    public static final Profiler profiler = new Profiler();
    public static final ServiceManager serviceManager = new ServiceManager();

    public static void main(String[] a) throws Throwable {

    }

    public static <T extends IService> T getService(Class<? extends T> c) {
        try {
            return serviceManager.getService(c);
        } catch(InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
        }

        return null;
    }
}
