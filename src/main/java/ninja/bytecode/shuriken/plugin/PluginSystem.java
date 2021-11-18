/*
 * Copyright (c) 2021.
 * Arcane Arts Inc. All rights reserved.
 * Proprietary. Do not distribute outside MPower Me LLC or Arcane Arts Inc.
 */

package ninja.bytecode.shuriken.plugin;

import ninja.bytecode.shuriken.collections.KList;

import java.io.File;

public interface PluginSystem {
    public KList<PluginManager> getPlugins();

    public PluginManager load(File p) throws PluginException;

    public void loadAll(File folder) throws PluginException;

    public void disableAll();

    public void enableAll();

    public void unloadAll();

    public PluginManager getPlugin(String name);

    public PluginManager sideload(String pluginName, String classname) throws PluginException;
}
