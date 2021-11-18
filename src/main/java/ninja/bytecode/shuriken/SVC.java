/*
 * Copyright (c) 2021.
 * Arcane Arts Inc. All rights reserved.
 * Proprietary. Do not distribute outside MPower Me LLC or Arcane Arts Inc.
 */

package ninja.bytecode.shuriken;

import ninja.bytecode.shuriken.service.IService;

public class SVC {
    public static <T extends IService> T get(Class<? extends T> c) {
        return Shuriken.getService(c);
    }
}
