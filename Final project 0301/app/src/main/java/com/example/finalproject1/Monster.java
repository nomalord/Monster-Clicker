package com.example.finalproject1;

import java.util.Random;

public class Monster {


    protected String name, size, type;
    protected int level; //the monsters' level
    protected int hp; //monster hit points

    protected double dropChance;

    Weapon WeaponType;

    //protected String[] listSize = new String[]{"small", "medium", "large"};
    //protected String[] listType = new String[]{"fire", "water", "air"};

    public Monster(String name, int level, double dropChance, int hp, String weaponName,
                   String size, String type) {
        this.name = name;
        this.level = level;
        this.dropChance = dropChance;
        this.hp = hp;
        this.size = size;
        this.type = type;

        WeaponType = new Weapon(weaponName, 15, "fire");
    }

    public Monster(String name, int level, int hp) {
        this.name = name;
        this.level = level;
        this.hp = hp;
    }
    public Monster(){
        this.name = "";
        this.level = 0;
        this.dropChance = 0;
        this.hp = 0;
        this.size = "";
        this.type = "";

        WeaponType = new Weapon();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public double getDropChance() {
        return dropChance;
    }

    public void setDropChance(double dropChance) {
        this.dropChance = dropChance;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public void setMagical(Boolean magical) {
    }

    public Weapon getWeaponType() {
        return WeaponType;
    }

    public void setWeaponType(Weapon weaponType) {
        WeaponType = weaponType;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Monster{" +
                "name='" + name + '\'' +
                ", size='" + size + '\'' +
                ", type='" + type + '\'' +
                ", level=" + level +
                ", hp=" + hp +
                ", dropChance=" + dropChance +
                ", WeaponType=" + WeaponType +
                '}';
    }
    public void onHit(int hit){
        this.hp = this.hp - hit;
    }

    public Boolean death(){
        if(this.hp <= 0)
            return true;
        return false;
    }


    public void createSlime(int level, double dropChance, int hp, Weapon weapon, //create a slime type monster
                   String size, String type) {
        this.name = "slime";
        this.level = level;
        this.dropChance = dropChance;
        this.hp = hp;
        this.size = size;
        this.type = type;

        this.WeaponType = weapon;
    }

    public void createGoblin(int level, double dropChance, int hp, Weapon weapon, //create a goblin type monster
                            String size, String type) {
        this.name = "goblin";
        this.level = level;
        this.dropChance = dropChance;
        this.hp = hp;
        this.size = size;
        this.type = type;

        this.WeaponType = weapon;
    }

    public void createOgre(int level, double dropChance, int hp, Weapon weapon, //create an ogre type monster
                             String size, String type) {
        this.name = "ogre";
        this.level = level;
        this.dropChance = dropChance;
        this.hp = hp;
        this.size = size;
        this.type = type;

        this.WeaponType = weapon;
    }

    //public Monster nextStage(){

    //}

    public void generateMonster(int level, int hp){//create a random monster
        Random random = new Random();
        int temp = random.nextInt(3);
        double dropRand = Math.random();

        switch(temp){
            case 0:
                createSlime(level, dropRand ,hp ,new Weapon("random"), randomSize(), randomType());
                break;
            case 1:
                createGoblin(level, dropRand ,hp ,new Weapon("random"), randomSize(), randomType());
                break;

            case 2:
                createOgre(level, dropRand ,hp ,new Weapon("random"), randomSize(), randomType());
                break;
        }
    }

    public String randomSize(){ //generate a random size
        Random random = new Random();
        int temp = random.nextInt(3);

        switch(temp){
            case 0:
                return "small";
            case 1:
                return "medium";
            case 2:
                return "large";
        }
        return "";
    }

    public String randomType(){ //generate a random type
        Random random = new Random();
        int temp = random.nextInt(3);

        switch(temp){
            case 0:
                return "fire";
            case 1:
                return "water";
            case 2:
                return "air";
        }
        return "";
    }

}
