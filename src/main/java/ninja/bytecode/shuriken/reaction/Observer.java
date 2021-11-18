/*
 * Copyright (c) 2021.
 * Arcane Arts Inc. All rights reserved.
 * Proprietary. Do not distribute outside MPower Me LLC or Arcane Arts Inc.
 */

package ninja.bytecode.shuriken.reaction;

@FunctionalInterface
public interface Observer<T> {
    public void onChanged(T from, T to);
}
