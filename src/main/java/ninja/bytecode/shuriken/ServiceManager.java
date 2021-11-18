/*
 * Copyright (c) 2021.
 * Arcane Arts Inc. All rights reserved.
 * Proprietary. Do not distribute outside MPower Me LLC or Arcane Arts Inc.
 */

package ninja.bytecode.shuriken;

import ninja.bytecode.shuriken.collections.KMap;
import ninja.bytecode.shuriken.service.IService;

import java.lang.reflect.InvocationTargetException;

public class ServiceManager {
    private KMap<Class<? extends IService>, IService> services;

    public ServiceManager() {
        services = new KMap<Class<? extends IService>, IService>();
    }

    public void stop() {
        for(Class<? extends IService> i : services.k()) {
            stop(i);
        }
    }

    public void stop(Class<? extends IService> s) {
        if(services.containsKey(s)) {
            services.get(s).onStop();
            services.remove(s);
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends IService> T getService(Class<T> sc) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        if(!services.containsKey(sc)) {
            services.put(sc, sc.getConstructor().newInstance());
            services.get(sc).onStart();
        }

        return (T) services.get(sc);
    }
}
