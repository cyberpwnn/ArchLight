/*
 * Copyright (c) 2021.
 * Arcane Arts Inc. All rights reserved.
 * Proprietary. Do not distribute outside MPower Me LLC or Arcane Arts Inc.
 */

package ninja.bytecode.shuriken.sql;

import java.sql.Connection;

public interface SleepyConnection extends Connection {
    public long getTimeSinceLastUsage();
}
