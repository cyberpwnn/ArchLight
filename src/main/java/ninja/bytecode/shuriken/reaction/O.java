/*
 * Copyright (c) 2021.
 * Arcane Arts Inc. All rights reserved.
 * Proprietary. Do not distribute outside MPower Me LLC or Arcane Arts Inc.
 */

package ninja.bytecode.shuriken.reaction;

import ninja.bytecode.shuriken.collections.KList;

public class O<T> implements Observable<T> {
    private T t = null;
    private KList<Observer<T>> observers;

    @Override
    public T get() {
        return t;
    }

    @Override
    public O<T> set(T t) {
        T x = t;
        this.t = t;

        if(observers != null && observers.hasElements()) {
            observers.forEach((o) -> o.onChanged(x, t));
        }

        return this;
    }

    @Override
    public boolean has() {
        return t != null;
    }

    @Override
    public O<T> clearObservers() {
        observers.clear();
        return this;
    }

    @Override
    public O<T> observe(Observer<T> t) {
        if(observers == null) {
            observers = new KList<>();
        }

        observers.add(t);

        return this;
    }
}
