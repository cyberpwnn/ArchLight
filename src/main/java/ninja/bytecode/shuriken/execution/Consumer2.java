/*
 * Copyright (c) 2021.
 * Arcane Arts Inc. All rights reserved.
 * Proprietary. Do not distribute outside MPower Me LLC or Arcane Arts Inc.
 */

package ninja.bytecode.shuriken.execution;

@FunctionalInterface
public interface Consumer2<A, B> {
    public void accept(A a, B b);
}
