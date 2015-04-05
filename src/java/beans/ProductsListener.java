/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import javax.jms.Message;
import javax.jms.MessageListener;

/**
 *
 * @author Babathurpe
 */
public class ProductsListener implements MessageListener {
    
    public ProductsListener() {
    }
     
    @Override
    public void onMessage(Message message) {
    }
    
}
