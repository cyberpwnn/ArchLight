/*
 * Copyright (c) 2021.
 * Arcane Arts Inc. All rights reserved.
 * Proprietary. Do not distribute outside MPower Me LLC or Arcane Arts Inc.
 */

package ninja.bytecode.shuriken.config;

import ninja.bytecode.shuriken.json.JSONObject;

public abstract class WritableObject implements Writable {
    public abstract void toJSON(JSONObject o);

    @Override
    public abstract void fromJSON(JSONObject j);

    @Override
    public JSONObject toJSON() {
        JSONObject j = new JSONObject();
        toJSON(j);
        return j;
    }

}
