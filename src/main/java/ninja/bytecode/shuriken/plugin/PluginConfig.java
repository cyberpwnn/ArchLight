/*
 * Copyright (c) 2021.
 * Arcane Arts Inc. All rights reserved.
 * Proprietary. Do not distribute outside MPower Me LLC or Arcane Arts Inc.
 */

package ninja.bytecode.shuriken.plugin;

public class PluginConfig {
    private String version;
    private String main;
    private String name;

    public PluginConfig() {
    }

    public PluginConfig(String version, String main, String name) {
        this.version = version;
        this.main = main;
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public String getMain() {
        return main;
    }

    public String getName() {
        return name;
    }
}
