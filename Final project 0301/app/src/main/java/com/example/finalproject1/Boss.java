package com.example.finalproject1;

import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

public class Boss extends Monster{
        private int bonusHp;

    @Override
    public String toString() {
        return "Boss{" +
                "bonusHp=" + bonusHp +
                ", name='" + name + '\'' +
                ", size='" + size + '\'' +
                ", type='" + type + '\'' +
                ", level=" + level +
                ", hp=" + hp +
                ", dropChance=" + dropChance +
                ", WeaponType=" + WeaponType +
                '}';
    }

    public int getBonusHp() {
        return bonusHp;
    }

    public void setBonusHp(int bonusHp) {
        this.bonusHp = bonusHp;
    }


    public Boss(String name, int level, double dropChance, int hp, String weaponName, String size, String type
            , int bonusHp) {
        super(name, level, dropChance, hp, weaponName, size, type);
        this.bonusHp = bonusHp;
    }

    public Boss(String name, int level, int hp) {
            super(name, level, hp);
        }

        public Boss(){
            super();
            this.bonusHp = 0;
        }

    public void createWitch(int level, double dropChance, int hp, Weapon weapon,
                            String size, String type) {

        //creates a boss monster which is a witch type


        this.name = "witch";
        this.level = level;
        this.dropChance = dropChance;
        this.hp = hp;
        this.bonusHp = 0;
        this.size = size;
        this.type = type;

        this.WeaponType = weapon;
    }

    public void createDragon(int level, double dropChance, int hp, Weapon weapon,
                             String size, String type) {

        //creates a boss monster which is a dragon type

        this.name = "dragon";
        this.level = level;
        this.dropChance = dropChance;
        this.hp = hp;
        this.bonusHp = 0;
        this.size = size;
        this.type = type;

        this.WeaponType = weapon;


    }

    @Override
    public void generateMonster(int level, int hp) {//generate a random boss monster
        Random random = new Random();
        int temp = random.nextInt(2);
        double dropRand = Math.random();

        switch(temp){
            case 0:
                createWitch(level, dropRand ,hp ,new Weapon("random"), randomSize(), randomType());
                break;
            case 1:
                createDragon(level, dropRand ,hp ,new Weapon("random"), randomSize(), randomType());
                break;
            //case 2:
                //create a monster which the player created
                //break;
        }
        randomBonus();
    }

    public void randomBonus(){ //creates random bonus health and adds time in the main activity depending on bonus health size
        double dropRand = Math.random();

        double multiplyer = 1+dropRand;

        this.bonusHp = this.hp;

        this.hp *= multiplyer;

        this.bonusHp = this.hp - this.bonusHp;

        Log.i("hp", String.valueOf(this.hp));
    }
}
