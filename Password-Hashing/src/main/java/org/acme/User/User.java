package org.acme.User;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode

@Entity
public class User {

    @Id
    @GeneratedValue
    private long Id;

    private String username;
    private String email;
    private String telephoneNumber;
    private String password;
    private byte[] salt;
}
