/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import java.io.IOException;
import java.sql.SQLException;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import model.ProductList;
import model.Products;

/**
 *
 * @author Babathurpe
 */
//@WebServlet("/products")
@Path("/products")
@RequestScoped
public class ProductServlet {

    @Inject
    ProductList productList;

    @GET
    @Produces("application/json; charset=UTF-8")
    public Response getAll() throws SQLException {
        return Response.ok(productList.toJson()).build();
    }

    @GET
    @Produces("application/json; charset=UTF-8")
    @Path("{productid}")
    public Response getById(@PathParam("productid") int id) throws SQLException {
        return Response.ok(productList.get(id).toJson()).build();
    }

    @POST
    @Consumes("application/json")
    public Response add(JsonObject json) {
        Response response;
        try {
            productList.add(new Products(json));
            response = Response.ok().build();
        } catch (Exception ex) {
            response = Response.status(500).build();
        }
        return response;
    }

    @DELETE
    @Path("{productid}")
    public Response delete(@PathParam("productid") int id) throws IOException, SQLException {
        Response response;
        try {
            productList.remove(id);
            response = Response.ok().build();
        } catch (Exception ex) {
            response = Response.status(500).build();
        }
        return response;
    }

    @PUT
    @Path("{productid}")
    public Response set(@PathParam("productid") int id, JsonObject json) {
        Response response;
        try {
            Products p = new Products(json);
            productList.set(id, p);
            response = Response.ok().build();
        } catch (Exception ex) {
            response = Response.status(500).build();
        }
        return response;
    }
}
