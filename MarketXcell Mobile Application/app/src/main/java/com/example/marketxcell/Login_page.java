package com.example.marketxcell;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.marketxcell.Class.PasswordManagerClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pl.droidsonroids.gif.GifImageView;


public class Login_page extends AppCompatActivity {

    LinearLayout backBtn;
    EditText editTextemail;
    EditText editTextpassword;
    Button buttonLogin;
    GifImageView gifImageView;
    TextView textViewForgetPassword;
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        backBtn = (LinearLayout) findViewById(R.id.Login_page_backBtn);
        editTextemail = (EditText) findViewById(R.id.Login_Page_txt_email);
        editTextpassword = (EditText) findViewById(R.id.Login_Page_txt_password);
        buttonLogin = (Button) findViewById(R.id.login_page_btn_login);
        gifImageView =(GifImageView) findViewById(R.id.login_page_two_loadingGif);
        textViewForgetPassword = (TextView) findViewById(R.id.Login_page_btn_forgetPassword);

        //Back btn
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back = new Intent(Login_page.this, Welcome_pageActivity.class);
                startActivity(back);
            }
        });

        // Forget Password button
        textViewForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent forgetpasswordintent = new Intent(Login_page.this, Account_recovery.class);
                startActivity(forgetpasswordintent);
            }
        });


        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usenEnteredemail = editTextemail.getText().toString().trim();
                String usenEnteredpassword = editTextpassword.getText().toString().trim();

                if(usenEnteredemail.isEmpty()){
                    Toast.makeText(Login_page.this, "Email field is required", Toast.LENGTH_SHORT).show();
                } else if (isValidEmail(usenEnteredemail)) {
                    Toast.makeText(Login_page.this, "Enter a valid email", Toast.LENGTH_SHORT).show();
                } else if (usenEnteredpassword.isEmpty()) {
                    Toast.makeText(Login_page.this, "Password field is required", Toast.LENGTH_SHORT).show();
                } else if (usenEnteredpassword.length()<6) {
                    Toast.makeText(Login_page.this, "Passowrd length must more than six", Toast.LENGTH_SHORT).show();
                } else{
                    gifImageView.setVisibility(gifImageView.VISIBLE);
                    buttonLogin.setVisibility(buttonLogin.GONE);
                    DatabaseReference agentRef = FirebaseDatabase.getInstance().getReference("Users");
                    agentRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot agentSnapshot : dataSnapshot.getChildren()) {
                                    String agentemail = agentSnapshot.child("email").getValue(String.class);
                                    String agentPassword = agentSnapshot.child("password").getValue(String.class);

                                    PasswordManagerClass passwordManagerClass = new PasswordManagerClass();
                                    boolean passwordMatch = passwordManagerClass.verifyPassword(usenEnteredpassword, agentPassword);

                                    if (agentemail.equals(usenEnteredemail) && passwordMatch) {
                                        Toast.makeText(Login_page.this, "Authentication successful", Toast.LENGTH_SHORT).show();

                                        String agentid = agentSnapshot.getKey();
                                        String sales = agentSnapshot.child("sales").getValue(String.class);
                                        String name = agentSnapshot.child("name").getValue(String.class);
                                        String email = agentSnapshot.child("email").getValue(String.class);
                                        sharedPreferences = getSharedPreferences("agentprefs", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString("agentid", agentid);
                                        editor.putString("agentsales", sales);
                                        editor.putString("email", email);
                                        editor.putString("cartrefid", randomId());
                                        editor.putString("agentname", name);
                                        editor.apply();

                                        Intent intent = new Intent(Login_page.this, MarketActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                        return;
                                    }
                                }

                                Toast.makeText(Login_page.this, "Invalid username or password.", Toast.LENGTH_SHORT).show();
                                gifImageView.setVisibility(gifImageView.GONE);
                                buttonLogin.setVisibility(buttonLogin.VISIBLE);

                            } else {
                                System.out.println("No data found in the 'agent' table.");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            System.out.println("Data retrieval canceled with error: " + error.getMessage());
                        }
                    });
                }
            }
        });
    }

    public boolean isValidEmail(String email) {
        // Define a regular expression for a simple email validation
        String emailRegex = "[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher mat = pattern.matcher(email);
        if(mat.matches()){
            return false;
        }else{
            return true;
        }
    }


    public String randomId(){

        Random random = new Random();
        long lowerBound = 1000000000L;
        long upperBound = 9999999999L;
        long random10DigitNumber = lowerBound + (long) (random.nextDouble() * (upperBound - lowerBound));
        String randomNumber = Long.toString(random10DigitNumber);

        return randomNumber;
    }
}