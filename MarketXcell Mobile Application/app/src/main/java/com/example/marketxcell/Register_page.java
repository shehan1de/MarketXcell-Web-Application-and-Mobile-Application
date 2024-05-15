package com.example.marketxcell;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pl.droidsonroids.gif.GifImageView;

public class Register_page extends AppCompatActivity {

    LinearLayout backBtn;
    EditText nameText, emailText, passwordText, confirmPasswordText;
    Button RegisterNextBtn;
    GifImageView gifImageView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);

        backBtn = (LinearLayout) findViewById(R.id.Register_page_backBtn);
        nameText = (EditText) findViewById(R.id.Register_Page_txt_name);
        emailText = (EditText) findViewById(R.id.Register_Page_txt_email);
        passwordText = (EditText) findViewById(R.id.Regster_Page_txt_password);
        confirmPasswordText = (EditText) findViewById(R.id.Register_Page_txt_Confirm_password);
        RegisterNextBtn = (Button) findViewById(R.id.Register_page_btn_Registernext);
        gifImageView = (GifImageView) findViewById(R.id.Register_page_two_loadingGif);


        // Back button
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back = new Intent(Register_page.this, Welcome_pageActivity.class);
                startActivity(back);
            }
        });

        // Register button
        RegisterNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = nameText.getText().toString().trim();
                String email = emailText.getText().toString().trim().toLowerCase();
                String password = passwordText.getText().toString().trim();
                String confirmPassword = confirmPasswordText.getText().toString().trim();

                if(name.isEmpty()){
                    Toast.makeText(Register_page.this, "Name field is required", Toast.LENGTH_SHORT).show();
                } else if ( name.matches(".*\\d.*")) {
                    Toast.makeText(Register_page.this, "Enter a valid name", Toast.LENGTH_SHORT).show();
                } else if (email.isEmpty()) {
                    Toast.makeText(Register_page.this, "Email field is required", Toast.LENGTH_SHORT).show();
                } else if (isValidEmail(email)) {
                    Toast.makeText(Register_page.this, "Enter a valid email", Toast.LENGTH_SHORT).show();
                } else if (password.isEmpty()) {
                    Toast.makeText(Register_page.this, "Password field is required", Toast.LENGTH_SHORT).show();
                } else if (password.length()<6) {
                    Toast.makeText(Register_page.this, "Passowrd length must more than six", Toast.LENGTH_SHORT).show();
                } else if (!confirmPassword.equals(password)) {
                    Toast.makeText(Register_page.this, "Confirm password not matched", Toast.LENGTH_SHORT).show();
                } else{
                    RegisterNextBtn.setVisibility(View.GONE);
                    gifImageView.setVisibility(View.VISIBLE);
                    DatabaseReference agentRef = FirebaseDatabase.getInstance().getReference("Users");
                    agentRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot agentSnapshot : dataSnapshot.getChildren()) {

                                    String agentemail = agentSnapshot.child("email").getValue(String.class);

                                    if (agentemail.equals(email)) {
                                        Toast.makeText(Register_page.this, "Email address is already in use", Toast.LENGTH_SHORT).show();
                                        RegisterNextBtn.setVisibility(View.VISIBLE);
                                        gifImageView.setVisibility(View.GONE);
                                        return;
                                    }
                                }
                            Intent intent = new Intent(Register_page.this,Register_two.class);
                            intent.putExtra("name",name);
                            intent.putExtra("email",email);
                            intent.putExtra("password",password);
                            startActivity(intent);

                            } else {
                                System.out.println("No data found in the 'agent' table.");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

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


}