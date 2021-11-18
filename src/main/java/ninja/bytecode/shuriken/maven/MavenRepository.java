/*
 * Copyright (c) 2021.
 * Arcane Arts Inc. All rights reserved.
 * Proprietary. Do not distribute outside MPower Me LLC or Arcane Arts Inc.
 */

package ninja.bytecode.shuriken.maven;

public class MavenRepository {
    private final String repository;

    public MavenRepository(String repository) {
        this.repository = repository;
    }

    public String getRepository() {
        return repository;
    }
}
