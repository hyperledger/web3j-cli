/*
 * Copyright 2020 Web3 Labs Ltd.
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
package org.web3j.console.openapi.options

import org.web3j.console.project.BaseProjectOptions
import picocli.CommandLine
import picocli.CommandLine.Option

class OpenApiProjectOptions : BaseProjectOptions() {

    @Option(
        names = ["--context-path"],
        description = ["Set the API context path (Default: project name)."]
    )
    var contextPath: String? = null

    @Option(
        names = ["--address-length"],
        description = ["Specify the address length."],
        showDefaultValue = CommandLine.Help.Visibility.ALWAYS
    )
    var addressLength = 20
}
