package com.example.finalproject1;

import java.util.ArrayList;

public class OnlineGame {

    private ArrayList<Gamer> gamer; //when someone enters online they wait for another player insert two gamers
    private Boss boss1, boss2; //when two players exist create a boss monster for them and insert it into the online game in fire-base
    //once there are 2 players and a boss in fire-base we can start loading the game.
    //in the game, the only things that there are the fists and the monster.
    //whoever wins steals all gold from opponent
    //gold is how we measure the leaderboard
    //if the timer runs out on both phones the game ends in a draw and both players are returned to the main menu activity
    //to check who won we always save the time and once the monster dies we stop and compare the times.

    private String difficulty;
    private Boolean player1win, player2win;

    public OnlineGame(ArrayList<Gamer> gamer, Boss boss1, Boss boss2, String difficulty) {
        this.gamer = gamer;
        this.boss1 = boss1;
        this.boss2 = boss2;
        this.difficulty = difficulty;
        player1win = false;
        player2win = false;
    }

    public OnlineGame(ArrayList<Gamer> gamer){
        this.gamer = gamer;
        this.boss1 = null;
        this.boss2 = null;
        this.difficulty = "insane";
    }

    public OnlineGame(ArrayList<Gamer> gamer, ArrayList<Gamer> gamer2) {
        this.gamer = gamer;
        this.boss1 = null;

        this.difficulty = "insane";
    }

    public OnlineGame(){
        this.gamer = new ArrayList<>();
        gamer.add(new Gamer());
        gamer.add(new Gamer());
        this.boss1 = new Boss();
        this.boss2 = new Boss();
        this.difficulty = "insane";
        player1win = false;
        player2win = false;
    }

    public ArrayList<Gamer> getGamer() {
        return gamer;
    }

    public void setGamer(ArrayList<Gamer> gamer) {
        this.gamer = gamer;
    }

    public Boss getBoss1() {
        return boss1;
    }

    public void setBoss1(Boss boss1) {
        this.boss1 = boss1;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    @Override
    public String toString() {
        return "OnlineGame{" +
                "gamer=" + gamer +
                ", boss1=" + boss1 +
                ", boss2=" + boss2 +
                ", difficulty='" + difficulty + '\'' +
                ", player1win=" + player1win +
                ", player2win=" + player2win +
                '}';
    }

    public Boolean getPlayer1win() {
        return player1win;
    }

    public void setPlayer1win(Boolean player1win) {
        this.player1win = player1win;
    }

    public Boolean getPlayer2win() {
        return player2win;
    }

    public void setPlayer2win(Boolean player2win) {
        this.player2win = player2win;
    }

    public Boss getBoss2() {
        return boss2;
    }

    public void setBoss2(Boss boss2) {
        this.boss2 = boss2;
    }

    public void addGamer(int index, Gamer gamer){
        this.gamer.set(index, gamer);
    }

}
