package com.example.finalproject1;

import java.util.Random;

public class Weapon {

    private String name; //weapon's name
    private int breakingPoint; //number of attacks before weapon breaks
    private String type;

    public Weapon(String name, int breakingPoint, String type) {
        this.name = name;
        this.breakingPoint = breakingPoint;
        this.type = type;
    }

    public Weapon() {
        this.name = "fists";
        this.breakingPoint = -1;
        this.type = "";
    }

    public Weapon(String generate){
        Random random = new Random();
        int temp = random.nextInt(3);

        switch(temp){
            case 0:
                this.type = "fire";
                break;
            case 1:
                this.type = "water";
                break;
            case 2:
                this.type = "air";
                break;
        }
        this.name = "sword";
        this.breakingPoint = random.nextInt(300);
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBreakingPoint() {
        return breakingPoint;
    }

    public void setBreakingPoint(int breakingPoint) {
        this.breakingPoint = breakingPoint;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Weapon{" +
                "name='" + name + '\'' +
                ", breakingPoint=" + breakingPoint +
                ", type='" + type + '\'' +
                '}';
    }

    public void onAttack(){
        this.breakingPoint--;
        if(this.breakingPoint == 0){
            this.name = "fists";
            this.breakingPoint = -1;
            this.type = "";
        }
    }
}
