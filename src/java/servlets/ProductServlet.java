/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Babathurpe
 */
//@WebServlet("/products")
@Path("/products")
public class ProductServlet {

    private JsonObject jsonData;

    @GET
    @Produces("application/json; charset=UTF-8")
    public String doGet() throws SQLException {
        //JSONArray jsonArray = new JSONArray();
        JsonArrayBuilder productsList = Json.createArrayBuilder();
        Connection conn = DbConnection.getConnection();
        String query = "SELECT * FROM product";
        PreparedStatement pstmt = conn.prepareStatement(query);
        
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            JsonObjectBuilder productBuilder = Json.createObjectBuilder();
            productBuilder.add("productId", rs.getInt("productid"))
                    .add("name", rs.getString("name"))
                    .add("description", rs.getString("description"))
                    .add("quantity", rs.getInt("quantity"));
            productsList.add(productBuilder);
        }
        return productsList.build().toString();
    }

    @GET
    @Produces("application/json; charset=UTF-8")
    @Path("{productid}")
    public String doGet(@PathParam("productid") int id) throws SQLException {
        Connection conn = DbConnection.getConnection();
        String query = "SELECT * FROM product where productid =" + id;
        PreparedStatement pstmt = conn.prepareStatement(query);

        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            jsonData = Json.createObjectBuilder()
                    .add("productId", rs.getInt("productid"))
                    .add("name", rs.getString("name"))
                    .add("description", rs.getString("description"))
                    .add("quantity", rs.getInt("quantity"))
                    .build();
        }
        return jsonData.toString();
    }

    @POST
    @Path("{productid}")
    public void doPost(String str) throws SQLException, ParseException {
        JSONObject jsonPostData = (JSONObject) new JSONParser().parse(str);
        String productName = (String) jsonPostData.get("name");
        String productDesc = (String) jsonPostData.get("description");
        long productQty = (long) jsonPostData.get("quantity");
        doInsert("INSERT INTO product (name, description, quantity) VALUES (?, ?, ?)", productName, productDesc, productQty);
    }

    private int doInsert(String query, String name, String desc, long qty) {
        int numChanges = 0;
        ArrayList params = new ArrayList();
        params.add(name);
        params.add(desc);
        params.add(qty);
        try (Connection conn = DbConnection.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(query);
            for (int i = 1; i <= params.size(); i++) {
                pstmt.setString(i, params.get(i - 1).toString());
            }
            numChanges = pstmt.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(ProductServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        return numChanges;
    }

    @DELETE
    @Path("{productid}")
    public void doDelete(@PathParam("productid") int id) throws IOException, SQLException {
        Connection conn = DbConnection.getConnection();
        String query = "DELETE FROM product where productid =" + id;
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.execute();
    }

    private int delete(String query, int id) {
        int numChanges = 0;
        try (Connection conn = DbConnection.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setLong(1, id);
            numChanges = pstmt.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(ex);
            Logger.getLogger(ProductServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        return numChanges;
    }

    @PUT
    @Path("{productid}")
    public void doPut(@PathParam("productid") int id, String str) throws SQLException, ParseException {
        JSONObject jsonPutData = (JSONObject) new JSONParser().parse(str);
        //long productid = (long) jsonPostData.get("id");
        String productName = (String) jsonPutData.get("name");
        String productDesc = (String) jsonPutData.get("description");
        long productQty = (long) jsonPutData.get("quantity");
        Connection conn = DbConnection.getConnection();
        String query = "UPDATE product SET name =\'" + productName + "\', description =\'" + productDesc + "\', quantity =" + productQty + " WHERE productid =" + id;
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.executeUpdate();
    }

    private int Update(String query, String name, String desc, long qty, long id) {
        int numChanges = 0;
        ArrayList params = new ArrayList();
        params.add(name);
        params.add(desc);
        params.add(qty);
        try (Connection conn = DbConnection.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(query);
            for (int i = 1; i <= params.size(); i++) {
                pstmt.setString(i, params.get(i - 1).toString());
            }
            numChanges = pstmt.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(ProductServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        return numChanges;
    }
}
