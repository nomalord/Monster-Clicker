package com.example.finalproject1;

import java.io.Serializable;
import java.util.ArrayList;

public class Gamer extends User implements Serializable {

    protected int level; //the player's level

    protected int gold; //how much gold the player has

    protected int timeLeft;

    protected Weapon weapon;

    protected String difficulty;

    ArrayList<Boolean> trackUnlocked;

    public ArrayList<Boolean> getTrackUnlocked() {
        return trackUnlocked;
    }

    public void setTrackUnlocked(ArrayList<Boolean> trackUnlocked) {
        this.trackUnlocked = trackUnlocked;
    }

    protected Monster monster;

    public Gamer(String email, String password, String name, Weapon weapon, Monster monster,
                 int level, int gold, String difficulty) {
        super(email, password, name);
        this.level = level;
        this.gold = gold;
        this.weapon = weapon;
        this.monster = monster;
        this.difficulty = difficulty;
        this.timeLeft = 60;
        trackUnlocked = new ArrayList<>();
        tracksSetup();
    }

    public Gamer(String email, String password, String name, Weapon weapon,
                 int level,  int gold) {
        super(email, password, name);
        this.level = level;
        this.gold = gold;
        this.weapon = weapon;
        this.difficulty = "";
        this.monster = new Monster();
        this.timeLeft = 60;
        trackUnlocked = new ArrayList<>();
        tracksSetup();
    }

    public Gamer(String email, String password, String name) {
        super(email, password, name);
        this.level = 1;
        this.gold = 0;
        this.weapon = new Weapon();
        this.difficulty = "";
        monster = new Monster();
        this.timeLeft = 60;
        trackUnlocked = new ArrayList<>();
        tracksSetup();
    }

    public Gamer(){
        super("", "", "");
        this.level = 0;
        this.gold = 0;
        this.weapon = new Weapon();
        this.difficulty = "";
        monster = new Monster();
        this.timeLeft = 60;
        tracksSetup();
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }


    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public Weapon getWeapon() {
        return weapon;
    }

    public void setWeapon(Weapon weapon) {
        this.weapon = weapon;
    }

    public Monster getMonster() {
        return monster;
    }

    public void setMonster(Monster monster) {
        this.monster = monster;
    }

    public int getTimeLeft() {
        return timeLeft;
    }

    public void setTimeLeft(int timeLeft) {
        this.timeLeft = timeLeft;
    }

    @Override
    public String toString() {
        return "Gamer{" +
                "level=" + level +
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

    public void levelUp(int gold){
        this.level++;
        this.gold += gold;
    }

    public void restart() {
        this.level = 1;
        difficulty = "";
        this.timeLeft = 60;
        this.weapon = new Weapon();
        tracks();
    }
    public void tracks() {
        tracksSetup();
        if (this.level >= 1 && this.level < 20) {
            for (int i = 0; i < 1; i++)
                trackUnlocked.set(i, true);
        } else if (this.level >= 20 && this.level < 40) {
            for (int i = 0; i < 2; i++)
                trackUnlocked.set(i, true);
        } else if (this.level >= 40 && this.level < 60) {
            for (int i = 0; i < 3; i++)
                trackUnlocked.set(i, true);
        } else if (this.level >= 60 && this.level < 80) {
            for (int i = 0; i < 4; i++)
                trackUnlocked.set(i, true);
        } else if (this.level >= 80 && this.level < 100) {
            for (int i = 0; i < 5; i++)
                trackUnlocked.set(i, true);
        } else if (this.level >= 100) {
            for (int i = 0; i < 6; i++)
                trackUnlocked.set(i, true);
        }
    }
    public void tracksSetup(){
        this.trackUnlocked = new ArrayList<>();
        for(int i = 0; i<6; i++)
            this.trackUnlocked.add(false);
    }
}
