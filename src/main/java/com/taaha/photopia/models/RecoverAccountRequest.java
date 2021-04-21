package com.taaha.photopia.models;

import com.taaha.photopia.validator.ValidPassword;

import javax.persistence.Column;
import java.io.Serializable;

public class RecoverAccountRequest implements Serializable {

    @ValidPassword
    private String password;

    public RecoverAccountRequest() {
    }

    public RecoverAccountRequest(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
