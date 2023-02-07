package com.example.finalproject1;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Random;

public class OnlineActivity extends AppCompatActivity {

    TextView name, HP, timerView;
    ImageButton weaponIMG, monsterIMG;
    Boss boss;
    Intent serviceIntent;
    Intent get;
    ProgressBar HPbar, load;
    Gamer gamer;
    CountDownTimer timer;

    Integer hpPercent;

    String randomString;

    OnlineGame onlineGame;

    AnimationDrawable monsterAnimation, weaponAnimation;

    FirebaseDatabase database;
    DatabaseReference myRef, onlineGameRef, totalRef;
    String account;

    int ogHP, imageResource, sec5;

    Boolean onCheckHost;

    Drawable res;

    ValueEventListener valueEventListenerOnline, valueEventListenerConnect, valueEventListenerGamer
            ,valueEventListenerSecond, valueEventListenerDead, valueEventListenerEnd;

    private View.OnClickListener attackOnClickListener = v -> Attack(v); //listener for attack click

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.online_game);

        serviceIntent = new Intent(OnlineActivity.this, MusicService.class); //create music service

        onCheckHost = true;

        name = findViewById(R.id.monsterName);
        HP = findViewById(R.id.monsterHP);
        weaponIMG = findViewById(R.id.weapon);
        monsterIMG = findViewById(R.id.monsterIMG);
        timerView = findViewById(R.id.timer);
        HPbar = findViewById(R.id.HPbar);
        load = findViewById(R.id.load);

        HPbar.getProgressDrawable().setColorFilter(
                Color.RED, android.graphics.PorterDuff.Mode.SRC_IN); //set progress bar color red

        weaponIMG.setOnClickListener(attackOnClickListener);
        monsterIMG.setOnClickListener(attackOnClickListener);

        gamer = new Gamer();
        onlineGame = new OnlineGame();
        boss = new Boss();

        database = FirebaseDatabase.getInstance();
        get = getIntent();
        account = get.getStringExtra("account");

        name.setVisibility(View.GONE);
        HP.setVisibility(View.GONE);
        weaponIMG.setVisibility(View.GONE);
        monsterIMG.setVisibility(View.GONE);
        HPbar.setVisibility(View.GONE);
        load.setVisibility(View.VISIBLE);

        myRef = database.getReference("Users").child(account);
        totalRef = database.getReference();

        randomString = getSaltString();

        //FirebaseDatabase.getInstance().getReference("Online Game").removeValue();

        valueEventListenerSecond = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {//gets gamer
                onlineGame = snapshot.child("online game").getValue(OnlineGame.class);
                startGame(2);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        valueEventListenerOnline = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) { //get data from firebase and add to an empty online game or a created online game
                for(DataSnapshot child: snapshot.getChildren()){
                    if(child.getKey().contains("Online Game")){
                        for(DataSnapshot childOfChild: child.getChildren()){
                            if(childOfChild.getKey().contains("online game")){
                                onCheckHost = false;
                                break;
                            }
                            onCheckHost = true;
                        }
                        if(onCheckHost){
                            FirebaseDatabase.getInstance().getReference(child.getKey()).child(account).setValue(gamer);
                            onCheckHost = false;
                            randomString = child.getKey();
                            randomString.replace("Online", "");
                            Log.i("lol", randomString);
                            onlineGameRef = database.getReference(randomString);
                            createTimer2(1);
                            break;
                        }
                        onCheckHost = true;
                    }
                }
                if(onCheckHost){
                    FirebaseDatabase.getInstance().getReference("Online Game "+randomString)
                            .child(account).setValue(gamer);
                    onlineGameRef = database.getReference("Online Game "+randomString);
                    onlineGameRef.addValueEventListener(valueEventListenerConnect);
                    Log.d("onlineRef", onlineGameRef.toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i("database", String.valueOf(error));
            }
        };

        valueEventListenerConnect = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                switch ((int) snapshot.getChildrenCount()){
                    case 1:
                        break;
                    case 2:
                        int i = 0;
                        for (DataSnapshot child: snapshot.getChildren()){
                            onlineGame.addGamer(i, child.getValue(Gamer.class));
                            i++;
                        }

                        randomMonsterMaker(boss);
                        onlineGame.setBoss1(boss);
                        onlineGame.setBoss2(boss);
                        onlineGame.getGamer().get(0).setWeapon(new Weapon());
                        onlineGame.getGamer().get(1).setWeapon(new Weapon());

                        onlineGame.getBoss1().setHp(onlineGame.getGamer().get(0).getLevel() * 550);
                        onlineGame.getBoss2().setHp(onlineGame.getGamer().get(1).getLevel() * 550);

                        onlineGame.getGamer().get(0).setTimeLeft(60);
                        onlineGame.getGamer().get(1).setTimeLeft(60);
                        onlineGameRef.removeEventListener(valueEventListenerConnect);

                        snapshot.getRef().child("online game").setValue(onlineGame);
                        startGame(1);

                        Log.d("onlineRef", onlineGameRef.toString());
                        break;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i("database", String.valueOf(error));
            }
        };

        valueEventListenerGamer = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                gamer = snapshot.getValue(Gamer.class);
                totalRef.addListenerForSingleValueEvent(valueEventListenerOnline);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i("database", String.valueOf(error));
            }
        };

        valueEventListenerDead = new ValueEventListener() { //called when monster dies
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                deadFunc(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i("database", String.valueOf(error));
            }
        };

        valueEventListenerEnd = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(onCheckHost) {
                    if(snapshot.child("online game").child("player2win").getValue(Boolean.class)){
                        changeActivity(LoseActivity.class);
                        onlineGameRef.removeEventListener(valueEventListenerEnd);
                    }
                }
                else{
                    if(snapshot.child("online game").child("player1win").getValue(Boolean.class)){
                        changeActivity(LoseActivity.class);
                        onlineGameRef.removeEventListener(valueEventListenerEnd);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        myRef.addListenerForSingleValueEvent(valueEventListenerGamer);

    }

    private void Attack(View v) {

        if (!boss.death() && !((int) (+100 * ((double) boss.getHp() / ogHP)) == 0)){
            randomMonsterIMGdmg(boss);
            if(onCheckHost){
                boss.onHit(onlineGame.getGamer().get(0).getLevel() * 50);
                HPbar.setProgress(boss.getHp());
                animateAttack(onlineGame.getGamer().get(0));
                onlineGameRef.child("online game").child("boss1").setValue(onlineGame.getBoss1());
            }
            else{
                boss.onHit(onlineGame.getGamer().get(1).getLevel() * 50);
                HPbar.setProgress(boss.getHp());
                animateAttack(onlineGame.getGamer().get(1));
                onlineGameRef.child("online game").child("boss2").setValue(onlineGame.getBoss2());
            }
            HP.setText("HP = " + (int) (+100 * ((double) boss.getHp() / ogHP)) + "%");

        }

        else {
            deadMonster();
        }
    }
    public void deadMonster(){ //if a monster dies action is called to create a new monster by creating new activity saving data to firebase etc
        HP.setText("DEAD");

        timer.cancel();
        if(onCheckHost){
            onlineGame.getGamer().set(0, gamer);
            onlineGameRef.child("online game").child("time1").setValue(onlineGame.getGamer().get(0).getTimeLeft());
        }
        else{
            onlineGame.getGamer().set(1, gamer);
            onlineGameRef.child("online game").child("time2").setValue(onlineGame.getGamer().get(1).getTimeLeft());
        }
        onlineGameRef.addListenerForSingleValueEvent(valueEventListenerDead);
    }

    public void randomMonsterMaker(Monster monster) { //create a random monster and set an image accordingly
        monster.generateMonster(gamer.getLevel(), 550 * gamer.getLevel());
        randomMonsterIMG(monster);
    }

    public void randomMonsterIMG(Monster monster) { //set the image of a monster accordingly to its name type lvl etc

        String uri = "@drawable/" + monster.getName() + "_" + monster.getType();  // where myresource (without the extension) is the file

        imageResource = getResources().getIdentifier(uri, null, getPackageName());

        res = getResources().getDrawable(imageResource);

        monsterIMG.setBackground(res);

        monsterAnimation = (AnimationDrawable) monsterIMG.getBackground();
        monsterAnimation.start();
    }
    public void randomMonsterIMGdmg(Monster monster){ //set the image of a monster accordingly to its name type lvl etc if it was damaged
            String uri = "@drawable/" + monster.getName() + "_" + monster.getType() +"_dmg";  // where myresource (without the extension) is the file

            imageResource = getResources().getIdentifier(uri, null, getPackageName());

            res = getResources().getDrawable(imageResource);

            monsterIMG.setBackground(res);

            monsterAnimation = (AnimationDrawable) monsterIMG.getBackground();
            monsterAnimation.start();
        }

    protected String getSaltString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 18) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;

    }
    public void createTimer(int start){ //create a timer for some time and check at the end who won.
        timer = new CountDownTimer(start * 1000L, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.i("Main", "Countdown seconds remaining" + millisUntilFinished / 1000);
                timerView.setText(String.valueOf((int) (millisUntilFinished / 1000)));
                if(onCheckHost)
                    onlineGameRef.child("time player 1").setValue((int) millisUntilFinished / 1000);
                else
                    onlineGameRef.child("time player 2").setValue((int) millisUntilFinished / 1000);

                Log.d("onlineRef", onlineGameRef.toString());
            }

            @Override
            public void onFinish() {
                if(onCheckHost){
                    onlineGameRef.child("online game").child("boss1").setValue(onlineGame.getBoss1());
                    onlineGameRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.child("time player 1").getValue(Integer.class) == 0 &&
                                    snapshot.child("time player 2").getValue(Integer.class) == 0){
                                deadFunc(snapshot);
                            }
                            name.setVisibility(View.INVISIBLE);
                            HP.setVisibility(View.INVISIBLE);
                            weaponIMG.setVisibility(View.INVISIBLE);
                            monsterIMG.setVisibility(View.INVISIBLE);
                            HPbar.setVisibility(View.INVISIBLE);
                            load.setVisibility(View.VISIBLE);
                            timerView.setText("loading results");
                            onlineGameRef.addValueEventListener(valueEventListenerEnd);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                else {
                    onlineGameRef.child("online game").child("boss2").setValue(onlineGame.getBoss2());

                    onlineGameRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.child("time player 1").getValue(Integer.class) == 0 &&
                                    snapshot.child("time player 2").getValue(Integer.class) == 0) {
                                deadFunc(snapshot);
                            }
                            else
                                onlineGameRef.addValueEventListener(valueEventListenerEnd);
                            name.setVisibility(View.INVISIBLE);
                            HP.setVisibility(View.INVISIBLE);
                            weaponIMG.setVisibility(View.INVISIBLE);
                            monsterIMG.setVisibility(View.INVISIBLE);
                            HPbar.setVisibility(View.INVISIBLE);
                            load.setVisibility(View.VISIBLE);
                            timerView.setText("loading results");

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        };
        timer.start();
    }
    public void startGame(int num){

        Log.d("onlineRef", onlineGameRef.toString());

        name.setVisibility(View.VISIBLE);
        HP.setVisibility(View.VISIBLE);
        weaponIMG.setVisibility(View.VISIBLE);
        monsterIMG.setVisibility(View.VISIBLE);
        HPbar.setVisibility(View.VISIBLE);
        load.setVisibility(View.GONE);

        onlineGame.getGamer().get(0).setLevel(1);
        onlineGame.getGamer().get(1).setLevel(1);

        switch (num){
            case 1:
                boss = onlineGame.getBoss1();
                ogHP = onlineGame.getBoss1().getHp();
                hpPercent = (int) (+100 * ((double) onlineGame.getBoss1().getHp() / ogHP));
                HP.setText("HP = " + hpPercent + "%");
                HPbar.setMax(ogHP);
                HPbar.setProgress(ogHP);
                setAttack(new Gamer());
                boss = onlineGame.getBoss1();
                break;
            case 2:
                boss = onlineGame.getBoss2();
                randomMonsterIMG(boss);
                ogHP = onlineGame.getBoss2().getHp();
                hpPercent = (int) (+100 * ((double) onlineGame.getBoss2().getHp() / ogHP));
                HP.setText("HP = " + hpPercent + "%");
                HPbar.setMax(ogHP);
                HPbar.setProgress(ogHP);
                setAttack(new Gamer());

                break;
        }
        createTimer(5);

        name.setText("level " + 100 + " "+ boss.getType() + " " + boss.getName());
    }
    public void setAttack(Gamer gamer){ //set the image of the weapon accordinglty and animate the weapon every attack
        String uri = "";
        if(gamer.getWeapon().getName().equals("fists")){
            uri = "@drawable/" + gamer.getWeapon().getName() +"_animation";  // where myresource (without the extension) is the file
        }
        else
            uri = "@drawable/" + gamer.getWeapon().getName() + "_" + gamer.getWeapon().getType();  // where myresource (without the extension) is the file

        imageResource = getResources().getIdentifier(uri, null, getPackageName());

        res = getResources().getDrawable(imageResource);

        weaponIMG.setBackground(res);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Really Exit?")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        onlineGameRef.removeValue();
                        OnlineActivity.super.onBackPressed();
                        finish();
                    }
                }).create().show();
    }

    public void createTimer2(int start){ //create a timer for some time and if 5 sec pass reset monster damage animation
        timer = new CountDownTimer(start * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.i("Main", "Countdown seconds remaining" + millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {
                onlineGameRef.addListenerForSingleValueEvent(valueEventListenerSecond);
            }
        };
        timer.start();
    }
    public void animateAttack(Gamer gamer) { //set the image of a weapon accordingly and animate the wepaon
        setAttack(gamer);

        weaponAnimation = (AnimationDrawable) weaponIMG.getBackground();
        weaponAnimation.start();

    }

    public void changeActivity(Class cl){ //change activities and delete previous to stop users from accessing previous monster
        Intent newActiv = new Intent(OnlineActivity.this, cl);
        newActiv.putExtra("account", account);
        OnlineActivity.this.startActivity(newActiv);
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseDatabase.getInstance().getReference("Users")
                .child(account).child("loggedIn").setValue(false);
        onlineGameRef.child("time player 1").setValue(0);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
    }

    public void deadFunc(DataSnapshot snapshot){
        int i = 0, hp1, hp2;
        String[] accountDead = new String[2];

        for(DataSnapshot child : snapshot.getChildren()){
            accountDead[i] = child.getKey();
            i++;
            if(i == 2)
                break;
        }

        hp1 = snapshot.child("online game").child("boss1").getValue(Boss.class).getHp();
        hp2 = snapshot.child("online game").child("boss2").getValue(Boss.class).getHp();

        if(hp1 < hp2){
            if(onCheckHost){
                onlineGame.setPlayer1win(true);
                Intent newActiv = new Intent(OnlineActivity.this, WinActivity.class);
                newActiv.putExtra("account", account);
                newActiv.putExtra("player1", accountDead[0]);
                newActiv.putExtra("player2", accountDead[1]);
                newActiv.putExtra("player1win",onlineGame.getPlayer1win());
                newActiv.putExtra("player2win",onlineGame.getPlayer2win());
                OnlineActivity.this.startActivity(newActiv);
            }
            else
                changeActivity(LoseActivity.class);
            onlineGameRef.child("online game").setValue(onlineGame);
        }
        else if(hp2 < hp1){
            onlineGame.setPlayer2win(true);
            if(!onCheckHost){
                Intent newActiv = new Intent(OnlineActivity.this, WinActivity.class);
                newActiv.putExtra("account", account);
                newActiv.putExtra("player1", accountDead[0]);
                newActiv.putExtra("player2", accountDead[1]);
                newActiv.putExtra("player1win",onlineGame.getPlayer1win());
                newActiv.putExtra("player2win",onlineGame.getPlayer2win());
                OnlineActivity.this.startActivity(newActiv);
            }
            else
                changeActivity(LoseActivity.class);
            onlineGameRef.child("online game").setValue(onlineGame);
        }
        if(hp1 == hp2) {
            FirebaseDatabase.getInstance().getReference("Users")
                    .child(accountDead[1]).child("gold").setValue(0);
            FirebaseDatabase.getInstance().getReference("Users")
                    .child(accountDead[0]).child("gold").setValue(0);
        }
        onlineGameRef.child("online game").setValue(onlineGame);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { //creating options menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_game, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) { //update options menu constantly to check if new tracks are added

        if(gamer != null){
            gamer.tracks();

            MenuItem menuItem;

            int[] menuItemsId = new int[6];
            menuItemsId[0] = R.id.track1;
            menuItemsId[1] = R.id.track2;
            menuItemsId[2] = R.id.track3;
            menuItemsId[3] = R.id.track4;
            menuItemsId[4] = R.id.track5;
            menuItemsId[5] = R.id.track6;

            for(int i = 0; i< gamer.getTrackUnlocked().size(); i++){
                if(gamer.getTrackUnlocked().get(i)) {
                    menuItem = menu.findItem(menuItemsId[i]);
                    menuItem.setVisible(true);
                    continue;
                }
                break;
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) { //every options menu click do something accordingly

        switch (item.getItemId()) {
            case R.id.track1:
                mediaPlayerMenu("1");
                break;
            case R.id.track2:
                mediaPlayerMenu("20");
                break;
            case R.id.track3:
                mediaPlayerMenu("40");
                break;
            case R.id.track4:
                mediaPlayerMenu("60");
                break;
            case R.id.track5:
                mediaPlayerMenu("80");
                break;
            case R.id.track6:
                mediaPlayerMenu("100");
                break;
            case R.id.stopMusic:
                stopService(serviceIntent);
                break;
            case R.id.exit:
                FirebaseDatabase.getInstance().getReference("Users").child(account).child("loggedIn").setValue(false);
                finish();
                System.exit(0);
                break;
            case R.id.logOut:
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mAuth.signOut();
                FirebaseDatabase.getInstance().getReference("Users").child(account).child("loggedIn").setValue(false);
                Intent newActiv = new Intent(OnlineActivity.this, LoginActivity.class);
                mAuth.signOut();
                stopService(serviceIntent);
                newActiv.putExtra("account", account);
                newActiv.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                OnlineActivity.this.startActivity(newActiv);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    public void mediaPlayerMenu(String level) { //method for playing music when someone picks it in menu
        this.stopService(serviceIntent);

        serviceIntent.putExtra("lvl", level);
        this.startService(serviceIntent);
    }
}
