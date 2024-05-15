package com.example.marketxcell;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import pl.droidsonroids.gif.GifImageView;

public class Account_recovery extends AppCompatActivity {

    LinearLayout backBtn;
    EditText editTextNicNumber;
    Button buttonNicCheck;
    GifImageView gifImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_recovery);

        backBtn = (LinearLayout) findViewById(R.id.account_recovery_page_backBtn);
        editTextNicNumber = (EditText) findViewById(R.id.account_recovery_txt_nicNumber);
        buttonNicCheck = (Button) findViewById(R.id.account_recovery_btn_nicverify);
        gifImageView = (GifImageView) findViewById(R.id.account_recovery_loadingGif);



        // Back button
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back = new Intent(Account_recovery.this, Login_page.class);
                startActivity(back);
            }
        });

        buttonNicCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userNICNumber = editTextNicNumber.getText().toString().trim();

                if(userNICNumber.isEmpty()) {
                    Toast.makeText(Account_recovery.this, "NIC number field is required", Toast.LENGTH_SHORT).show();
                } else if (userNICNumber.length() < 10) {
                    Toast.makeText(Account_recovery.this, "Enter a valid ID number", Toast.LENGTH_SHORT).show();
                } else{
                    buttonNicCheck.setVisibility(View.GONE);
                    gifImageView.setVisibility(View.VISIBLE);
                    DatabaseReference agentRef = FirebaseDatabase.getInstance().getReference("Users");
                    agentRef.child(userNICNumber).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                String securityQuesOne = snapshot.child("sequrityQuesOne").getValue(String.class);
                                String securityQuesTwo = snapshot.child("sequrityQuesTwo").getValue(String.class);
                                String securityAnswerOne = snapshot.child("sequrityAnswerOne").getValue(String.class);
                                String securityAnswerTwo = snapshot.child("sequrityAnswerTwo").getValue(String.class);

                                Intent intent = new Intent(Account_recovery.this,Securityquestionsvalidate.class);
                                intent.putExtra("Q1",securityQuesOne);
                                intent.putExtra("Q2",securityQuesTwo);
                                intent.putExtra("Q1Answer",securityAnswerOne);
                                intent.putExtra("Q2Answer",securityAnswerTwo);
                                intent.putExtra("idnumber",userNICNumber);
                                startActivity(intent);

                            } else{
                                buttonNicCheck.setVisibility(View.VISIBLE);
                                gifImageView.setVisibility(View.GONE);
                                Toast.makeText(Account_recovery.this, "Invalid NIC Number", Toast.LENGTH_SHORT).show();

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
}