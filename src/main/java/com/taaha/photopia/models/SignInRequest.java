package com.taaha.photopia.models;

import com.sun.istack.NotNull;
import com.taaha.photopia.validator.ValidPassword;

import javax.validation.constraints.*;
import java.io.Serializable;

public class SignInRequest implements Serializable {

    @NotNull
    @NotEmpty(message="Username(empty) is mandatory")
    @NotBlank(message="Username(blank) is mandatory")
    @Size(min=2,message = "Username(size) at least wo character")
    private String username;

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
    public SignInRequest()
    {

    }

    public SignInRequest(String username, String password) {
        this.setUsername(username);
        this.setPassword(password);
    }
}
