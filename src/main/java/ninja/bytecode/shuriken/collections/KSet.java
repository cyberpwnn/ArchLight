/*
 * Copyright (c) 2021.
 * Arcane Arts Inc. All rights reserved.
 * Proprietary. Do not distribute outside MPower Me LLC or Arcane Arts Inc.
 */

package ninja.bytecode.shuriken.collections;

import java.util.Collection;
import java.util.HashSet;

public class KSet<T> extends HashSet<T> {
    private static final long serialVersionUID = 1L;

    public KSet() {
        super();
    }

    public KSet(Collection<? extends T> c) {
        super(c);
    }

    public KSet(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public KSet(int initialCapacity) {
        super(initialCapacity);
    }
}
