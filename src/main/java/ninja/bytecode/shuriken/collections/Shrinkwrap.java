/*
 * Copyright (c) 2021.
 * Arcane Arts Inc. All rights reserved.
 * Proprietary. Do not distribute outside MPower Me LLC or Arcane Arts Inc.
 */

package ninja.bytecode.shuriken.collections;

public class Shrinkwrap<T> {
    private T t;

    public Shrinkwrap(T t) {
        set(t);
    }

    public Shrinkwrap() {
        this(null);
    }

    public T get() {
        return t;
    }

    public void set(T t) {
        this.t = t;
    }
}
