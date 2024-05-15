package com.example.marketxcell;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import pl.droidsonroids.gif.GifImageView;

public class Register_two extends AppCompatActivity {

    EditText mobileNumberText, idNumberText, addressText;
    LinearLayout backBtn;
    Button registerNextBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_two);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String email = intent.getStringExtra("email");
        String password = intent.getStringExtra("password");

        mobileNumberText = (EditText) findViewById(R.id.Register_Page_Two_txt_mobileNumber);
        idNumberText = (EditText) findViewById(R.id.Register_Page_Two_txt_idNumber);
        addressText = (EditText) findViewById(R.id.Register_Page_Two_txt_Address);
        registerNextBtn = (Button) findViewById(R.id.Register_Page_Two_btn_next);
        backBtn = (LinearLayout) findViewById(R.id.Register_Page_Two_backbtn);


        // Back button
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back = new Intent(Register_two.this, Register_page.class);
                startActivity(back);
            }
        });

        // Register button
        registerNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mobileNumber = mobileNumberText.getText().toString().trim();
                String iDNumber = idNumberText.getText().toString().trim().toUpperCase();
                String address = addressText.getText().toString().trim();

                if(mobileNumber.isEmpty()){
                    Toast.makeText(Register_two.this, "Mobile number field is required", Toast.LENGTH_SHORT).show();
                } else if (mobileNumber.length() < 10) {
                    Toast.makeText(Register_two.this, "Enter a valid mobile number", Toast.LENGTH_SHORT).show();
                } else if (iDNumber.isEmpty()) {
                    Toast.makeText(Register_two.this, "NIC number field is required", Toast.LENGTH_SHORT).show();
                } else if (iDNumber.length() < 10) {
                    Toast.makeText(Register_two.this, "Enter a valid NIC number", Toast.LENGTH_SHORT).show();
                } else if (address.isEmpty()) {
                    Toast.makeText(Register_two.this, "Address field is required", Toast.LENGTH_SHORT).show();
                } else{
                    Intent intentNext = new Intent(Register_two.this,Register_three.class);
                    intentNext.putExtra("name",name);
                    intentNext.putExtra("email",email);
                    intentNext.putExtra("password",password);
                    intentNext.putExtra("mobileNumber",mobileNumber);
                    intentNext.putExtra("idNumber",iDNumber);
                    intentNext.putExtra("address",address);
                    intentNext.putExtra("from","reg2");
                    startActivity(intentNext);
                }
            }
        });

    }
}