package com.taaha.photopia.models;

import com.sun.istack.NotNull;

import javax.validation.constraints.*;
import java.io.Serializable;

public class AuthenticationRequest implements Serializable {

    @NotNull
    @NotEmpty(message="Name(empty) is mandatory")
    @NotBlank(message="Name(blank) is mandatory")
    @Size(min=2,message = "Name(size) at least wo character")
    private String username;

    @NotNull
    @NotEmpty(message="Password(empty) is mandatory")
    @NotBlank(message="Password(blank) is mandatory")
    @Min(value = 5,message = "min len 5")
    @Max(value = 10,message = "max len 10")
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    //need default constructor for JSON Parsing
    public AuthenticationRequest()
    {

    }

    public AuthenticationRequest(String username, String password) {
        this.setUsername(username);
        this.setPassword(password);
    }
}
