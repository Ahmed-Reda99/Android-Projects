package com.example.android.med_ai;

public class User {
    private String email;
    private String password;

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getUserName() {
        return email;
    }

    public String getPassword() {
        return password;
    }

}
