package com.example.finalproject1;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    TextView name, HP, timerView;
    ImageButton weaponIMG, monsterIMG, shop, inventory;
    Monster monster;
    String currentImagePath = null;
    private static final int REQUST_IMAGE_CAPTURE = 1;

    Intent serviceIntent;

    Integer hpPercent, dmgChangeAnimation = 0, dmgModifier = 1;

    CountDownTimer timer;

    AnimationDrawable monsterAnimation, weaponAnimation;

    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseAuth mAuth;
    String account;

    ProgressBar HPbar, load;

    Gamer gamer;

    int ogHP, imageResource;

    Drawable res;

    ValueEventListener valueEventListenerCreate;

    private View.OnClickListener attackOnClickListener = v -> Attack(v); //listener for attack click
    private View.OnClickListener inventoryOnClickListener = v -> openInventory(v); //listener for inventory click
    private View.OnClickListener shopOnClickListener = v -> openShop(v); //listener for shop click click


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainapp_page);

        serviceIntent = new Intent(MainActivity.this, MusicService.class); //create music service

        name = findViewById(R.id.monsterName);
        HP = findViewById(R.id.monsterHP);
        weaponIMG = findViewById(R.id.weapon);
        monsterIMG = findViewById(R.id.monsterIMG);
        shop = findViewById(R.id.shop);
        inventory = findViewById(R.id.inventory);
        timerView = findViewById(R.id.timer);
        HPbar = findViewById(R.id.HPbar);
        load = findViewById(R.id.load);

        weaponIMG.setOnClickListener(attackOnClickListener);
        monsterIMG.setOnClickListener(attackOnClickListener);
        inventory.setOnClickListener(inventoryOnClickListener);

        shop.setOnClickListener(shopOnClickListener);

        gamer = new Gamer();

        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null){
            account = mAuth.getCurrentUser().getUid();
        }
        else{
            Intent intent = getIntent();
            account = intent.getStringExtra("account");
        }

        HPbar.getProgressDrawable().setColorFilter(
                Color.RED, android.graphics.PorterDuff.Mode.SRC_IN); //set progress bar color red

        Log.d("account", account);

        name.setVisibility(View.GONE);
        HP.setVisibility(View.GONE);
        weaponIMG.setVisibility(View.GONE);
        monsterIMG.setVisibility(View.GONE);
        shop.setVisibility(View.GONE);
        inventory.setVisibility(View.GONE);
        HPbar.setVisibility(View.GONE);
        load.setVisibility(View.VISIBLE);

        //load.getProgressDrawable().setColorFilter(
        //        Color.BLACK, android.graphics.PorterDuff.Mode.SRC_IN);


        myRef = database.getReference("Users").child(account);
        valueEventListenerCreate = new ValueEventListener() { //read from database
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                AdvancedGamer aGamer = dataSnapshot.getValue(AdvancedGamer.class);
                if(aGamer.getPath() != null)
                    gamer = new AdvancedGamer(aGamer.getEmail(), aGamer.getPassword(), aGamer.getName(),
                            aGamer.getWeapon(), aGamer.getMonster(), aGamer.getLevel(), aGamer.getGold(),
                            aGamer.getDifficulty(), aGamer.getPath());
                else
                    gamer = dataSnapshot.getValue(Gamer.class);

                monster = gamer.getMonster();

                gamer.setTimeLeft(60);

                if (gamer.getMonster().toString().equals(new Monster().toString())) {
                    if(gamer.getLevel() % 10 == 0){
                        monster = new Boss();
                    }
                    if(gamer.getLevel() < 100)
                        randomMonsterMaker(monster);
                    else
                        randomMonsterMakerLast(monster);
                } else{
                    monster = gamer.getMonster();
                    randomMonsterIMG(monster);
                }



                switch (gamer.getDifficulty()) { //check players difficulty level and adjust monster hp accordingly
                    case "normal":
                        ogHP = 125 * gamer.getLevel();
                        break;

                    case "hard":
                        ogHP = 300 * gamer.getLevel();
                        break;

                    case "insane":
                        ogHP = 550 * gamer.getLevel();
                        break;
                }
                if(monster instanceof Boss && gamer.getLevel() % 100 == 0)
                    ogHP = gamer.getLevel() * 1500;
                monster.setHp(ogHP);

                name.setText("level " + monster.getLevel() + " "+monster.getSize()+" "+ monster.getType() + " " + monster.getName());
                hpPercent = (int) (+100 * ((double) monster.getHp() / ogHP));
                HP.setText("HP = " + hpPercent + "%");
                FirebaseDatabase.getInstance().getReference("Users")
                        .child(account).setValue(gamer);
                Log.d("gamer", gamer.toString());
                //Log.d("readGamer", read(MainActivity.this).toString());
                HPbar.setMax(ogHP);
                HPbar.setProgress(ogHP);

                name.setVisibility(View.VISIBLE);
                HP.setVisibility(View.VISIBLE);
                weaponIMG.setVisibility(View.VISIBLE);
                monsterIMG.setVisibility(View.VISIBLE);
                shop.setVisibility(View.VISIBLE);
                inventory.setVisibility(View.VISIBLE);
                HPbar.setVisibility(View.VISIBLE);
                timerView.setText("timer");
                load.setVisibility(View.GONE);

                setAttack(gamer);

                gamer.setMonster(monster);
                FirebaseDatabase.getInstance().getReference("Users")
                        .child(account).setValue(gamer);

                if(monster instanceof Boss){
                    int ratio = monster.getHp() / ((Boss) monster).getBonusHp();
                    ratio = 30 / ratio;
                    createTimer(60 + ratio);
                }
                else
                    createTimer(60);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.d("error1", error.getMessage());
            }
        };
        myRef.addListenerForSingleValueEvent(valueEventListenerCreate); //start the read

        //gamer = new Gamer();
        //myRef.child(account.getUid()).setValue(gamer);
    }

    private void randomMonsterMakerLast(Monster monster) { // when monster gets to level 100 FINAL BOSS TIME
        monster.generateMonster(gamer.getLevel(), 1500 * gamer.getLevel());
        randomMonsterIMG(monster);
    }

    public void Attack(View view) { //every time user clicks weapon or monster attack (deal damage to monster, set hpbar and hp percent.
        Log.d("gamer", gamer.toString());


        switch (gamer.getWeapon().getName()){
            case "fists":
                monster.onHit(gamer.getLevel() * dmgModifier); //* 1000000);
                gamer.getWeapon().onAttack();
                break;
            case "sword":
                //monster.onHit(gamer.getLevel() * dmgModifier * 1000000);
                gamerSwordHit(monster, gamer, dmgModifier);
        }

        HPbar.setProgress(monster.getHp());

        if (!monster.death() && !((int) (+100 * ((double) monster.getHp() / ogHP)) == 0)){
            HP.setText("HP = " + (int) (+100 * ((double) monster.getHp() / ogHP)) + "%");
            randomMonsterIMGdmg(monster);
            animateAttack(gamer);
            gamer.setMonster(monster);
            FirebaseDatabase.getInstance().getReference("Users")
                    .child(account).setValue(gamer);
        }

        else {
            deadMonster();
        }
        //deadMonster();
    }

    public void openInventory(View v) { //open custom dialog for inventory
        inventoryDialog();
    }

    ImageButton profilePic;

    private void inventoryDialog() { //create a dialog for inventory to see stats of gamer
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog_inventory);

        TextView list_gold = dialog.findViewById(R.id.list_gold);
        TextView list_level= dialog.findViewById(R.id.list_level);
        TextView list_weapon= dialog.findViewById(R.id.list_weapon);
        TextView list_monster= dialog.findViewById(R.id.list_monster);

        profilePic = dialog.findViewById(R.id.list_image);

        if(gamer instanceof AdvancedGamer){
            Resources res = getResources();
            Bitmap bitmap = BitmapFactory.decodeFile(((AdvancedGamer) gamer).getPath());
            BitmapDrawable bd = new BitmapDrawable(res, bitmap);
            profilePic.setImageDrawable(bd);
        }

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(cameraIntent, REQUST_IMAGE_CAPTURE);
                    File imageFile = null;

                    try {
                        imageFile = getImageFile();
                        Log.i("filePath", imageFile.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        list_gold.setText("gold: "+gamer.getGold());
        list_level.setText("level: "+gamer.getLevel());
        list_weapon.setText("weapon: "+gamer.getWeapon().toString());
        list_monster.setText("monster: "+gamer.getMonster().toString());

        dialog.show();
    }

    public void openShop(View v) { //open custom dialog for shop
        shopDialog();
    }

    @Override
    public void onBackPressed() { //when user taps back button open alert dialog and ask if they are sure and if they are save data to firebase
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
                        gamer.setMonster(monster);

                        changeActivity(MainMenuActivity.class);
                    }
                }).setOnCancelListener(
                new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        //When you touch outside of dialog bounds,
                        //the dialog gets canceled and this method executes.
                    }
                }).create().show();
    }

    public void randomMonsterMaker(Monster monster) { //create a random monster and set an image accordingly

        switch (gamer.getDifficulty()) { //check players difficulty level and adjust monster hp accordingly
            case "normal":
                monster.generateMonster(gamer.getLevel(), 125 * gamer.getLevel());
                randomMonsterIMG(monster);
                break;

            case "hard":
                monster.generateMonster(gamer.getLevel(), 300 * gamer.getLevel());
                randomMonsterIMG(monster);
                break;

            case "insane":
                monster.generateMonster(gamer.getLevel(), 550 * gamer.getLevel());
                randomMonsterIMG(monster);
                break;
        }
    }


    public void randomMonsterIMG(Monster monster) { //set the image of a monster accordingly to its name type lvl etc

        String uri = "@drawable/" + monster.getName() + "_" + monster.getType();  // where myresource (without the extension) is the file

        imageResource = getResources().getIdentifier(uri, null, getPackageName());

        res = getResources().getDrawable(imageResource);

        monsterIMG.setBackground(res);

        if(!monster.getName().equals("slime")){
            monsterAnimation = (AnimationDrawable) monsterIMG.getBackground();
            monsterAnimation.start();
        }
        monsterSize();
    }
    public void monsterSize(){
        monsterIMG.requestLayout();
        switch (monster.getSize()){
            case "small":{

                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(250, 750, 0, 0);
                monsterIMG.setLayoutParams(lp);
                monsterIMG.getLayoutParams().height = 600;
                monsterIMG.getLayoutParams().width = 600;
                break;
            }
            case "medium":{

                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(175, 615, 0, 0);
                monsterIMG.setLayoutParams(lp);
                monsterIMG.getLayoutParams().height = 750;
                monsterIMG.getLayoutParams().width = 750;
                break;
            }
            case "large":{

                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(100, 480, 0, 50);
                monsterIMG.setLayoutParams(lp);
                monsterIMG.getLayoutParams().height = 900;
                monsterIMG.getLayoutParams().width = 900;
                break;
            }
        }

    }

    public void randomMonsterIMGdmg(Monster monster){ //set the image of a monster accordingly to its name type lvl etc if it was damaged
        if(!monster.getName().equals("slime")){
            String uri = "@drawable/" + monster.getName() + "_" + monster.getType() +"_dmg";  // where myresource (without the extension) is the file

            imageResource = getResources().getIdentifier(uri, null, getPackageName());

            res = getResources().getDrawable(imageResource);

            monsterIMG.setBackground(res);

            monsterAnimation = (AnimationDrawable) monsterIMG.getBackground();
            monsterAnimation.start();
        }
        else{
            String uri = "@drawable/" + monster.getName() +"_dmg";  // where myresource (without the extension) is the file

            imageResource = getResources().getIdentifier(uri, null, getPackageName());

            res = getResources().getDrawable(imageResource);

            monsterIMG.setBackground(res);
        }
    }
    public void animateAttack(Gamer gamer) { //set the image of a weapon accordingly and animate the wepaon
        setAttack(gamer);

        weaponAnimation = (AnimationDrawable) weaponIMG.getBackground();
        weaponAnimation.start();

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

    public void gamerSwordHit(Monster monster, Gamer gamer, int dmgModifier){ //calculate the damage values according to type streangths water -> fire -> air ->water
        int damage1, damage2, damage3;

        switch(gamer.getWeapon().getType()){
            case "fire":
                damage1 = 3;
                damage2 = 4;
                damage3 = 2;
                break;
            case "air":
                damage1 = 2;
                damage2 = 3;
                damage3 = 4;
                break;
            case "water":
                damage1 = 4;
                damage2 = 2;
                damage3 = 3;
                break;
            default:
                damage1 = 1;
                damage2 = 1;
                damage3 = 1;
        }

        switch(monster.getType()){
            case "fire":
                monster.onHit(gamer.getLevel() * damage1 * dmgModifier);
                gamer.getWeapon().onAttack();
                break;
            case "air":
                monster.onHit(gamer.getLevel() * damage2 * dmgModifier);
                gamer.getWeapon().onAttack();
                break;
            case "water":
                monster.onHit(gamer.getLevel() * damage3 * dmgModifier);
                gamer.getWeapon().onAttack();
                break;

        }
    }

    private void shopDialog(){ //open shop dialog with options to double or triple damage and skip level
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog_shop);

        dialog.setTitle("Shop");

        ImageButton doubleDmg = dialog.findViewById(R.id.doubleDmg);
        ImageButton tripleDmg = dialog.findViewById(R.id.tripleDamage);
        ImageButton exit = dialog.findViewById(R.id.exit);

        doubleDmg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("2X Damage")
                        .setMessage("Do you want to purchase double damage cheat for 60 gold?")
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface arg0, int arg1) {
                                purchaseCheat(60);
                            }
                        }).create().show();

            }
        });
        tripleDmg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("3X Damage")
                        .setMessage("Do you want to purchase triple damage cheat for 100 gold?")
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface arg0, int arg1) {
                                purchaseCheat(100);
                            }
                        }).create().show();
            }
        });
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void deadMonster(){ //if a monster dies action is called to create a new monster by creating new activity saving data to firebase etc
        HP.setText("DEAD");
        if(!(gamer.getLevel() % 100 == 0)) {
            if (Math.random() > monster.getDropChance()) {
                new AlertDialog.Builder(this)
                        .setTitle("New Weapon")
                        .setMessage("Do you want to change your weapon to a new one?        " +
                                "   your weapon: " + gamer.getWeapon().toString() +
                                "   dropped weapon: " + monster.getWeaponType().toString())
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                endGame();
                            }
                        })
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface arg0, int arg1) {
                                timer.cancel();
                                gamer.setWeapon(monster.getWeaponType());
                                endGame();
                            }
                        }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        if (gamer.getLevel() % 100 == 0) {
                            changeActivity(WinActivitySolo.class);
                            finish();
                        }
                        endGame();
                    }
                })
                        .create().show();
            } else {
                endGame();
            }
        }

    }
    public void endGame(){ //ending the game leveling up and such
        if(gamer.getLevel() % 100 == 0){
            new AlertDialog.Builder(this)
                    .setTitle("Do you want to continue?")
                    .setMessage("You WON!!!!! do you want to continue to endless or restart?")
                    .setNegativeButton("continue", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            gamer.levelUp(1000);
                            gamer.setMonster(new Monster());
                            gamer.getWeapon().setBreakingPoint(1000);
                            changeActivity(MainActivity.class);
                            dialog.dismiss();
                        }
                    })
                    .setPositiveButton("restart", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface arg0, int arg1) {
                            changeActivity(WinActivitySolo.class);
                            finish();
                        }
                    }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    changeActivity(WinActivitySolo.class);
                    finish();
                }
            });

        }
        else{
            switch(gamer.getDifficulty()){
                case "normal":
                    gamer.levelUp(gamer.getTimeLeft() / 4);
                    break;
                case "hard":
                    gamer.levelUp(gamer.getTimeLeft() / 3);
                    break;
                case "insane":
                    gamer.levelUp(gamer.getTimeLeft() / 2);
                    break;
            }

            gamer.setMonster(new Monster());

            changeActivity(MainActivity.class);
        }
    }

    public void purchaseCheat(int gold){ //method for purchasing items in the shop
        switch(gold) {
            case 60:
                dmgModifier *= 2;
                break;
            case 100:
                dmgModifier *= 3;
                break;
        }
        if(gamer.getGold() >= gold){
            gamer.setGold(gamer.getGold() - gold);
            Toast.makeText(MainActivity.this, "Purchased", Toast.LENGTH_SHORT).show();
            Toast.makeText(MainActivity.this, "current gold: "+gamer.getGold(), Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(MainActivity.this, "you don't have enough gold to purchase this cheat", Toast.LENGTH_SHORT).show();
            Toast.makeText(MainActivity.this, "current gold: "+gamer.getGold(), Toast.LENGTH_SHORT).show();
            switch(gold) {
                case 60:
                    dmgModifier /= 2;
                    break;
                case 100:
                    dmgModifier /= 3;
                    break;
            }
        }
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
            case R.id.restart:
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Restart?")
                        .setMessage("Are you sure you want to restart the game?")
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface arg0, int arg1) {
                                gamer.restart();
                                gamer.setMonster(null);
                                stopService(serviceIntent);
                                changeActivity(MainMenuActivity.class);
                            }
                        }).create().show();
                break;
            case R.id.stopMusic:
                stopService(serviceIntent);
                break;
            case R.id.exit:
                FirebaseDatabase.getInstance().getReference("Users").child(account).child("loggedIn").setValue(false);
                finish();
                System.exit(0);
                break;

            case R.id.difficulty:
                final String[] difficultyArr = {"normal", "hard", "insane"};
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("choose difficulty");
                builder.setItems(difficultyArr, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String selectedDifficulty = difficultyArr[which];
                        FirebaseDatabase.getInstance().getReference("Users")
                                .child(account).child("difficulty").setValue(selectedDifficulty);
                        Intent intent = new Intent(MainActivity.this, MainActivity.class);
                        intent.putExtra("account", account);
                        MainActivity.this.startActivity(intent);
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.show();
                break;
            case R.id.logOut:
                mAuth.signOut();
                FirebaseDatabase.getInstance().getReference("Users").child(account).child("loggedIn").setValue(false);
                Intent newActiv = new Intent(MainActivity.this, LoginActivity.class);
                mAuth.signOut();
                stopService(serviceIntent);
                newActiv.putExtra("account", account);
                newActiv.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                MainActivity.this.startActivity(newActiv);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void createTimer(int start){ //create a timer for some time and if 5 sec pass reset monster damage animation
        timer = new CountDownTimer(start * 1000L, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                timerView.setText("Time left: " + millisUntilFinished / 1000);
                Log.i("Main", "Countdown seconds remaining" + millisUntilFinished / 1000);
                gamer.setTimeLeft(((int) millisUntilFinished / 1000));

                dmgChangeAnimation++;
                if(dmgChangeAnimation >= 5) {
                    randomMonsterIMG(monster);
                    dmgChangeAnimation = 0;
                }
            }

            @Override
            public void onFinish() {
                gamer.restart();
                changeActivity(LoseActivity.class);
            }
        };
        timer.start();
    }

    public void mediaPlayerMenu(String level) { //method for playing music when someone picks it in menu
        this.stopService(serviceIntent);

        serviceIntent.putExtra("lvl", level);
        this.startService(serviceIntent);
    }

    public void changeActivity(Class cl){ //change activities and delete previous to stop users from accessing previous monster
        FirebaseDatabase.getInstance().getReference("Users")
                .child(account).setValue(gamer);
        Intent newActiv = new Intent(MainActivity.this, cl);
        newActiv.putExtra("account", account);
        newActiv.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        MainActivity.this.startActivity(newActiv);
        finish();
    }

    @Override
    protected void onStop() { //when user puts app in background stop timer
        myRef.child("loggedIn").setValue(false);
        timer.cancel();
        super.onStop();
    }

    @Override
    protected void onRestart() { //when user enters the app from background after stop restart timer at the previously saved time
        super.onRestart();
        myRef.child("loggedIn").setValue(true);
        createTimer(gamer.getTimeLeft());
    }

    private File getImageFile() throws IOException
    {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageName = "fpg_"+timeStamp+"_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File imageFile = File.createTempFile(imageName, ".jpg", storageDir);
        currentImagePath = imageFile.getAbsolutePath();
        return imageFile;
    }

    private void saveImage(Bitmap bitmap) {//save an image to internal storage
        FileOutputStream fos;
        try {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                ContentResolver resolver = getContentResolver();
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "userImage:"+account+ ".jpg");
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg");
                Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                fos = (FileOutputStream) resolver.openOutputStream(Objects.requireNonNull(imageUri));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                Log.i("filePath",getRealPathFromUri(MainActivity.this, imageUri));
                Objects.requireNonNull(fos);
                gamer = new AdvancedGamer(gamer.getEmail(), gamer.getPassword(), gamer.getName(),
                        gamer.getWeapon(), gamer.getMonster(),gamer.getLevel(), gamer.getGold(), gamer.getDifficulty(),
                        getRealPathFromUri(MainActivity.this, imageUri));
                FirebaseDatabase.getInstance().getReference("Users")
                        .child(account).setValue(gamer);
            }
        }catch (Exception e){
            Log.d("error", e.toString());
        }
    }

    private String getRealPathFromUri(Context context, Uri uri){ //get path from uri
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
        if(cursor != null){
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        return null;
    }

    Bitmap imageBitmap;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) { //gets the image retrieved from the gallery
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            profilePic.setImageBitmap(imageBitmap);
            saveImage(imageBitmap);//saves the pic in the phone
        }
    }
}