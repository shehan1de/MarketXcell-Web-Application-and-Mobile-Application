package com.example.marketxcell;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marketxcell.Class.AgentClass;
import com.example.marketxcell.Class.PasswordManagerClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class PasswordValidationActivity extends AppCompatActivity {

    TextView textViewheading, textViewsubheading;

    Button buttonupdate;
    EditText editTextpassword;
    String from;
    LinearLayout backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_validation);

        menucomponents();

        buttonupdate = (Button) findViewById(R.id.PasswordValidation_updatebtn);
        editTextpassword = (EditText) findViewById(R.id.PasswordValidation_password);
        textViewheading = (TextView) findViewById(R.id.PasswordValidation_heading);
        textViewsubheading = (TextView) findViewById(R.id.PasswordValidation_subHeading);
        backBtn =(LinearLayout) findViewById(R.id.passwordValidation_btn);

        Intent intentNext = getIntent();
        String name = intentNext.getStringExtra("name");
        String email = intentNext.getStringExtra("email");
        String password = intentNext.getStringExtra("password"); // hashed password
        String mobileNumber = intentNext.getStringExtra("mobileNumber");
        String idNumber = intentNext.getStringExtra("idNumber");
        String address = intentNext.getStringExtra("address");
        from = intentNext.getStringExtra("from");

        if(from.equals("settings")){
            textViewheading.setText("Update User Details");
            textViewsubheading.setText("Enter The Password To Update User Details");
        } else if (from.equals("changepassword")) {
            textViewheading.setText("Change Password");
            textViewsubheading.setText("Enter The Password To Set New Password");
            buttonupdate.setText("Next");
        } else if (from.equals("changesecQues")) {
            textViewheading.setText("Change Security Questions");
            textViewsubheading.setText("Enter The Password To Change Security Questions");
            buttonupdate.setText("Next");
        }

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PasswordValidationActivity.this,MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); finish();
            }
        });

        buttonupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String userentedpassword = editTextpassword.getText().toString();

                if(from.equals("settings")){
                    updateprofile(userentedpassword,password,name,email,mobileNumber,address,idNumber);
                } else if (from.equals("changepassword")) {
                    updatepassword(userentedpassword,password,idNumber);
                } else if (from.equals("changesecQues")) {
                    updatesecques(userentedpassword,password,idNumber);
                }

            }
        });
    }

    public void updateprofile(String userentedpassword,String password,String name, String email, String mobileNumber, String address, String idNumber){

        PasswordManagerClass passwordManagerClass = new PasswordManagerClass();
        boolean passwordMatch = passwordManagerClass.verifyPassword(userentedpassword, password);

        if (userentedpassword.isEmpty()) {
            Toast.makeText(PasswordValidationActivity.this, "Password field is required", Toast.LENGTH_SHORT).show();
        } else if (userentedpassword.length()<6) {
            Toast.makeText(PasswordValidationActivity.this, "Passowrd length must more than six", Toast.LENGTH_SHORT).show();
        } else if (!passwordMatch) {
            Toast.makeText(PasswordValidationActivity.this, "Invalid password", Toast.LENGTH_SHORT).show();
        } else{
            Map<String, Object> updatedUser = new HashMap<>();
            updatedUser.put("name", name);
            updatedUser.put("email", email);
            updatedUser.put("phone", mobileNumber);
            updatedUser.put("address", address);

            DatabaseReference agentRef = FirebaseDatabase.getInstance().getReference("Users").child(idNumber);
            agentRef.updateChildren(updatedUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(PasswordValidationActivity.this, "User details updated Successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(PasswordValidationActivity.this,ProfileActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    public void updatepassword(String userentedpassword,String password, String idNumber){

        PasswordManagerClass passwordManagerClass = new PasswordManagerClass();
        boolean passwordMatch = passwordManagerClass.verifyPassword(userentedpassword, password);

        if (userentedpassword.isEmpty()) {
            Toast.makeText(PasswordValidationActivity.this, "Password field is required", Toast.LENGTH_SHORT).show();
        } else if (userentedpassword.length()<6) {
            Toast.makeText(PasswordValidationActivity.this, "Passowrd length must more than six", Toast.LENGTH_SHORT).show();
        } else if (!passwordMatch) {
            Toast.makeText(PasswordValidationActivity.this, "Invalid password", Toast.LENGTH_SHORT).show();
        } else{
            Intent intentNext = new Intent(PasswordValidationActivity.this,Change_Password.class);
            intentNext.putExtra("nicNumber",idNumber);
            startActivity(intentNext);
        }
    }

    public void updatesecques(String userentedpassword,String password, String idNumber){

        PasswordManagerClass passwordManagerClass = new PasswordManagerClass();
        boolean passwordMatch = passwordManagerClass.verifyPassword(userentedpassword, password);

        if (userentedpassword.isEmpty()) {
            Toast.makeText(PasswordValidationActivity.this, "Password field is required", Toast.LENGTH_SHORT).show();
        } else if (userentedpassword.length()<6) {
            Toast.makeText(PasswordValidationActivity.this, "Passowrd length must more than six", Toast.LENGTH_SHORT).show();
        } else if (!passwordMatch) {
            Toast.makeText(PasswordValidationActivity.this, "Invalid password", Toast.LENGTH_SHORT).show();
        } else{

            Intent intentNext = new Intent(PasswordValidationActivity.this,Register_three.class);
            intentNext.putExtra("nicNumber",idNumber);
            intentNext.putExtra("from","changesecQues");
            startActivity(intentNext);
        }
    }

    public void menucomponents(){

        LinearLayout cartButton = findViewById(R.id.cartbtn);
        LinearLayout MarketButton = findViewById(R.id.marketbtn);
        LinearLayout OrdersButton = findViewById(R.id.ordersbtn);
        LinearLayout ProfileButton = findViewById(R.id.Profilebtn);

        ImageView activeIcon = MarketButton.findViewById(R.id.imageView7);
        TextView activeText = MarketButton.findViewById(R.id.texttitle43);

        activeIcon.setColorFilter(getResources().getColor(R.color.PrimaryColor), PorterDuff.Mode.SRC_IN);
        activeText.setTextColor(getResources().getColor(R.color.PrimaryColor));

        cartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PasswordValidationActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });

        MarketButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PasswordValidationActivity.this,MarketActivity.class);
                startActivity(intent);
            }
        });

        OrdersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PasswordValidationActivity.this,OrdersActivity.class);
                startActivity(intent);
            }
        });

        ProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PasswordValidationActivity.this,ProfileActivity.class);
                startActivity(intent);
            }
        });

    }
}