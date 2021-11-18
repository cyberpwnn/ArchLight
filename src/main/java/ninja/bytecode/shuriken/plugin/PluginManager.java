/*
 * Copyright (c) 2021.
 * Arcane Arts Inc. All rights reserved.
 * Proprietary. Do not distribute outside MPower Me LLC or Arcane Arts Inc.
 */

package ninja.bytecode.shuriken.plugin;

import java.io.File;

public interface PluginManager {
    public PluginClassLoader getClassLoader();

    public Plugin getPlugin();

    public File getJar();

    public PluginConfig getConfig();

    public void unload();
}
