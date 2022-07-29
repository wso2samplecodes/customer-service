package org.wso2.rest.jersey;

/**
 * Created by ramindu on 9/27/17.
 */

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.impl.DefaultFileSystemManager;
import org.apache.commons.vfs2.impl.StandardFileSystemManager;
import org.json.JSONException;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@Path("/customer/")
public class Customer {
    HashMap<String, Map<String, Object>> customerDetails = new HashMap<String, Map<String, Object>>();

    public Customer() {
        Map<String, Object> customer1 = new HashMap<>();
        customer1.put("name", "John");
        customer1.put("city", "Brussels");
        customer1.put("accountNo", 123456);
        customer1.put("contact", 940710351);
        Map<String, Object> customer2 = new HashMap<>();
        customer2.put("name", "Mary");
        customer2.put("city", "Antwerp");
        customer2.put("accountNo", 789102);
        customer1.put("contact", 940734543);
        customerDetails.put("John", customer1);
        customerDetails.put("Mary", customer2);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/content")
    public Response postOrgDetails(String payload) {
        StandardFileSystemManager fsManager;
        PrintWriter pw = null;
        OutputStream out = null;
        JSONObject jsonPayload = new JSONObject(payload);
        String fileName = jsonPayload.getString("fileName");
        String payloadContent = jsonPayload.getString("content");
        String note = "";
        try {
            note = jsonPayload.getString("note");
        } catch (JSONException ignore) {

        }

        String output;
        try {
//            fsManager = VFS.getManager();
            fsManager = new StandardFileSystemManager();
            fsManager.init();
            if (fsManager != null) {
                FileObject fileObj = fsManager.resolveFile(fileName);
                // if the file does not exist, this method creates it, and the parent folder, if necessary
                // if the file does exist, it appends whatever is written to the output stream
                out = fileObj.getContent().getOutputStream(true);
                pw = new PrintWriter(out);

                if (note.isEmpty()) {
                    payloadContent = payloadContent + " - via service1\n";
                } else {
                    payloadContent = payloadContent + " - via service1 - Note: " + note + "\n";
                }
                pw.write(payloadContent);
                pw.flush();
                fileObj.close();
                fsManager.close();
            }
            output = "{\"status\": \"content processed successfully\"}";
        } catch (FileSystemException e) {
            e.printStackTrace();
            output = "{\"exception\": " + e.getMessage() + " - " + payloadContent + "}";
        } finally {
            if (pw != null) {
                pw.close();
            }
        }

        if (output.isEmpty()) {
            output = "{\"exception\": \" exception has ocurred \"}";
        }
        return Response.status(200).entity(output).build();
    }

    @GET
    @Path("/all")
    public Response getMsg2() {
        String output = "[{\"name\": \"WSO2\", \"country\": \"USA\"},{\"name\": \"IBM\", \"country\": \"USA\"}]";
        return Response.status(200).entity(output).build();
    }

    @GET
    @Path("/{customerName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCustomerDetails(@PathParam("customerName") String customerName) {
        String output;
        try {
            if (customerDetails.containsKey(customerName)) {
                Map<String, Object> customer = customerDetails.get(customerName);
                output = "{\"name\": \"" + customerName + "\", \"city\": \"" + customer.get("city") + "\", \"account\": " + customer.get("accountNo") + "}";
            } else {
                output = "{\"message\": \"Account Name for " + customerName + " not found\"}";
                return Response.status(500).entity(output).build();
            }
        } catch (Throwable t) {
            output = "{\"message\": " + t.getMessage() + "}";
            return Response.status(500).entity(output).build();
        }
        return Response.status(200).entity(output).build();
    }

    @GET
    @Path("/{customerName}/contact")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCustomerContactDetails(@PathParam("customerName") String customerName) {
        String output;
        try {
            if (customerDetails.containsKey(customerName)) {
                Map<String, Object> customer = customerDetails.get(customerName);
                output = "{\"name\": \"" + customerName + "\", \"contact\": " + customer.get("contact") + "}";
            } else {
                output = "{\"message\": \"Account Name for " + customerName + " not found\"}";
                return Response.status(500).entity(output).build();
            }
        } catch (Throwable t) {
            output = "{\"message\": " + t.getMessage() + "}";
            return Response.status(500).entity(output).build();
        }
        return Response.status(200).entity(output).build();
    }

    @GET
    @Path("/info")
    public Response get_staff() {
        String output = "get staff called";
        return Response.status(200).entity(output).build();
    }
}
