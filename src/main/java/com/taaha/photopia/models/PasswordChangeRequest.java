package com.taaha.photopia.models;

import com.taaha.photopia.validator.ValidPassword;

import java.io.Serializable;

public class PasswordChangeRequest implements Serializable {
    @ValidPassword
    private String oldpassword;

    @ValidPassword
    private String newpassword;

    public PasswordChangeRequest() {
    }

    public PasswordChangeRequest(String oldpassword, String newpassword) {
        this.oldpassword = oldpassword;
        this.newpassword = newpassword;
    }

    public String getOldpassword() {
        return oldpassword;
    }

    public void setOldpassword(String oldpassword) {
        this.oldpassword = oldpassword;
    }

    public String getNewpassword() {
        return newpassword;
    }

    public void setNewpassword(String newpassword) {
        this.newpassword = newpassword;
    }
}
