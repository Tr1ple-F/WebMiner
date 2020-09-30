package project.rest.client.resources;

import project.rest.client.auth.Credentials;
import project.rest.client.auth.TokenManager;
import project.rest.client.code.SQLRequests;
import project.rest.client.code.StaticInfo;
import project.rest.client.mining.Table;
import project.rest.client.mining.WebMiner;
import project.rest.client.response.InfoMessage;
import project.rest.client.response.MiningResponse;
import project.rest.client.response.TableInfo;

import javax.inject.Inject;
import javax.net.ssl.*;
import javax.ws.rs.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Path("/mine")
public class MiningResource {

    // Inject TokenManager via CDI
    private @Inject
    TokenManager tokenManager;

    @OPTIONS
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    // CORS Options
    public Response options() {
        return Response.noContent().build();
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    // Mining Request
    public Response mine(@HeaderParam("authToken") UUID token, @HeaderParam("target") String target, @HeaderParam("masks") String masks) {
        List<InfoMessage> warnings = new ArrayList<>();
        if (target == null || target.isEmpty()) {
            // No Target specified
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new InfoMessage(Response.Status.BAD_REQUEST.getStatusCode(), StaticInfo.noTargetDefined))
                    .build();
        }
        String content;
        try {
            // Get content
            content = getContent(target);
            if (masks != null && !masks.isEmpty()) {

                for (String mask : masks.split(",")) {
                    content = content.replaceAll(mask, "");
                }
            }
        } catch (Exception e) {
            // Unable to load URI
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new InfoMessage(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), StaticInfo.unableToLoadURI))
                    .build();
        }
        List<Table> tables = WebMiner.parse(content); // Parse Tables
        Credentials c = tokenManager.get(token);
        try {
            Connection connection = c.use();
            for (Table t : tables) {
                // Generate SQL Code
                String outer = SQLRequests.SQL_CREATE_TABLE.replace("?", t.getName());
                StringBuilder innerStringBuilder = new StringBuilder();
                boolean hasID = false;
                try {
                    for (String column : t.getColumns()) {
                        // SQL attributes
                        innerStringBuilder.append("`");
                        innerStringBuilder.append(column);
                        innerStringBuilder.append("`");
                        innerStringBuilder.append(" ");
                        // Column type
                        String type = t.getColumnTypeMap().get(t.getColumns().indexOf(column));
                        innerStringBuilder.append(type);
                        innerStringBuilder.append(",");

                        if (column.equalsIgnoreCase("id")) {
                            hasID = true;
                        }
                    }
                    if (!hasID) {
                        // Add ID attribute if non-existent
                        innerStringBuilder.append("id");
                        innerStringBuilder.append(" ");
                        innerStringBuilder.append("INTEGER");
                        innerStringBuilder.append(",");
                    }
                    // ID is primary key
                    innerStringBuilder.append("PRIMARY KEY (id)");

                    // Execute SQL Code
                    Statement stmt = connection.createStatement();
                    stmt.executeUpdate(outer.replace("%", innerStringBuilder.toString()));
                } catch (SQLException e) {
                    // CREATE TABLE exception
                    e.printStackTrace();
                    warnings.add(new InfoMessage(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), e.getMessage()));
                }
                try {
                    int rIndex = 0;
                    for (List<String> row : t.getRows()) {
                        // Generate SQL Code
                        String sqlBase = SQLRequests.SQL_INSERT.replace("?", t.getName());
                        StringBuilder sqlInsert = new StringBuilder();
                        int column = 0;
                        for (String item : row) {
                            // Item type
                            String type = t.getColumnTypeMap().get(column);
                            // Use comma for thousand notation
                            if (type.equals("DOUBLE")) {
                                sqlInsert.append(Double.parseDouble(item.replace(",", "")));
                            } else if (type.equals("INTEGER")) {
                                sqlInsert.append(Integer.parseInt(item.replace(",", "")));
                            } else {
                                sqlInsert.append("'");
                                sqlInsert.append(item);
                                sqlInsert.append("'");
                            }
                            sqlInsert.append(",");
                            column++;
                        }
                        if (hasID) {
                            // No ID, delete last comma
                            sqlInsert.deleteCharAt(sqlInsert.length() - 1);
                        }else{
                            // add id
                            sqlInsert.append(rIndex);
                        }

                        Statement rIn = connection.createStatement();
                        // Execute statement
                        rIn.executeUpdate(sqlBase.replace("%", sqlInsert.toString()));
                        rIndex++;
                    }
                } catch (SQLException e) {
                    // INSERT exception
                    e.printStackTrace();
                    warnings.add(new InfoMessage(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), e.getMessage()));
                }
            }
            connection.close();
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
        // Everything works fine
        MiningResponse response = new MiningResponse(tables.stream().map(TableInfo::new).collect(Collectors.toList()), warnings);
        return Response.ok(response).build();
    }

    private String getContent(String uri) throws Exception {
        // SSL Certificate Security
        Client client = ClientBuilder.newBuilder().sslContext(SSLContext.getDefault()).build();
        // Get target URI and return as String
        String content = client.target(uri).request().get(String.class);
        client.close();
        return content;
    }

}
