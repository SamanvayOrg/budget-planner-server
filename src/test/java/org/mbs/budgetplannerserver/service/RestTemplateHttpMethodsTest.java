package org.mbs.budgetplannerserver.service;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Removing a role from a user in Auth0 is DELETE /api/v2/users/{id}/roles WITH a request
// body listing the roles. Not every HTTP client can send a body on DELETE — the JDK's
// HttpURLConnection, which RestTemplate uses by default, has historically refused it. If
// it silently drops the body, Auth0 receives no roles to remove and the demotion quietly
// does nothing, which is precisely the bug this was meant to close. So pin the behaviour.
class RestTemplateHttpMethodsTest {

    // Auth0's own way to disable an account is to PATCH it with blocked:true, which would
    // be the tidier match for this application's soft delete. It is not used, because the
    // JDK's HttpURLConnection — which a plain RestTemplate uses — accepts only a fixed set
    // of methods and PATCH is not among them. Deletion revokes the account's roles instead.
    //
    // This is pinned rather than left as a comment so the constraint is visible: if the
    // request factory is ever changed for one that supports PATCH, this test fails and
    // whoever changed it should reconsider blocking, which is the better behaviour.
    @Test
    public void patchIsNotAvailableWithTheConfiguredClientWhichIsWhyDeletionRevokesRolesInstead() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>("{\"blocked\":true}", headers);

        ResourceAccessException thrown = assertThrows(ResourceAccessException.class,
                () -> new RestTemplate().exchange("http://localhost:1/user", HttpMethod.PATCH,
                        request, String.class));

        assertTrue(thrown.getMessage().contains("Invalid HTTP method: PATCH"),
                "expected the client to reject PATCH outright, but it failed differently: "
                        + thrown.getMessage());
    }

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
