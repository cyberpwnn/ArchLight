/*
 * Copyright (c) 2021.
 * Arcane Arts Inc. All rights reserved.
 * Proprietary. Do not distribute outside MPower Me LLC or Arcane Arts Inc.
 */

package ninja.bytecode.shuriken.plugin;

public interface Plugin {
    public boolean isEnabled();

    public void onEnable();

    public void onDisable();

    public PluginManager getPluginManager();

    public void enable();

    public void setManager(PluginManager manager);

    public void disable();
}
