package com.example.finalproject1;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class LeaderboardActivity extends AppCompatActivity {
    ListView listView;
    ArrayList<Gamer> gamerList;
    GamerAdapter gamerAdapter;
    Intent serviceIntent;
    String account;
    Gamer gamer;
    ValueEventListener valueEventListenerGamers;

    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.leaderboard_page);

        serviceIntent = new Intent(this, MusicService.class); //create music service

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Users");

        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null){
            account = mAuth.getCurrentUser().getUid();
        }
        else{
            Intent intent = getIntent();
            account = intent.getStringExtra("account");
        }

        listView = findViewById(R.id.gamerLeaderboard);

        gamerList = new ArrayList<>();

        valueEventListenerGamers = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot child:snapshot.getChildren()){
                    //if(child.getValue() instanceof Gamer ||  child.getValue() instanceof AdvancedGamer)
                        if(child.getKey().equals(account)){
                            gamerList.add(child.getValue(AdvancedGamer.class));
                            gamer = child.getValue(AdvancedGamer.class);
                        }
                        else{
                            gamerList.add(child.getValue(Gamer.class));
                        }

                }

                Gamer[] gamers = gamerList.toArray(new Gamer[0]);

                bubbleSort(gamers);

                gamerList = new ArrayList<>(Arrays.asList(gamers));

                Collections.reverse(gamerList);

                ArrayList<AdvancedGamer> advancedGamers = new ArrayList<>();
                for(int i = 0; i< gamerList.size(); i++){
                    if(gamerList.get(i) instanceof AdvancedGamer)
                    {
                        advancedGamers.add(new AdvancedGamer(gamerList.get(i).getEmail(), gamerList.get(i).getPassword(),
                                gamerList.get(i).getName(), gamerList.get(i).getWeapon(), gamerList.get(i).getMonster(),
                                gamerList.get(i).getLevel(), gamerList.get(i).getGold(), gamerList.get(i).getDifficulty(),
                                ((AdvancedGamer) gamerList.get(i)).getPath()));
                        advancedGamers.get(i).setAccountCheck(true);
                    }
                    else
                        advancedGamers.add(new AdvancedGamer(gamerList.get(i).getEmail(), gamerList.get(i).getPassword(),
                                gamerList.get(i).getName(), gamerList.get(i).getWeapon(), gamerList.get(i).getMonster(),
                                gamerList.get(i).getLevel(), gamerList.get(i).getGold(), gamerList.get(i).getDifficulty()
                                ,null));

                }

                gamerAdapter = new GamerAdapter(LeaderboardActivity.this, advancedGamers);

                listView.setAdapter(gamerAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("error1", error.getMessage());
            }
        };
        myRef.addListenerForSingleValueEvent(valueEventListenerGamers);
    }
    public void bubbleSort(Gamer[] a) {
        boolean sorted = false;
        Gamer temp;
        while (!sorted) {
            sorted = true;
            for (int i = 0; i < a.length - 1; i++) {
                if (a[i].getGold() > a[i+1].getGold()) {
                    temp = a[i];
                    a[i]= a[i+1];
                    a[i+1] = temp;
                    sorted = false;
                }
            }
        }
    }
    @Override
    protected void onStop() { //when user puts app in background make it so user isnt logged in
        super.onStop();
        FirebaseDatabase.getInstance().getReference("Users").child(account).child("loggedIn").setValue(false);
    }

    @Override
    protected void onRestart() { //when user enters the app from background after stop restart the login
        super.onRestart();
        FirebaseDatabase.getInstance().getReference("Users").child(account).child("loggedIn").setValue(true);
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
                new AlertDialog.Builder(this)
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
                AlertDialog.Builder builder = new AlertDialog.Builder(LeaderboardActivity.this);
                builder.setTitle("choose difficulty");
                builder.setItems(difficultyArr, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String selectedDifficulty = difficultyArr[which];
                        FirebaseDatabase.getInstance().getReference("Users")
                                .child(account).child("difficulty").setValue(selectedDifficulty);
                        Intent intent = new Intent(LeaderboardActivity.this, MainActivity.class);
                        intent.putExtra("account", account);
                        LeaderboardActivity.this.startActivity(intent);
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
                Intent newActiv = new Intent(LeaderboardActivity.this, LoginActivity.class);
                mAuth.signOut();
                stopService(serviceIntent);
                newActiv.putExtra("account", account);
                newActiv.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                LeaderboardActivity.this.startActivity(newActiv);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    public void mediaPlayerMenu(String level) { //method for playing music when someone picks it in menu
        this.stopService(serviceIntent);

        serviceIntent.putExtra("lvl", level);
        this.startService(serviceIntent);
    }
    public void changeActivity(Class cl){ //change activities and delete previous to stop users from accessing previous monster
        FirebaseDatabase.getInstance().getReference("Users")
                .child(account).setValue(gamer);
        Intent newActiv = new Intent(LeaderboardActivity.this, cl);
        newActiv.putExtra("account", account);
        newActiv.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        LeaderboardActivity.this.startActivity(newActiv);
        finish();
    }
}
