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
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
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
    public void doPost(String str) throws SQLException, ParseException {
        JSONObject jsonData = (JSONObject) new JSONParser().parse(str);
        //int productid = (int) jsonData.get("id");
        String productName = (String) jsonData.get("name");
        String productDesc = (String) jsonData.get("description");
        long productQty = (long) jsonData.get("quantity");
        //System.out.println(productName + "\n" + productDesc + "\n" + productQty);
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
    public void doDelete() throws IOException {
        
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

    @PUT
    public void doPut(String str) throws SQLException, ParseException {
        JSONObject jsonData = (JSONObject) new JSONParser().parse(str);
        long productid = (long) jsonData.get("id");
        String productName = (String) jsonData.get("name");
        String productDesc = (String) jsonData.get("description");
        long productQty = (long) jsonData.get("quantity");
        //System.out.println(productName + "\n" + productDesc + "\n" + productQty);
        Update("UPDATE product SET name = ?, description = ?, quantity = ? WHERE productid = ?", productName, productDesc, productQty, productid);
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
