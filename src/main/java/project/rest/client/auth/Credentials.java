package project.rest.client.auth;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;

public class Credentials {

    private String username;
    private String password;
    private String uri;
    private String db;

    public Credentials() {
    }

    public Credentials(String username, String password, String uri, String db) {
        this.username = username;
        this.password = password;
        this.uri = uri;
        this.db = db;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getDb() {
        return db;
    }

    public void setDb(String db) {
        this.db = db;
    }

    // Create connection from Credentials
    public Connection use() throws SQLException {
        return DriverManager.getConnection(uri + db, username, password);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Credentials that = (Credentials) o;
        return Objects.equals(username, that.username) &&
                Objects.equals(password, that.password) &&
                Objects.equals(uri, that.uri) &&
                Objects.equals(db, that.db);
    }

}
