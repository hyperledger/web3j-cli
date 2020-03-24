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
package org.web3j.console.project.kotlin;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import org.web3j.console.project.utils.Folders;

public class KotlinProjectCreatorCLIRunnerTest {
    private String tempDirPath;

    @BeforeEach
    void setup() {
        tempDirPath = Folders.tempBuildFolder().getAbsolutePath();
    }

    @Test
    public void testWhenNonDefinedArgsArePassed() {
        final KotlinProjectCreatorCLIRunner kotlinProjectCreatorCLIRunner =
                new KotlinProjectCreatorCLIRunner();
        final String[] args = {"-t=org.org", "-b=test", "-z=" + tempDirPath};
        final CommandLine commandLine = new CommandLine(kotlinProjectCreatorCLIRunner);
        Assertions.assertThrows(
                CommandLine.MissingParameterException.class, () -> commandLine.parseArgs(args));
    }

    @Test
    public void testWhenNoArgsArePassed() {
        final KotlinProjectCreatorCLIRunner kotlinProjectCreatorCLIRunner =
                new KotlinProjectCreatorCLIRunner();
        final String[] args = {};
        final CommandLine commandLine = new CommandLine(kotlinProjectCreatorCLIRunner);
        Assertions.assertThrows(
                CommandLine.MissingParameterException.class, () -> commandLine.parseArgs(args));
    }

    @Test
    public void testWhenDuplicateArgsArePassed() {
        final KotlinProjectCreatorCLIRunner kotlinProjectCreatorCLIRunner =
                new KotlinProjectCreatorCLIRunner();
        final String[] args = {"-p=org.org", "-n=test", "-n=OverrideTest", "-o=" + tempDirPath};
        final CommandLine commandLine = new CommandLine(kotlinProjectCreatorCLIRunner);
        Assertions.assertThrows(
                CommandLine.OverwrittenOptionException.class, () -> commandLine.parseArgs(args));
    }
}
