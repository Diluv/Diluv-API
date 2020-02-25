package com.diluv.api2.v1.user;

import static com.diluv.api2.Main.DATABASE;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.diluv.api.endpoints.v1.user.DataAuthorizedUser;
import com.diluv.api.endpoints.v1.user.DataUser;
import com.diluv.api.utils.auth.AccessToken;
import com.diluv.api.utils.error.ErrorMessage;
import com.diluv.confluencia.database.record.UserRecord;

@Path("/user")
public class UserAPI {
    
    @GET
    @Path("/prod")
    @Produces(MediaType.APPLICATION_JSON)
    public Response prod() { 
        
        return Response.ok(new Test()).build();
    }
    
    @GET
    @Path("/self")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSelf(@HeaderParam("Authorization") String auth) { 
        
        if (auth == null) {
            
            return ErrorMessage.USER_REQUIRED_TOKEN.respond();
        }
        
        else {
            
            final AccessToken token = AccessToken.getToken(auth);
            
            if (token == null) {
                
                return ErrorMessage.USER_INVALID_TOKEN.respond();
            }
            
            else {
                
                final UserRecord userRecord = DATABASE.userDAO.findOneByUsername(token.getUsername());
                
                if (userRecord == null) {
                    
                    return ErrorMessage.NOT_FOUND_USER.respond();
                }
                
                return Response.ok(new DataAuthorizedUser(userRecord)).build();
            }
        }
    }
    
    @GET
    @Path("/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUser(@PathParam("username") String username, @HeaderParam("Authorization") String auth) { 
        
        final UserRecord userRecord = DATABASE.userDAO.findOneByUsername(username);
        
        if (userRecord == null) {
            
            return ErrorMessage.NOT_FOUND_USER.respond();
        }
        
        else if (auth != null) {
            
            final AccessToken token = AccessToken.getToken(auth);
            
            if (token != null && token.getUsername().equalsIgnoreCase(username)) {
                
                return Response.ok(new DataAuthorizedUser(userRecord)).build();
            }
        }
        
        return Response.ok(new DataUser(userRecord)).build();
    }
    
    private class Test {
        
        public String val1 = "hello";
        public int val2 = 34;
    }
}