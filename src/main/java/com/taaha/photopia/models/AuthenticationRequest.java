package com.taaha.photopia.models;

import com.sun.istack.NotNull;
import com.taaha.photopia.validator.ValidPassword;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.io.Serializable;

public class AuthenticationRequest implements Serializable {

    @NotNull
    @NotEmpty(message="Name(empty) is mandatory")
    @NotBlank(message="Name(blank) is mandatory")
    @Size(min=2,message = "Name(size) at least wo character")
    private String username;

    @NotNull
    @NotEmpty(message="Email(empty) is mandatory")
    @NotBlank(message="Email(blank) is mandatory")
    @ValidPassword
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
