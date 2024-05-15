package com.example.marketxcell;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import pl.droidsonroids.gif.GifImageView;

public class Securityquestionsvalidate extends AppCompatActivity {

    LinearLayout backBtn;
    EditText editTextSecQuesOne;
    EditText editTextSeqQuesTwo;
    TextView textViewQ1, textViewQ2;
    Button buttonverify;
    GifImageView gifImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_securityquestionsvalidate);

        backBtn = (LinearLayout) findViewById(R.id.Validate_backbtn);
        editTextSecQuesOne = (EditText) findViewById(R.id.Validate_txt_Q1Answer);
        editTextSeqQuesTwo = (EditText) findViewById(R.id.Validate_txt_Q2Answer);
        buttonverify = (Button) findViewById(R.id.Validate_btn_verify);
        gifImageView = (GifImageView) findViewById(R.id.Validate_loadingGif);
        textViewQ1 = (TextView) findViewById(R.id.Validate_Q1);
        textViewQ2 = (TextView) findViewById(R.id.Validate_Q2);

        buttonverify.setVisibility(View.GONE);

        Intent intent = getIntent();
        String nicNumber = intent.getStringExtra("idnumber");
        String Q1 = intent.getStringExtra("Q1");
        String Q2 = intent.getStringExtra("Q2");
        String Q1Answer = intent.getStringExtra("Q1Answer");
        String Q2Answer = intent.getStringExtra("Q2Answer");


        // Set Security questions to Text Field
        textViewQ1.setText(Q1);
        textViewQ2.setText(Q2);

        buttonverify.setVisibility(View.VISIBLE);

        //back button
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //Verifty Data
        buttonverify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonverify.setVisibility(View.GONE);
                gifImageView.setVisibility(View.VISIBLE);

                String Q1UserAnswer = editTextSecQuesOne.getText().toString().trim();
                String Q2UserAnswer = editTextSeqQuesTwo.getText().toString().trim();

                if(Q1UserAnswer.isEmpty() || Q2UserAnswer.isEmpty()){
                    Toast.makeText(Securityquestionsvalidate.this, "All fields are required", Toast.LENGTH_SHORT).show();
                    buttonverify.setVisibility(View.VISIBLE);
                    gifImageView.setVisibility(View.GONE);
                } else if(!(Q1UserAnswer.equals(Q1Answer) && Q2UserAnswer.equals(Q2Answer))){
                    Toast.makeText(Securityquestionsvalidate.this, "Security questions one or two is wrong!", Toast.LENGTH_SHORT).show();
                    buttonverify.setVisibility(View.VISIBLE);
                    gifImageView.setVisibility(View.GONE);
                } else{
                    Intent newIntent = new Intent(Securityquestionsvalidate.this, Change_Password.class);
                    newIntent.putExtra("nicNumber",nicNumber);
                    startActivity(newIntent);
                }

            }
        });
    }


}