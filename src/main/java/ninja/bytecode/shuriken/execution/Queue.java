/*
 * Copyright (c) 2021.
 * Arcane Arts Inc. All rights reserved.
 * Proprietary. Do not distribute outside MPower Me LLC or Arcane Arts Inc.
 */

package ninja.bytecode.shuriken.execution;

import ninja.bytecode.shuriken.collections.KList;

public interface Queue<T> {
    public Queue<T> queue(T t);

    public Queue<T> queue(KList<T> t);

    public boolean hasNext(int amt);

    public boolean hasNext();

    public T next();

    public KList<T> next(int amt);

    public Queue<T> clear();

    public int size();

    public static <T> Queue<T> create(KList<T> t) {
        return new ShurikenQueue<T>().queue(t);
    }

    @SuppressWarnings("unchecked")
    public static <T> Queue<T> create(T... t) {
        return new ShurikenQueue<T>().queue(new KList<T>().add(t));
    }
}
