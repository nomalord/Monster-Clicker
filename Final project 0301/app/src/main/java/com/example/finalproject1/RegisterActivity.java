package com.example.finalproject1;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseUser account;

    EditText regName, regEmail, regPassword;
    Button regBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_page);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Users");

        regName = findViewById(R.id.regName);
        regEmail = findViewById(R.id.logEmail);
        regPassword = findViewById(R.id.logPassword);
        regBtn = findViewById(R.id.logBtn);

        regBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(regName.getText().toString().isEmpty() || regEmail.getText().toString().isEmpty() ||
                regPassword.getText().toString().isEmpty())
            Toast.makeText(RegisterActivity.this, "Please fill out all the required parameters",
                    Toast.LENGTH_SHORT).show();
        else {
            User user = new User(regEmail.getText().toString(), regPassword.getText().toString(),
                    regName.getText().toString());
            mAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                account = mAuth.getCurrentUser();
                                updateUI(account);
                                //myRef.setValue(user.getUid());
                                FirebaseDatabase.getInstance().getReference("Users")
                                        .child(account.getUid()).setValue(user);

                                new AlertDialog.Builder(RegisterActivity.this)
                                        .setTitle("Tutorial")
                                        .setMessage("Do you want to enter the tutorial?")
                                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent intent = new Intent(view.getContext(), LoginActivity.class);
                                                view.getContext().startActivity(intent);
                                                dialog.dismiss();
                                            }
                                        })
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                            public void onClick(DialogInterface arg0, int arg1) {
                                                Uri uri = Uri.parse("https://imgur.com/a/uMpggke"); // missing 'http://' will cause crashed
                                                Intent URL = new Intent(Intent.ACTION_VIEW, uri);
                                                onBackPressed();
                                                startActivity(URL);

                                            }
                                        }).create().show();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(RegisterActivity.this, "enter a better password or an appropriate email",
                                        Toast.LENGTH_SHORT).show();
                                updateUI(null);
                            }
                        }
                    });
        }
    }
    public void updateUI(FirebaseUser account) {

        if (account != null) {
            Toast.makeText(this, "You Have Registered successfully", Toast.LENGTH_LONG).show();

        } else {
            Toast.makeText(this, "You Didn't Register successfully", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { //creating options menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_app, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) { //every options menu click do something accordingly

        switch (item.getItemId()) {
            case R.id.exit:
                finish();
                System.exit(0);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
