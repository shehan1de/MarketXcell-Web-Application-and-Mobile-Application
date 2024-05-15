package com.example.marketxcell;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import pl.droidsonroids.gif.GifImageView;

public class Change_Password extends AppCompatActivity {

    EditText editTextpassword;
    EditText editTextconfirmPassword;
    Button buttonresetPassword;
    LinearLayout backBtn;
    GifImageView gifImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        editTextpassword = (EditText) findViewById(R.id.Change_password_page_txt_new_password);
        editTextconfirmPassword = (EditText) findViewById(R.id.Change_password_page_txt_Confirm_password);
        buttonresetPassword = (Button) findViewById(R.id.Change_password_page_btn_PasswordReset);
        backBtn = (LinearLayout) findViewById(R.id.Change_password_page_backbtn);
        gifImageView = (GifImageView) findViewById(R.id.Change_password_page_loadingGif);

        Intent newIntent = getIntent();
        String nicNumber = newIntent.getStringExtra("nicNumber");


        // Backbtn
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back = new Intent(Change_Password.this, Securityquestionsvalidate.class);
                startActivity(back);
            }
        });

        buttonresetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = editTextpassword.getText().toString().trim();
                String confirmPassword = editTextconfirmPassword.getText().toString().trim();
                if (password.isEmpty()) {
                    Toast.makeText(Change_Password.this, "Password field is required", Toast.LENGTH_SHORT).show();
                } else if (password.length()<6) {
                    Toast.makeText(Change_Password.this, "Passowrd length must more than six", Toast.LENGTH_SHORT).show();
                } else if (!confirmPassword.equals(password)) {
                    Toast.makeText(Change_Password.this, "Confirm password not matched", Toast.LENGTH_SHORT).show();
                } else{
                    buttonresetPassword.setVisibility(View.GONE);
                    gifImageView.setVisibility(View.VISIBLE);

                    Map<String, Object> passwordMap = new HashMap<>();
                    passwordMap.put("password", password);

                    DatabaseReference agentRef = FirebaseDatabase.getInstance().getReference("Users");
                    agentRef.child(nicNumber).updateChildren(passwordMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                            if(error == null){
                                Toast.makeText(Change_Password.this, "Passowrd is updated successfully", Toast.LENGTH_SHORT).show();
                                Intent intentpass = new Intent(Change_Password.this, Login_page.class);
                                finishAffinity();
                                startActivity(intentpass);
                            }else{
                                Toast.makeText(Change_Password.this, "Password update failed. Try again", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });


    }
}