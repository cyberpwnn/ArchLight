/*
 * Copyright (c) 2021.
 * Arcane Arts Inc. All rights reserved.
 * Proprietary. Do not distribute outside MPower Me LLC or Arcane Arts Inc.
 */

package ninja.bytecode.shuriken.config;

import ninja.bytecode.shuriken.json.JSONObject;

public interface Writable {
    public void fromJSON(JSONObject j);

    public JSONObject toJSON();
}
