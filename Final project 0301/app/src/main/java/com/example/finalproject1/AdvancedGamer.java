package com.example.finalproject1;

public class AdvancedGamer extends Gamer{
    private String path;
    private Boolean accountCheck;

    public Boolean getAccountCheck() {
        return accountCheck;
    }

    public void setAccountCheck(Boolean accountCheck) {
        this.accountCheck = accountCheck;
    }

    public AdvancedGamer(String email, String password, String name, Weapon weapon, Monster monster,
                         int level, int gold, String difficulty, String path){
        super(email, password, name, weapon, monster, level, gold, difficulty);
        this.path = path;
        accountCheck = false;
    }

    public AdvancedGamer() {
        this.path = null;
        accountCheck = false;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "AdvancedGamer{" +
                "path='" + path + '\'' +
                ", level=" + level +
                ", gold=" + gold +
                ", timeLeft=" + timeLeft +
                ", weapon=" + weapon +
                ", difficulty='" + difficulty + '\'' +
                ", monster=" + monster +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", loggedIn=" + loggedIn +
                '}';
    }
}
