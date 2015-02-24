/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Babathurpe
 */
@WebServlet("/products")
public class ProductServlet extends HttpServlet {

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
            sb.setLength(sb.length()-2);
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

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Content-Type", "text/plain-text");
        try (PrintWriter out = response.getWriter()) {
            if (!request.getParameterNames().hasMoreElements()) {
                // There are no parameters at all
                out.println(getResults("SELECT * FROM product"));
            } else {
                // There are some parameters
                int id = Integer.parseInt(request.getParameter("id"));
                out.println(getSingleResult("SELECT * FROM product WHERE productid = ?", String.valueOf(id)));
            }
        } catch (IOException ex) {
            Logger.getLogger(ProductServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        Set<String> keySet = request.getParameterMap().keySet();
        try (PrintWriter out = response.getWriter()) {
            if (keySet.contains("name") && keySet.contains("description") && keySet.contains("quantity")) {
                // There are some parameters                
                String name = request.getParameter("name");
                String description = request.getParameter("description");
                String quantity = request.getParameter("quantity");
                int qty = Integer.parseInt(quantity);
                doUpdate("INSERT INTO product (name, description, quantity) VALUES (?, ?, ?)", name, description, qty);
            } else {
                // There are no parameters at all
                response.sendError(500, "Unsuccessful insert.");
                //out.println("Error: Not enough data to input. Please use a URL in the form /product?name=XXX&description=XXX&quantity=#");
            }
        } catch (IOException ex) {
            Logger.getLogger(ProductServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    private int doUpdate(String query, String name, String desc, int qty) {
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
    
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try (PrintWriter out = response.getWriter()) {
            if (request.getParameterNames().hasMoreElements()) {
                int id = Integer.parseInt(request.getParameter("id"));
                delete("DELETE FROM product WHERE productid = ?", id);
            } else {
                response.setStatus(500);
            }
        } catch (IOException ex) {
            Logger.getLogger(ProductServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
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
    
    @Override
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
    
    private int update(String query,String name, String desc, int qty,int id) {
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
