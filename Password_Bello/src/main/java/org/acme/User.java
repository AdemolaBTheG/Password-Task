package org.acme;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@NamedQuery(name= "GET_BY_NAME",query="SELECT u FROM User u WHERE u.userName = :name")
@NamedQuery(name="UPDATE_PASSWORD", query ="UPDATE User u set u.hashedPassword = :hashed WHERE u.id = :id")
@Getter
@Setter
@Entity
public class User{

    @Id
    @GeneratedValue
    private int id;

    private String userName;
    @Transient
    private String password;

    private String email;

    private String phoneNumber;

    private byte[] hashedPassword;

    private byte[] salt;

}