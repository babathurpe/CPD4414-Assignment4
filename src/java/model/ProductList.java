/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.List;
import javax.ejb.Singleton;

/**
 *
 * @author Babathurpe
 */
@Singleton
public class ProductList {
    
    private List<Products> productList;
    
    public ProductList(){
        
    }
}
