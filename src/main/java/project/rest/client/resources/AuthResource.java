package project.rest.client.resources;

import project.rest.client.auth.AuthToken;
import project.rest.client.auth.TokenManager;
import project.rest.client.code.SQLRequests;
import project.rest.client.code.StaticInfo;
import project.rest.client.response.InfoMessage;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.*;
import java.util.UUID;

@Path("/authenticationToken")
public class AuthResource {

    // Inject TokenManager via CDI
    private TokenManager tokenManager;

    @Inject
    public AuthResource(TokenManager tokenManager){
        this.tokenManager = tokenManager;
    }

    @OPTIONS
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    // CORS Options Request
    public Response options() {
        return Response.noContent().build();
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    // Login
    public Response authenticate(@HeaderParam("username") String username, @HeaderParam("password") String password,
                                 @HeaderParam("uri") String uri, @HeaderParam("db") String db) {
        // No database selected
        if (db == null || db.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new InfoMessage(Response.Status.BAD_REQUEST.getStatusCode(), StaticInfo.errorDBMissing))
                    .build();
        }
        try {
            // URI
            StringBuilder uriBuilder = new StringBuilder(StaticInfo.dbPrefix);
            if (uri == null) {
                uriBuilder.append(StaticInfo.dbDefault);
            } else {
                uriBuilder.append(uri);
                if (uriBuilder.charAt(uriBuilder.length() - 1) != '/') {
                    uriBuilder.append("/");
                }
            }

            // Test authentication
            Connection connection = DriverManager.getConnection(uriBuilder.toString(), username, password);
            // Get all tables to test connection
            ResultSet resultSet = connection.getMetaData().getCatalogs();
            while (resultSet.next()) {
                String databaseName = resultSet.getString(1);
                if (databaseName.equalsIgnoreCase(db)) {
                    // Test access via retrieving all tables in database
                    connection.getMetaData().getTables(databaseName, null,
                            "%", null);
                    connection.close();
                    AuthToken token = new AuthToken();
                    tokenManager.add(token.getToken(), username, password, uriBuilder.toString(), db);
                    return Response.ok(token).build();
                }
            }
            // Database non existent, trying to create database
            Statement stmt = connection.createStatement();
            stmt.executeUpdate(SQLRequests.SQL_CREATE_DATABASE.replace("?", db));
            connection.close();
            AuthToken token = new AuthToken();
            tokenManager.add(token.getToken(), username, password, uriBuilder.toString(), db);
            return Response.ok(token).build();
        } catch (SQLInvalidAuthorizationSpecException e) {
            // Not authorized / invalid credentials
            e.printStackTrace();
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(new InfoMessage(Response.Status.UNAUTHORIZED.getStatusCode(),
                            Response.Status.UNAUTHORIZED.getReasonPhrase()))
                    .build();
        } catch (SQLException e) {
            // Database not working
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new InfoMessage(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                            Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase()))
                    .build();
        }
    }

    @DELETE
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    // Delete token and credentials
    public Response authenticate(@HeaderParam("authToken") UUID token) {
        if (token == null) {
            // No Token supplied
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new InfoMessage(Response.Status.BAD_REQUEST.getStatusCode(), StaticInfo.tokenMissing))
                    .build();
        }
        tokenManager.delete(token);
        return Response.noContent().build();
    }

}
