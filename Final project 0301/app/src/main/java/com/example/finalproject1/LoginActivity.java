package com.example.finalproject1;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseUser account;

    String dialogTxt;

    EditText logEmail, logPassword;
    Button logBtn, regOpen, forgotPass;
    Intent intent;

    String temp = "";

    User user = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Users");

        logEmail = findViewById(R.id.logEmail);
        logPassword = findViewById(R.id.logPassword);
        logBtn = findViewById(R.id.logBtn);
        regOpen = findViewById(R.id.regOpen);
        forgotPass = findViewById(R.id.forgotPass);

        logBtn.setOnClickListener(this);
        regOpen.setOnClickListener(this);
        forgotPass.setOnClickListener(this);

    }

    @Override
    public void onClick(@NonNull View view) {
        switch (view.getId()) {
            case R.id.logBtn:
                if (logEmail.getText().toString().isEmpty() || logPassword.getText().toString().isEmpty())
                    Toast.makeText(LoginActivity.this, "Please fill out all the required parameters",
                            Toast.LENGTH_SHORT).show();
                else {
                    logBtn.setClickable(false);
                    mAuth.signInWithEmailAndPassword(logEmail.getText().toString(), logPassword.getText().toString())
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information

                                        account = mAuth.getCurrentUser();

                                        myRef.child(account.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if(dataSnapshot.child("loggedIn").getValue(Boolean.class)){
                                                    Toast.makeText(LoginActivity.this, "This user is already logged in", Toast.LENGTH_SHORT).show();
                                                    logBtn.setClickable(true);
                                                }
                                                else {
                                                    Log.d(TAG, "signInWithEmail:success");
                                                    updateUI(account);
                                                    temp = dataSnapshot.child("name").getValue(String.class);
                                                    if (!dataSnapshot.hasChild("weapon")) {
                                                        user = new Gamer(logEmail.getText().toString(), //turning a user that logged in to a gamer if it wasn't already a gamer before.
                                                                logPassword.getText().toString(),
                                                                temp);
                                                        FirebaseDatabase.getInstance().getReference("Users")
                                                                .child(account.getUid()).setValue(user);
                                                        intent = new Intent(view.getContext(), MainMenuActivity.class);
                                                        intent.putExtra("music", "music");
                                                        logBtn.setClickable(true);
                                                    } else {
                                                        intent = new Intent(view.getContext(), MainMenuActivity.class);
                                                        intent.putExtra("music", "music");
                                                        logBtn.setClickable(true);
                                                    }
                                                    intent.putExtra("account", account.getUid());
                                                    view.getContext().startActivity(intent);
                                                }
                                            }
                                            @Override
                                            public void onCancelled(DatabaseError error) {
                                            }
                                        });
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                        logBtn.setClickable(true);
                                        updateUI(null);
                                    }
                                }
                            });
                }
                break;
            case R.id.regOpen:
                intent = new Intent(view.getContext(), RegisterActivity.class);
                view.getContext().startActivity(intent);
                break;
            case R.id.forgotPass:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Forgot password?");
                builder.setMessage("Enter email to reset password");

// Set up the input
                final EditText input = new EditText(this);
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                builder.setView(input);
                if(!logEmail.getText().toString().equals(""))
                    input.setText(logEmail.getText().toString());

// Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialogTxt = input.getText().toString();
                        mAuth.sendPasswordResetEmail(dialogTxt);
                        Toast.makeText(LoginActivity.this, "Sent Password Reset To Mail.", Toast.LENGTH_SHORT).show();
                        Toast.makeText(LoginActivity.this, "If nothing was sent try again later.", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
                break;
        }

    }

    public void updateUI(FirebaseUser account) {

        if (account != null) {
            Toast.makeText(this, "You Have Logged in successfully", Toast.LENGTH_LONG).show();

        } else {
            Toast.makeText(this, "You Didn't Login successfully", Toast.LENGTH_LONG).show();
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
