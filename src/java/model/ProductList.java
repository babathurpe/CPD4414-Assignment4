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
    
    public JsonArray toJson(){
        JsonArrayBuilder jsonProduct = Json.createArrayBuilder();
        for (Products p : productList){
            jsonProduct.add(p.toJson());
        }
        return jsonProduct.build();
    }
}
