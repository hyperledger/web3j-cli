/*
 * Copyright 2019 Web3 Labs Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.web3j.console.project.java;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.web3j.console.project.templates.TemplateBuilder;
import org.web3j.console.project.templates.TemplateProvider;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class TemplateProviderTest {
    private TemplateProvider templateProvider;

    @BeforeEach
    public void init() throws IOException {
        templateProvider =
                new TemplateBuilder()
                        .withGradleBatScript("gradlew.bat.template")
                        .withGradleScript("gradlew.template")
                        .withMainJavaClass("Template.java")
                        .withGradleBuild("build.gradle.template")
                        .withGradleSettings("settings.gradle.template")
                        .withWrapperGradleSettings("gradlew-wrapper.properties.template")
                        .withGradlewWrapperJar("gradle-wrapper.jar")
                        .withPackageNameReplacement("test")
                        .withProjectNameReplacement("test")
                        .build();
    }

    @Test
    public void loadMainJavaClassTest() {
        assertFalse(templateProvider.getMainJavaClass().isEmpty());
    }

    @Test
    public void loadGradleBuildTest() {
        assertFalse(templateProvider.getGradleBuild().isEmpty());
    }

    @Test
    public void loadGradleSettingsTest() {
        assertFalse(templateProvider.getGradleSettings().isEmpty());
    }

    @Test
    public void loadGradlewScriptTest() {
        assertFalse(templateProvider.getGradlewScript().isEmpty());
    }

    @Test
    public void loadGradlewBatScriptTest() {
        assertFalse(templateProvider.getGradlewBatScript().isEmpty());
    }

    @Test
    public void loadGradleWrapperTest() {
        assertFalse(templateProvider.getGradlewWrapperSettings().isEmpty());
    }
}
