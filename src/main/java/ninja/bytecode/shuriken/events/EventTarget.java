/*
 * Copyright (c) 2021.
 * Arcane Arts Inc. All rights reserved.
 * Proprietary. Do not distribute outside MPower Me LLC or Arcane Arts Inc.
 */

package ninja.bytecode.shuriken.events;

import lombok.Data;

import java.lang.reflect.Method;

@Data
public class EventTarget {
    private Object instance;
    private Method method;

    public EventTarget(Object instance, Method method) {
        this.instance = instance;
        this.method = method;
    }

    public void invoke(Event event) throws Throwable {
        method.invoke(instance, event);
    }
}
