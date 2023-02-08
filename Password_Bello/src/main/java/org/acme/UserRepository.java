package org.acme;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@ApplicationScoped
public class UserRepository {

    @Inject
    EntityManager em;

    @Inject
    UserService service;

    public static Logger LOGGER = LoggerFactory.getLogger(Slf4j.class);
    @Transactional
    public User addUser(@NonNull final User user){

        user.setSalt(service.generateSalt());
        try {
            user.setHashedPassword(service.generateHash(user.getPassword(),user.getSalt()));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
        em.persist(user);
        return user;
    }

    public User getUserByName(String username){
        TypedQuery<User> query = em.createNamedQuery("GET_BY_NAME",User.class);
        query.setParameter("name",username);
        return query.getSingleResult();

    }


    public User userLogin(UserResource.UserDTO userLogin){
        LOGGER.info("Logging in with " + userLogin.username + userLogin.password);

        byte[] hashed;

       User loggedUser = getUserByName(userLogin.username);
        try {

             hashed = service.generateHash(userLogin.password, loggedUser.getSalt());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }

        LOGGER.info("Matching data.....");
        if(hashed == loggedUser.getHashedPassword()){
            LOGGER.info("Successfully logged in");
            return loggedUser;
        }
        LOGGER.info("Wrong user credentials!");
        return null;

    }

    public User resetPassword(UserResource.ResetDTO resetDTO){
        LOGGER.info("Trying to reset password with the username " + resetDTO.username);
        User resetUser;
        try {
            LOGGER.info("Query is being generated....");
             resetUser = getUserByName(resetDTO.username);

        }catch(NoResultException ex){

            LOGGER.info("Couldn't find a user with matching usernames");
            return null;
        }

        LOGGER.info("A new password is being generated....");

        resetUser.setSalt(service.generateSalt());
        try {
            resetUser.setHashedPassword(service.generateHash(resetUser.getPassword(),resetUser.getSalt()));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }

        Query query = em.createNamedQuery("UPDATE_PASSWORD");
        query.setParameter("hashed",resetUser.getHashedPassword());
        query.setParameter("id",resetUser.getId());
        query.executeUpdate();

        LOGGER.info("The new password will be sent either to your email:" + resetDTO.email + " or mobile " + resetDTO.telephoneNumber);

        return resetUser;
    }
    public User getUserById(long id) {

        return em.find(User.class,id);
    }


}
