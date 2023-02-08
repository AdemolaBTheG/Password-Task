package org.acme;

import lombok.*;

import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;

@Path("/users")
public class UserResource {

    @Inject
    UserRepository userRepository;

    @POST
    public Response register(final User user){

        if(user == null){

            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        final User createdUser = userRepository.addUser(user);
        return Response.created(URI.create("users" + createdUser.getId())).build();

    }

    @GET
    @Path("/login")
    @Produces("application/json")
    public Response LoginUser(UserDTO userLogin){

        if(userLogin == null){

            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        try{
            final User user = userRepository.userLogin(userLogin);
            return Response.ok(user).build();

        }catch (NoResultException e){
            return Response.ok("Incorrect credentials.").build();

        }
    }




    @GET
    @Path("{id}")
    public Response getUserById(@PathParam("id") final String id){

        if(id == null){

            return Response.status(Response.Status.BAD_REQUEST).build();

        }

        final User user = userRepository.getUserById(Long.parseLong(id));
        return Response.ok(user).build();

    }

    @POST
    @Path("/forgotPassword")
    public Response resetPassword(ResetDTO resetDTO){

        if (resetDTO == null){

            return Response.status(Response.Status.BAD_REQUEST).build();
        }


        try{
            final User updatedUser = userRepository.resetPassword(resetDTO);
            return Response.ok(updatedUser).build();

        }catch (NoResultException e){
            return Response.ok("Password could not be updated,").build();

        }

    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @ToString
    @EqualsAndHashCode
    public static class UserDTO {

        String username;
        String password;

    }

    public static class ResetDTO {

        String username;
        String email;
        String telephoneNumber;
    }

}