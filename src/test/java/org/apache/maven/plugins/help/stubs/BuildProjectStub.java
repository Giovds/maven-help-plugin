/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.maven.plugins.help.stubs;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.api.model.Build;
import org.apache.maven.api.model.Plugin;
import org.apache.maven.api.plugin.testing.stubs.ProjectStub;

/**
 * A stub implementation of a project with a build that can be manipulated.
 * This class provides methods to access and manipulate plugins in the build.
 */
public class BuildProjectStub extends ProjectStub {

    private List<Plugin> plugins = new ArrayList<>();

    /**
     * Gets the build for this project.
     *
     * @return the build
     */
    @Override
    public Build getBuild() {
        return Build.newBuilder().plugins(plugins).build();
    }

    /**
     * Sets the plugins for this project.
     *
     * @param plugins the plugins to set
     */
    public void setPlugins(List<Plugin> plugins) {
        this.plugins = plugins;
    }
}
