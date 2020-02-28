package org.web3j.console.telemetry;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.web3j.console.Runner;
import org.web3j.console.config.CliConfig;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class Telemetry {

    public static void invokeAnalyticsUpload() throws URISyntaxException, IOException {
        System.out.println("Invoking telemetry upload");
        final String jarFile = new File(
                Runner.class
                        .getProtectionDomain()
                        .getCodeSource()
                        .getLocation()
                        .toURI())
                .getPath();
        if (jarFile.endsWith(".jar")) {
            Runtime.getRuntime()
                    .exec(
                            new String[]{
                                    "java",
                                    "-jar",
                                    jarFile,
                                    "--telemetry"
                            });
        }
    }


    private CliConfig config;

    public Telemetry(CliConfig config) {
        this.config = config;
    }

    public void uploadAnalytics() {
        OkHttpClient client = new OkHttpClient();

        RequestBody analyticsBody =
                new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("os", CliConfig.determineOS().toString())
                        .addFormDataPart("clientId", config.getClientId())
                        .addFormDataPart("data", "not_update")
                        .addFormDataPart("params", "params")
                        .build();

        Request analyticsRequest =
                new okhttp3.Request.Builder()
                        .url(String.format("%s/api/analytics", config.getServicesUrl()))
                        .post(analyticsBody)
                        .build();

        try {
            client.newCall(analyticsRequest).execute();
        } catch (Exception ignored) {
        }
    }

}
