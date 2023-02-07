package com.example.finalproject1;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Application;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoseActivity extends AppCompatActivity implements View.OnClickListener{

    TextView lose;
    String account;
    Gamer gamer;

    FirebaseAuth mAuth;
    Intent serviceIntent;

    private static final int CONTACT_PICK_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lose_screen);

        //Intent serviceIntent;
        serviceIntent = new Intent(this, MusicService.class); //create music service

        lose = findViewById(R.id.lose);

        lose.setOnClickListener(this);

        gamer = new Gamer();

        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_CONTACTS},1);
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.SEND_SMS},1);

        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null){
            account = mAuth.getCurrentUser().getUid();
        }
        else{
            Intent intent = getIntent();
            account = intent.getStringExtra("account");
        }

        FirebaseDatabase.getInstance().getReference("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                gamer = snapshot.child(account).getValue(Gamer.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Firebase error", error.getMessage());
            }
        });

        dialogMake();
    }


    @Override
    public void onClick(View v) {
        Intent i=new Intent(this, MainMenuActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.putExtra("account", account);
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
        Intent i=new Intent(this, MainMenuActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.putExtra("account", account);
        startActivity(i);
        super.onBackPressed();
    }

    public void dialogMake(){
        new AlertDialog.Builder(this)
                .setTitle("Share")
                .setMessage("You Lost :( ... Do you want to share you results?")
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent smsIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                        startActivityForResult(smsIntent, CONTACT_PICK_CODE);
                        }
                }).create().show();


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == CONTACT_PICK_CODE){
                Cursor cursor1, cursor2;
                Uri uri = data.getData();

                cursor1 = getContentResolver().query(uri, null, null, null, null);

                if(cursor1.moveToFirst()){
                    @SuppressLint("Range") String contactID = cursor1.getString(cursor1.getColumnIndex(ContactsContract.Contacts._ID));
                    @SuppressLint("Range") String contactName = cursor1.getString(cursor1.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    @SuppressLint("Range") String idResults = cursor1.getString(cursor1.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                    int idResultHold = Integer.parseInt(idResults);

                    if(idResultHold == 1){
                        cursor2 = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID+" = "+contactID,
                                null,
                                null);
                        if(cursor2.moveToNext()){
                            @SuppressLint("Range") String contactNumber = cursor2.getString(cursor2.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            String smsMSG = "Hi " +contactName+
                                    ",  I am playing MonsterClicker, think you can beat me?, i have: "+gamer.getGold()
                                    + " gold, if you want to play too, download it in the playstore -MonsterClicker-";
                            SmsManager smsManager = SmsManager.getDefault();
                            smsManager.sendTextMessage(contactNumber, null, smsMSG, null, null);
                        }
                    }
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
            case R.id.stopMusic:
                stopService(serviceIntent);
                break;
            case R.id.exit:
                FirebaseDatabase.getInstance().getReference("Users").child(account).child("loggedIn").setValue(false);
                finish();
                System.exit(0);
                break;

            case R.id.logOut:
                mAuth.signOut();
                FirebaseDatabase.getInstance().getReference("Users").child(account).child("loggedIn").setValue(false);
                Intent newActiv = new Intent(LoseActivity.this, LoginActivity.class);
                mAuth.signOut();
                stopService(serviceIntent);
                newActiv.putExtra("account", account);
                newActiv.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                LoseActivity.this.startActivity(newActiv);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    public void mediaPlayerMenu(String level) { //method for playing music when someone picks it in menu
        this.stopService(serviceIntent);

        serviceIntent.putExtra("lvl", level);
        this.startService(serviceIntent);
        //Intent serviceIntent;
        //serviceIntent = new Intent(this, MusicService.class); //create music service
    }
    public void changeActivity(Class cl){ //change activities and delete previous to stop users from accessing previous monster
        FirebaseDatabase.getInstance().getReference("Users")
                .child(account).setValue(gamer);
        Intent newActiv = new Intent(LoseActivity.this, cl);
        newActiv.putExtra("account", account);
        newActiv.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        LoseActivity.this.startActivity(newActiv);
        finish();
    }
}
