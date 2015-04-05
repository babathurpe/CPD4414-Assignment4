/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import javax.json.Json;
import javax.json.JsonObject;

/**
 *
 * @author Babathurpe
 */
public class Products {
    private int productId;
    private String name;
    private String description;
    private int quantity;
    
     public Products(){
         
     }

    //Constructor of Products via form input
    public Products(int productId, String name, String description, int quantity) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.quantity = quantity;
    }
    
    //Constructor JSON format of Products
    public JsonObject toJson(){
        return Json.createObjectBuilder()
                .add("productId", productId)
                .add("name", name)
                .add("description", description)
                .add("quantity", quantity)
                .build();
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "Product{" + "productId=" + productId + ", name=" + name + ", description=" + description + ", quantity=" + quantity + '}';
    }
    
    
}
