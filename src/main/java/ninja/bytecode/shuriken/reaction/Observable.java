/*
 * Copyright (c) 2021.
 * Arcane Arts Inc. All rights reserved.
 * Proprietary. Do not distribute outside MPower Me LLC or Arcane Arts Inc.
 */

package ninja.bytecode.shuriken.reaction;

public interface Observable<T> {
    public T get();

    public Observable<T> set(T t);

    public boolean has();

    public Observable<T> clearObservers();

    public Observable<T> observe(Observer<T> t);
}
