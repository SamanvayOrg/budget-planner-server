package org.mbs.budgetplannerserver.service;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;

// Removing a role from a user in Auth0 is DELETE /api/v2/users/{id}/roles WITH a request
// body listing the roles. Not every HTTP client can send a body on DELETE — the JDK's
// HttpURLConnection, which RestTemplate uses by default, has historically refused it. If
// it silently drops the body, Auth0 receives no roles to remove and the demotion quietly
// does nothing, which is precisely the bug this was meant to close. So pin the behaviour.
class RestTemplateDeleteWithBodyTest {

    @Test
    public void theConfiguredRestTemplateCanSendABodyOnDelete() throws Exception {
        AtomicReference<String> received = new AtomicReference<>("");
        HttpServer server = HttpServer.create(new InetSocketAddress(0), 0);
        server.createContext("/roles", exchange -> {
            try (InputStream in = exchange.getRequestBody()) {
                received.set(new String(in.readAllBytes(), StandardCharsets.UTF_8));
            }
            byte[] body = "{}".getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, body.length);
            exchange.getResponseBody().write(body);
            exchange.close();
        });
        server.start();
        try {
            String url = "http://localhost:" + server.getAddress().getPort() + "/roles";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> request = new HttpEntity<>("{\"roles\":[\"rol_abc\"]}", headers);

            new RestTemplate().exchange(url, HttpMethod.DELETE, request, String.class);

            assertEquals("{\"roles\":[\"rol_abc\"]}", received.get(),
                    "the role list must actually reach the server, or a demotion silently does nothing");
        } finally {
            server.stop(0);
        }
    }
}
