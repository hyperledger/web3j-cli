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
package org.web3j.console.project.utils;

import java.io.IOException;

public class ProgressCounter {
    private boolean isLoading;

    public ProgressCounter(boolean isLoading) {
        this.isLoading = isLoading;
    }

    public void setLoading(boolean isLoading) {
        this.isLoading = isLoading;
    }

    public synchronized void processing(String message) {
        Thread th =
                new Thread(
                        () -> {
                            String anim = "|/―\\";
                            try {
                                System.out.write("\r|".getBytes());
                                int current = 0;
                                while (isLoading) {
                                    current++;
                                    String data =
                                            String.format(
                                                    "\r[ %s ] %s",
                                                    anim.charAt(current % anim.length()), message);
                                    System.out.write(data.getBytes());
                                    Thread.sleep(500);
                                }
                                System.out.write("\n".getBytes());
                            } catch (IOException | InterruptedException e) {
                                e.printStackTrace();
                            }
                        });
        th.start();
    }
}
