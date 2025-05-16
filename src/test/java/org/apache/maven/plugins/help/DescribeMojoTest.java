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
package org.apache.maven.plugins.help;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.apache.maven.api.Session;
import org.apache.maven.api.plugin.descriptor.MojoDescriptor;
import org.apache.maven.api.plugin.testing.stubs.PluginStub;
import org.apache.maven.api.services.MessageBuilderFactory;
import org.apache.maven.impl.DefaultMessageBuilder;
import org.apache.maven.plugins.help.DescribeMojo.PluginInfo;
import org.apache.maven.plugins.help.stubs.BuildProjectStub;
import org.junit.jupiter.api.Test;

import static org.apache.commons.lang3.reflect.FieldUtils.writeDeclaredField;
import static org.apache.commons.lang3.reflect.MethodUtils.invokeMethod;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 */
public class DescribeMojoTest {
    /**
     * Test method for expressions root.
     */
    @Test
    public void testGetExpressionsRoot() {
        try {
            DescribeMojo describeMojo = new DescribeMojo();
            invokeMethod(describeMojo, true, "toLines", "", 2, 2, 80);
        } catch (Throwable e) {
            fail("The API changes");
        }
    }

    @Test
    public void testValidExpression() throws Exception {
        StringBuilder sb = new StringBuilder();
        MojoDescriptor md = MojoDescriptor.newBuilder()
                .parameters(List.of(org.apache.maven.api.plugin.descriptor.Parameter.newBuilder()
                        .name("name")
                        .expression("${valid.expression}")
                        .build()))
                .build();

        String ls = System.getProperty("line.separator");

        Session session = mock(Session.class);
        MessageBuilderFactory messageBuilderFactory = mock(MessageBuilderFactory.class);
        when(session.getService(MessageBuilderFactory.class)).thenReturn(messageBuilderFactory);
        when(messageBuilderFactory.builder()).thenReturn(new DefaultMessageBuilder());

        DescribeMojo mojo = new DescribeMojo();
        mojo.session = session;

        try {
            invokeMethod(mojo, true, "describeMojoParameters", md, sb);

            assertEquals(
                    "  Available parameters:" + ls + ls + "    name" + ls + "      User property: valid.expression" + ls
                            + "      (no description available)" + ls,
                    sb.toString());
        } catch (Throwable e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testInvalidExpression() throws Exception {
        StringBuilder sb = new StringBuilder();
        MojoDescriptor md = MojoDescriptor.newBuilder()
                .parameters(List.of(org.apache.maven.api.plugin.descriptor.Parameter.newBuilder()
                        .name("name")
                        .expression("${project.build.directory}/generated-sources/foobar")
                        .build()))
                .build();

        String ls = System.getProperty("line.separator");

        Session session = mock(Session.class);
        MessageBuilderFactory messageBuilderFactory = mock(MessageBuilderFactory.class);
        when(session.getService(MessageBuilderFactory.class)).thenReturn(messageBuilderFactory);
        when(messageBuilderFactory.builder()).thenReturn(new DefaultMessageBuilder());

        DescribeMojo mojo = new DescribeMojo();
        mojo.session = session;

        try {
            invokeMethod(mojo, true, "describeMojoParameters", md, sb);

            assertEquals(
                    "  Available parameters:" + ls + ls
                            + "    name"
                            + ls + "      Expression: ${project.build.directory}/generated-sources/foobar"
                            + ls + "      (no description available)"
                            + ls,
                    sb.toString());
        } catch (Throwable e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testParsePluginInfoGAV() throws Throwable {
        DescribeMojo mojo = new DescribeMojo();
        writeDeclaredField(mojo, "groupId", "org.test", true);
        writeDeclaredField(mojo, "artifactId", "test", true);
        writeDeclaredField(mojo, "version", "1.0", true);

        PluginInfo pi = (PluginInfo) invokeMethod(mojo, true, "parsePluginLookupInfo");

        assertEquals("org.test", pi.getGroupId());
        assertEquals("test", pi.getArtifactId());
        assertEquals("1.0", pi.getVersion());
        assertNull(pi.getPrefix());
    }

    @Test
    public void testParsePluginInfoPluginPrefix() throws Throwable {
        DescribeMojo mojo = new DescribeMojo();
        writeDeclaredField(mojo, "plugin", "help", true);

        PluginInfo pi = (PluginInfo) invokeMethod(mojo, true, "parsePluginLookupInfo");

        assertNull(pi.getGroupId());
        assertNull(pi.getArtifactId());
        assertNull(pi.getVersion());
        assertEquals("help", pi.getPrefix());

        writeDeclaredField(mojo, "plugin", "help2:::", true);

        pi = (PluginInfo) invokeMethod(mojo, true, "parsePluginLookupInfo");

        assertEquals("help2", pi.getPrefix());
    }

    @Test
    public void testParsePluginInfoPluginGA() throws Throwable {
        DescribeMojo mojo = new DescribeMojo();
        writeDeclaredField(mojo, "plugin", "org.test:test", true);

        PluginInfo pi = (PluginInfo) invokeMethod(mojo, true, "parsePluginLookupInfo");

        assertEquals("org.test", pi.getGroupId());
        assertEquals("test", pi.getArtifactId());
        assertNull(pi.getVersion());
        assertNull(pi.getPrefix());
    }

    @Test
    public void testParsePluginInfoPluginGAV() throws Throwable {
        DescribeMojo mojo = new DescribeMojo();
        writeDeclaredField(mojo, "plugin", "org.test:test:1.0", true);

        PluginInfo pi = (PluginInfo) invokeMethod(mojo, true, "parsePluginLookupInfo");

        assertEquals("org.test", pi.getGroupId());
        assertEquals("test", pi.getArtifactId());
        assertEquals("1.0", pi.getVersion());
        assertNull(pi.getPrefix());
    }

    @Test
    public void testParsePluginInfoPluginIncorrect() throws Throwable {
        DescribeMojo mojo = new DescribeMojo();
        writeDeclaredField(mojo, "plugin", "org.test:test:1.0:invalid", true);
        try {
            invokeMethod(mojo, "parsePluginLookupInfo");
            fail();
        } catch (Exception e) {
            // expected
        }
    }

    @Test
    public void testLookupPluginDescriptorPrefixWithVersion() throws Throwable {
        Session session = mock(Session.class);

        PluginInfo pi = new PluginInfo();
        pi.setPrefix("test");
        pi.setVersion("1.0");

        PluginStub pluginStub = new PluginStub();
        pluginStub.setModel(org.apache.maven.api.model.Plugin.newBuilder()
                .groupId("org.test")
                .artifactId("test")
                .build());

        BuildProjectStub project = new BuildProjectStub();
        project.setPlugins(List.of(pluginStub.getModel()));

        DescribeMojo mojo = new DescribeMojo();
        mojo.session = session;
        mojo.project = project;

        org.apache.maven.api.plugin.descriptor.PluginDescriptor returned =
                (org.apache.maven.api.plugin.descriptor.PluginDescriptor)
                        invokeMethod(mojo, true, "lookupPluginDescriptor", pi);

        assertEquals("org.test", returned.getGroupId());
        assertEquals("test", returned.getArtifactId());
        assertEquals("1.0", returned.getVersion());
    }

    @Test
    public void testLookupPluginDescriptorPrefixWithoutVersion() throws Throwable {
        Session session = mock(Session.class);

        PluginInfo pi = new PluginInfo();
        pi.setPrefix("help");

        PluginStub pluginStub = new PluginStub();
        pluginStub.setModel(org.apache.maven.api.model.Plugin.newBuilder()
                .groupId("org.apache.maven.plugins")
                .artifactId("maven-help-plugin")
                .version("1.0")
                .build());

        BuildProjectStub project = new BuildProjectStub();
        project.setPlugins(List.of(pluginStub.getModel()));

        DescribeMojo mojo = new DescribeMojo();
        mojo.session = session;
        mojo.project = project;

        org.apache.maven.api.plugin.descriptor.PluginDescriptor returned =
                (org.apache.maven.api.plugin.descriptor.PluginDescriptor)
                        invokeMethod(mojo, true, "lookupPluginDescriptor", pi);

        assertEquals("org.apache.maven.plugins", returned.getGroupId());
        assertEquals("maven-help-plugin", returned.getArtifactId());
        assertEquals("1.0", returned.getVersion());
    }

    @Test
    public void testLookupPluginDescriptorGAV() throws Throwable {
        Session session = mock(Session.class);

        PluginInfo pi = new PluginInfo();
        pi.setGroupId("org.test");
        pi.setArtifactId("test");
        pi.setVersion("1.0");

        DescribeMojo mojo = new DescribeMojo();
        mojo.session = session;

        org.apache.maven.api.plugin.descriptor.PluginDescriptor returned =
                (org.apache.maven.api.plugin.descriptor.PluginDescriptor)
                        invokeMethod(mojo, true, "lookupPluginDescriptor", pi);

        assertEquals("org.test", returned.getGroupId());
        assertEquals("test", returned.getArtifactId());
        assertEquals("1.0", returned.getVersion());
    }

    @Test
    public void testLookupPluginDescriptorGMissingA() {
        DescribeMojo mojo = new DescribeMojo();
        PluginInfo pi = new PluginInfo();
        pi.setGroupId("org.test");
        try {
            invokeMethod(mojo, true, "lookupPluginDescriptor", pi);
            fail();
        } catch (InvocationTargetException e) {
            assertTrue(e.getTargetException().getMessage().startsWith("You must specify either"));
        } catch (NoSuchMethodException | IllegalAccessException e) {
            fail();
        }
    }

    @Test
    public void testLookupPluginDescriptorAMissingG() {
        DescribeMojo mojo = new DescribeMojo();
        PluginInfo pi = new PluginInfo();
        pi.setArtifactId("test");
        try {
            invokeMethod(mojo, true, "lookupPluginDescriptor", pi);
            fail();
        } catch (InvocationTargetException e) {
            assertTrue(e.getTargetException().getMessage().startsWith("You must specify either"));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
