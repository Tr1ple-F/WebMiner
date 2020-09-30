package project.rest.client.auth;

import org.junit.jupiter.api.*;

import java.util.UUID;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TokenManagerTest {

    private TokenManager manager;
    private UUID uuid;

    private static final String username = "username";
    private static final String password = "password";
    private static final String uri = "uri";
    private static final String db = "db";
    private static final Credentials verify = new Credentials(username, password, uri, db);

    @BeforeAll
    void init() {
        manager = new TokenManager();
        uuid = UUID.randomUUID();
    }

    @Test
    void test() {
        manager.add(uuid, username, password, uri, db);
        Assertions.assertTrue(manager.get(uuid).equals(verify));
        manager.delete(uuid);
        Assertions.assertEquals(manager.get(uuid), null);
    }
}
