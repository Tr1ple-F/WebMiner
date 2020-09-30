package project.rest.client.auth;

import javax.annotation.ManagedBean;
import javax.enterprise.context.ApplicationScoped;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@ApplicationScoped
@ManagedBean
// TokenManager
// CDI - Injectable
public class TokenManager {

    private Map<UUID, Credentials> tokenCredentialsMap;

    public TokenManager() {
        tokenCredentialsMap = new HashMap<>();
    }

    public void add(UUID token, String username, String password, String uri, String db) {
        Credentials c = new Credentials(username, password, uri, db);
        tokenCredentialsMap.put(token, c);
    }

    public void delete(UUID uuid) {
        tokenCredentialsMap.remove(uuid);
    }

    public Credentials get(UUID uuid) {
        return tokenCredentialsMap.get(uuid);
    }

}
