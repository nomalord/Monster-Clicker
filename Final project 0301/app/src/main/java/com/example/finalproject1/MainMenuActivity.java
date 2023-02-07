package com.example.finalproject1;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainMenuActivity extends AppCompatActivity {

    TextView welcome;
    ImageButton play, leaderboard;
    Button onlinePlay; //two players play against each other vs a boss on insane difficulty and whoever kills the boss first wins
    String selectedDifficulty = "";
    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseAuth mAuth;
    String account;
    Gamer gamer = new Gamer();
    Intent serviceIntent;

    ValueEventListener valueEventListener, velDiffic;

    private View.OnClickListener playOnClickListener = v -> Play(v);
    private View.OnClickListener createOnClickListener = v -> onlineGame(v);
    private View.OnClickListener leaderboardOnClickListener = v -> LeaderBoard(v);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);
        serviceIntent = new Intent(MainMenuActivity.this, MusicService.class); //create music service

        Intent intent = getIntent();

        String music = intent.getStringExtra("music");

        if(music != null)
            if(music.equals("music"))
                mediaPlayerMenu("1");

        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null){
            account = mAuth.getCurrentUser().getUid();
        }
        else{
            account = intent.getStringExtra("account");
        }


        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Users").child(account);

        myRef.child("loggedIn").setValue(true);

        welcome = findViewById(R.id.welcome);
        play = findViewById(R.id.play);
        onlinePlay = findViewById(R.id.onlinePlay);
        leaderboard = findViewById(R.id.leaderboard);

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("path"))
                    gamer = dataSnapshot.getValue(AdvancedGamer.class);
                else
                    gamer = dataSnapshot.getValue(AdvancedGamer.class);
                dataSnapshot.child("name").getValue(String.class);
                welcome.setText("Welcome player " +dataSnapshot.child("name").getValue(String.class));
                play.setOnClickListener(playOnClickListener);
                onlinePlay.setOnClickListener(createOnClickListener);
                leaderboard.setOnClickListener(leaderboardOnClickListener);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.d("error1", error.getMessage());
            }
        };

        velDiffic = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.child("difficulty").getValue(String.class).equals("")){
                    Intent intent = new Intent(MainMenuActivity.this, MainActivity.class);
                    intent.putExtra("account", account);
                    MainMenuActivity.this.startActivity(intent);
                }
                else{
                    final String[] difficultyArr = {"normal", "hard", "insane"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainMenuActivity.this);
                    builder.setTitle("choose difficulty");
                    builder.setItems(difficultyArr, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            selectedDifficulty = difficultyArr[which];
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(account).child("difficulty").setValue(selectedDifficulty);
                            Intent intent = new Intent(MainMenuActivity.this, MainActivity.class);
                            intent.putExtra("account", account);
                            MainMenuActivity.this.startActivity(intent);
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
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("error1", error.getMessage());
            }
        };

        myRef.addListenerForSingleValueEvent(valueEventListener);


    }

    private void Play(View v) {
        //make dialog to choose difficulty and move to game screen
        myRef.addListenerForSingleValueEvent(velDiffic);

    }
    private void onlineGame(View v) {
        FirebaseDatabase.getInstance().getReference()
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot child: snapshot.getChildren()){
                            if(child.getKey().contains("Online Game")) {
                                if (child.hasChild("time player 1"))
                                    if(child.child("time player 1").getValue(Integer.class) == 0)
                                        child.getRef().removeValue();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                changeActivity(OnlineActivity.class);
            }
        }, 500);


        //move to the online game screen
    }
    private void LeaderBoard(View v) {
        changeActivity(LeaderboardActivity.class);
        //move to the leaderboard screen
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Really Exit?")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onResume();
                    }
                })
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent newActiv = new Intent(MainMenuActivity.this, LoginActivity.class);
                        mAuth.signOut();
                        stopService(serviceIntent);
                        newActiv.putExtra("account", account);
                        MainMenuActivity.this.startActivity(newActiv);
                    }
                }).create().show();
    }


    public void changeActivity(Class cl){ //change activities and delete previous to stop users from accessing previous monster
        Intent newActiv = new Intent(MainMenuActivity.this, cl);
        newActiv.putExtra("account", account);
        FirebaseDatabase.getInstance().getReference("Users").child(account).setValue(gamer);
        MainMenuActivity.this.startActivity(newActiv);
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
                new AlertDialog.Builder(MainMenuActivity.this)
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
                AlertDialog.Builder builder = new AlertDialog.Builder(MainMenuActivity.this);
                builder.setTitle("choose difficulty");
                builder.setItems(difficultyArr, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectedDifficulty = difficultyArr[which];
                        FirebaseDatabase.getInstance().getReference("Users")
                                .child(account).child("difficulty").setValue(selectedDifficulty);
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
                Intent newActiv = new Intent(MainMenuActivity.this, LoginActivity.class);
                mAuth.signOut();
                stopService(serviceIntent);
                newActiv.putExtra("account", account);
                newActiv.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                MainMenuActivity.this.startActivity(newActiv);
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
