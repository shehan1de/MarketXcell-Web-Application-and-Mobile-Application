package com.example.marketxcell;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.marketxcell.Class.AgentClass;
import com.example.marketxcell.Class.PasswordManagerClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import pl.droidsonroids.gif.GifImageView;

public class Register_three extends AppCompatActivity {

    LinearLayout backBtn;
    Spinner spinnerSequrityQuesOne, spinnerSequrityQuesTwo;
    EditText editTextSequrityAnswerOne, editTextSequrityAnswerTwo;
    Button ButtonRegister;
    GifImageView gifImageView;
    String from;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_three);

        PasswordManagerClass passwordManagerClass = new PasswordManagerClass();

        // Intent Data
        Intent intentNext = getIntent();
        String name = intentNext.getStringExtra("name");
        String email = intentNext.getStringExtra("email");
        String password = intentNext.getStringExtra("password");
        String hashedPassword = passwordManagerClass.hashPassword(password);

        String mobileNumber = intentNext.getStringExtra("mobileNumber");
        String idNumber = intentNext.getStringExtra("idNumber");
        String address = intentNext.getStringExtra("address");
        from = intentNext.getStringExtra("from");
        String profilePicture = "https://firebasestorage.googleapis.com/v0/b/marketxcell-a2edb.appspot.com/o/agentIMG%2Fblank.jpg?alt=media&token=279c703d-b669-4d8a-b843-e46d735f4b41";

        // Component View
        backBtn = (LinearLayout) findViewById(R.id.Register_Page_Three_backbtn);
        spinnerSequrityQuesOne = (Spinner) findViewById(R.id.Register_Page_Three_spinner_Q1);
        spinnerSequrityQuesTwo = (Spinner) findViewById(R.id.Register_Page_Three_spinner_Q2);
        editTextSequrityAnswerOne = (EditText) findViewById(R.id.Register_Page_Three_txt_Q1Answer);
        editTextSequrityAnswerTwo = (EditText) findViewById(R.id.Register_Page_Three_txt_Q2Answer);
        ButtonRegister = (Button) findViewById(R.id.Register_Page_Three_btn_register);
        gifImageView = (GifImageView) findViewById(R.id.Register_Page_Three_loadingGif);

        if(from.equals("changesecQues")){
            ButtonRegister.setText("Update");
        }
        // Security Question One
        String QuestionsListOne[] = {"Select", "In what city were you born?", "What is the name of your favorite pet?",
                "What is your mother's maiden name?" };
        ArrayAdapter<String> adapterone = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, QuestionsListOne);
        adapterone.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSequrityQuesOne.setAdapter(adapterone);

        // Security Question Two
        String QuestionsListTwo[] = {"Select", "What is your favorite book or movie?", "What is your favorite vacation destination?",
                "What is the model of your first car?" };
        ArrayAdapter<String> adaptertwo = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, QuestionsListTwo);
        adaptertwo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSequrityQuesTwo.setAdapter(adaptertwo);


        // Back button
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Register Button
        ButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String SequrityQuesOne = spinnerSequrityQuesOne.getSelectedItem().toString();
                String SequrityAnswerOne = editTextSequrityAnswerOne.getText().toString().trim();
                String SequrityQuesTwo = spinnerSequrityQuesTwo.getSelectedItem().toString().trim();
                String SequrityAnswerTwo = editTextSequrityAnswerTwo.getText().toString();
                SharedPreferences sharedPreferences = getSharedPreferences("agentprefs", Context.MODE_PRIVATE);
                String userIdSP = sharedPreferences.getString("agentid", "");

                if(SequrityQuesOne=="Select" || SequrityQuesTwo=="Select"){
                    Toast.makeText(Register_three.this, "Please select a security questions", Toast.LENGTH_SHORT).show();
                } else if (SequrityAnswerOne.isEmpty() || SequrityAnswerTwo.isEmpty()) {
                    Toast.makeText(Register_three.this, "Please fill answers for security questions", Toast.LENGTH_SHORT).show();
                } else{
                    if(from.equals("changesecQues")){
                        Map<String, Object> SequrityQues = new HashMap<>();
                        SequrityQues.put("sequrityQuesOne", SequrityQuesOne);
                        SequrityQues.put("sequrityAnswerOne", SequrityAnswerOne);
                        SequrityQues.put("sequrityQuesTwo", SequrityQuesTwo);
                        SequrityQues.put("sequrityAnswerTwo", SequrityAnswerTwo);

                        DatabaseReference agentRef = FirebaseDatabase.getInstance().getReference("Users").child(userIdSP);
                        agentRef.updateChildren(SequrityQues).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(Register_three.this, "Security questions are updated successfully", Toast.LENGTH_SHORT).show();
                                    Intent intentpass = new Intent(Register_three.this, ProfileActivity.class);
                                    finishAffinity();
                                    startActivity(intentpass);
                                } else{
                                    Toast.makeText(Register_three.this, "Security questions update failed. Try again", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else{
                        if(SequrityQuesOne=="Select" || SequrityQuesTwo=="Select"){
                            Toast.makeText(Register_three.this, "Please select a security questions", Toast.LENGTH_SHORT).show();
                        } else if (SequrityAnswerOne.isEmpty() || SequrityAnswerTwo.isEmpty()) {
                            Toast.makeText(Register_three.this, "Please fill answers for security questions", Toast.LENGTH_SHORT).show();
                        } else{
                            ButtonRegister.setVisibility(View.GONE);
                            gifImageView.setVisibility(View.VISIBLE);
                            AgentClass agentClass = new AgentClass(name,email,hashedPassword,mobileNumber,idNumber,address,SequrityQuesOne,SequrityAnswerOne,SequrityQuesTwo,SequrityAnswerTwo,profilePicture,"0");
                            DatabaseReference agentRef = FirebaseDatabase.getInstance().getReference("Users");
                            agentRef.child(idNumber).setValue(agentClass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(Register_three.this, "Agent Created Successfully", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(Register_three.this, Login_page.class);
                                        startActivity(intent);
                                    }else{
                                        Toast.makeText(Register_three.this, "Registration Fail, Try again", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(Register_three.this, Register_page.class);
                                        startActivity(intent);
                                    }
                                }
                            });
                        }
                    }
                }

            }
        });
    }
}