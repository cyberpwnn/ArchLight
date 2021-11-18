/*
 * Copyright (c) 2021.
 * Arcane Arts Inc. All rights reserved.
 * Proprietary. Do not distribute outside MPower Me LLC or Arcane Arts Inc.
 */

package ninja.bytecode.shuriken.execution;

@FunctionalInterface
public interface Function4<A, B, C, D, R> {
    public R apply(A a, B b, C c, D d);
}
