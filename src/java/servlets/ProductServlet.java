/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import java.io.IOException;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonObject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import org.json.simple.JSONArray;
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

    private String getResults(String query, String... params) {
        StringBuilder sb = new StringBuilder();
        try (Connection conn = DbConnection.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(query);
            for (int i = 1; i <= params.length; i++) {
                pstmt.setString(i, params[i - 1]);
            }
            ResultSet rs = pstmt.executeQuery();
            sb.append("[");
            while (rs.next()) {
                //sb.append(String.format("%s\t%s\t%s\n", rs.getInt("productid"), rs.getString("name"), rs.getString("description"), rs.getInt("quantity")));
                sb.append(String.format("{ \"ProductId\" : %d, \"name\": \"%s\", \"description\": \"%s\", \"quantity\": %d },\n", rs.getInt("productid"), rs.getString("name"), rs.getString("description"), rs.getInt("quantity")));
            }
            sb.setLength(sb.length() - 2);
            sb.append("]");
        } catch (SQLException ex) {
            Logger.getLogger(ProductServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        return sb.toString();
    }

    private String getSingleResult(String query, String... params) {
        StringBuilder sb = new StringBuilder();
        try (Connection conn = DbConnection.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(query);
            for (int i = 1; i <= params.length; i++) {
                pstmt.setString(i, params[i - 1]);
            }
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                sb.append(String.format("{ \"ProductId\" : %d, \"name\": \"%s\", \"description\": \"%s\", \"quantity\": %s }", rs.getInt("productid"), rs.getString("name"), rs.getString("description"), rs.getInt("quantity")));
            }
        } catch (SQLException ex) {
            Logger.getLogger(ProductServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        return sb.toString();
    }

    @GET
    @Produces("application/json; charset=UTF-8")
    public String doGet() throws SQLException {
        JSONArray jsonArray = new JSONArray();
        Connection conn = DbConnection.getConnection();
        String query = "SELECT * FROM product";
        PreparedStatement pstmt = conn.prepareStatement(query);

        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            int total_columns = rs.getMetaData().getColumnCount();
            JSONObject jsonData = new JSONObject();
            for (int i = 0; i < total_columns; i++) {
                String columnName = rs.getMetaData().getColumnLabel(i + 1).toLowerCase();
                Object columnValue = rs.getObject(i + 1);
                jsonData.put(columnName, columnValue);
            }
            jsonArray.add(jsonData);

        }
        return jsonArray.toJSONString();
    }

    @GET
    @Produces("application/json; charset=UTF-8")
    @Path("{productid}")
    public String doGet(@PathParam("productid") int id) throws SQLException {
        JSONObject jsonData = new JSONObject();
        Connection conn = DbConnection.getConnection();
        String query = "SELECT * FROM product where productid =" + id;
        PreparedStatement pstmt = conn.prepareStatement(query);

        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            int total_columns = rs.getMetaData().getColumnCount();
            
            for (int i = 0; i < total_columns; i++) {
                String columnName = rs.getMetaData().getColumnLabel(i + 1).toLowerCase();
                Object columnValue = rs.getObject(i + 1);
                jsonData.put(columnName, columnValue);
            }
        }
        return jsonData.toJSONString();
    }

    @POST
    //@Consumes("application/json")
    public void doPost(String str) throws SQLException, ParseException {
        JSONObject jsonData = (JSONObject) new JSONParser().parse(str);
        //int productid = (int) jsonData.get("id");
        String productName = (String) jsonData.get("name");
        String productDesc = (String) jsonData.get("description");
        int productQty = (int) jsonData.get("quantity");
        doInsert("INSERT INTO product (name, description, quantity) VALUES (?, ?, ?)", productName, productDesc, productQty);
    }

    private int doInsert(String query, String name, String desc, int qty) {
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

    protected void doDelete() throws IOException {

    }

    private int delete(String query, int id) {
        int numChanges = 0;
        try (Connection conn = DbConnection.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, id);
            numChanges = pstmt.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(ProductServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        return numChanges;
    }

    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Set<String> keySet = request.getParameterMap().keySet();
        if (keySet.contains("name") && keySet.contains("description") && keySet.contains("quantity")) {
            // There are some parameters                
            String name = request.getParameter("name");
            String description = request.getParameter("description");
            String quantity = request.getParameter("quantity");
            int qty = Integer.parseInt(quantity);
            int id = Integer.parseInt(request.getParameter("id"));
            update("UPDATE product SET name = ?, description = ?, quantity = ? WHERE productid = ?", name, description, qty, id);
        } else {
            response.setStatus(500);
        }
    }

    private int update(String query, String name, String desc, int qty, int id) {
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
