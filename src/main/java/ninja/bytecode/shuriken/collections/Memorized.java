/*
 * Copyright (c) 2021.
 * Arcane Arts Inc. All rights reserved.
 * Proprietary. Do not distribute outside MPower Me LLC or Arcane Arts Inc.
 */

package ninja.bytecode.shuriken.collections;

public abstract class Memorized<T> {
    private boolean memorized;
    private T memory;

    public Memorized() {
        this.memorized = false;
        this.memory = null;
    }

    public abstract T runOnce();

    public T get() {
        return memorized ? memory : runOnce();
    }
}
