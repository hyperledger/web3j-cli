package org.web3j.console.account;

import okhttp3.Call;
import okhttp3.ConnectionPool;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.web3j.console.config.CliConfig;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AccountManagerTest {
    private static final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private static final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private static final PrintStream originalOut = System.out;
    private static final PrintStream originalErr = System.err;

    @BeforeAll
    public static void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @AfterAll
    public static void restoreStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    public void testAccountCreation() throws IOException {
        OkHttpClient mockedOkHttpClient = mock(OkHttpClient.class);
        Call call = mock(Call.class);
        ConnectionPool connectionPool = mock(ConnectionPool.class);
        AccountManager accountManager = new AccountManager(new CliConfig("", "", "", "", "", ""), mockedOkHttpClient);
        Request request = accountManager.createRequest(accountManager.createRequestBody("test@gmail.com"));
        Response response = new Response.Builder().protocol(Protocol.H2_PRIOR_KNOWLEDGE).message("")
                .body(ResponseBody.create("{\n" +
                        "    \"token\": \"8190c700-1f10-4c50-8bb2-1ce78bf0412b\",\n" +
                        "    \"createdTimestamp\": \"1583234909601\"\n" +
                        "}", MediaType.parse("application/json")))
                .code(200).request(request).build();
        when(call.execute()).thenReturn(response);
        when(mockedOkHttpClient.newCall(any(Request.class))).thenReturn(call);
        when(mockedOkHttpClient.connectionPool()).thenReturn(connectionPool);
        doNothing().when(connectionPool).evictAll();
        accountManager.createAccount("test@gmail.com");
        Assertions.assertTrue(outContent.toString().contains("Account created successfully."));
    }

}
