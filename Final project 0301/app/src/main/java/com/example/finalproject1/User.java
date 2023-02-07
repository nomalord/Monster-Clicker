package com.example.finalproject1;

public class User {
    protected String email, password, name;
    protected Boolean loggedIn;


    public Boolean getLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(Boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public User(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
        loggedIn = false;
    }

    public User(){
        this.email = "";
        this.password = "";
        this.name = "";
        loggedIn = false;
    }



    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", loggedIn=" + loggedIn +
                '}';
    }
}
