/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Singleton;
import javax.enterprise.context.ApplicationScoped;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import servlets.DbConnection;
import servlets.ProductServlet;

/**
 *
 * @author Babathurpe
 */
@ApplicationScoped
public class ProductList {

    private List<Products> productList;

    public ProductList() {
        productList = new ArrayList<>();
        try (Connection connection = DbConnection.getConnection()) {
            String query = "SELECT * FROM product";
            PreparedStatement pstmt = connection.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Products product = new Products(
                        rs.getInt("productid"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getInt("quantity"));
                productList.add(product);
            }

        } catch (SQLException ex) {
            Logger.getLogger(ProductList.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Products get(int productId) {
        Products products = null;
        for (int i = 0; i < productList.size() && products == null; i++) {
            Products p = productList.get(i);
            if (p.getProductId() == productId) {
                products = p;
            }
        }
        return products;
    }
    
    public void set(int productId, Products product) throws Exception {
        int result = doUpdate("UPDATE products SET name = ?, description = ?, quantity = ? WHERE productid = ?",
                product.getName(),
                product.getDescription(),
                String.valueOf(product.getQuantity()),
                String.valueOf(productId)
        );
        if(result == 1){
            Products products = get(productId);
            products.setName(product.getName());
            products.setDescription(product.getDescription());
            products.setQuantity(product.getQuantity());
        } else{
            throw new Exception("Cannot Update Products.");
        }
    }
    
    public void remove(Products p) throws Exception {
        remove(p.getProductId());
    }

    public void remove(int productId) throws Exception {
        int result = doUpdate("DELETE FROM products WHERE productid = ?",
                String.valueOf(productId));
        if (result > 0) {
            Products product = get(productId);
            productList.remove(product);
        } else {
            throw new Exception("Delete failed");
        }
    }
    
    public void add(Products newProduct) throws Exception {
        int result = doUpdate("INSERT INTO  products (productid, name, description, quantity) VALUES (?,?,?,?)",
                String.valueOf(newProduct.getProductId()),
                newProduct.getName(),
                newProduct.getDescription(),
                String.valueOf(newProduct.getQuantity()));
        if (result > 0) {
            productList.add(newProduct);
        } else {
            throw new Exception("Cannot add product.");
        }
    }
    

    public JsonArray toJson() {
        JsonArrayBuilder jsonProduct = Json.createArrayBuilder();
        for (Products p : productList) {
            jsonProduct.add(p.toJson());
        }
        return jsonProduct.build();
    }

    private int doUpdate(String query, String... params) {
        int numChanges = 0;
        try (Connection conn = DbConnection.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(query);
            for (int i = 1; i <= params.length; i++) {
                pstmt.setString(i, params[i - 1]);
            }
            numChanges = pstmt.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(ProductList.class.getName()).log(Level.SEVERE, null, ex);
        }
        return numChanges;
    }
}
