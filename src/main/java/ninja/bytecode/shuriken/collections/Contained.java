/*
 * Copyright (c) 2021.
 * Arcane Arts Inc. All rights reserved.
 * Proprietary. Do not distribute outside MPower Me LLC or Arcane Arts Inc.
 */

package ninja.bytecode.shuriken.collections;

import java.util.function.Function;

public class Contained<T> {
    private T t;

    public Contained(T t) {
        set(t);
    }

    public Contained() {
        this(null);
    }

    public void mod(Function<T, T> x) {
        set(x.apply(t));
    }

    public T get() {
        return t;
    }

    public void set(T t) {
        this.t = t;
    }
}
