package com.example.marketxcell;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pl.droidsonroids.gif.GifImageView;

public class ProfileActivity extends AppCompatActivity {

    EditText editTextname, editTextemail, editTextMobileNumber, editTextNICNumber, editTextAddress;
    Button buttonupdate, buttonChangePassword, buttonChangeSecQues;
    String address, email, useridNumber, name, password, mobileNumber,
            photoURL, sequrityAnswerOne, sequrityAnswerTwo, sequrityQuesOne, sequrityQuesTwo;
    ImageView imageViewprofilepic;
    Uri filePath;
    ProgressDialog progressDialog;
    DatabaseReference mDatabaseRef;
    StorageReference mStorageRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        SharedPreferences sharedPreferences = getSharedPreferences("agentprefs", Context.MODE_PRIVATE);
        useridNumber = sharedPreferences.getString("agentid", "");
        menucomponents();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Users");
        mStorageRef = FirebaseStorage.getInstance().getReference("agentIMG"); // Assuming "uploads" is your storage folder


        editTextname = (EditText) findViewById(R.id.profilepage_name);
        editTextemail = (EditText) findViewById(R.id.profilepage_email);
        editTextMobileNumber = (EditText) findViewById(R.id.profilepage_mobileNumber);
        editTextNICNumber = (EditText) findViewById(R.id.profilepage_NICNumber);
        editTextAddress = (EditText) findViewById(R.id.profilepage_address);
        buttonupdate = (Button) findViewById(R.id.profilepage_updatebtn);
        buttonChangePassword = (Button) findViewById(R.id.profilepage_changePassword);
        imageViewprofilepic = (ImageView) findViewById(R.id.profilepage_profilePic);
        buttonChangeSecQues = (Button) findViewById(R.id.profilepage_changeSecQes);


        editTextNICNumber.setFocusable(false);
        setLoadingText();
        personalData(useridNumber);

        imageViewprofilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        buttonupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                buttonupdate.setVisibility(View.GONE);
                name = editTextname.getText().toString();
                String newEmail = editTextemail.getText().toString();
                mobileNumber = editTextMobileNumber.getText().toString();
                useridNumber = editTextNICNumber.getText().toString();
                address = editTextAddress.getText().toString();

                if(name.isEmpty()){
                    Toast.makeText(ProfileActivity.this, "Name field is required", Toast.LENGTH_SHORT).show();
                } else if ( name.matches(".*\\d.*")) {
                    Toast.makeText(ProfileActivity.this, "Enter a valid name", Toast.LENGTH_SHORT).show();
                } else if (newEmail.isEmpty()) {
                    Toast.makeText(ProfileActivity.this, "Email field is required", Toast.LENGTH_SHORT).show();
                } else if (isValidEmail(newEmail)) {
                    Toast.makeText(ProfileActivity.this, "Enter a valid email", Toast.LENGTH_SHORT).show();
                } else if(mobileNumber.isEmpty()){
                    Toast.makeText(ProfileActivity.this, "Mobile number field is required", Toast.LENGTH_SHORT).show();
                } else if (mobileNumber.length() < 10) {
                    Toast.makeText(ProfileActivity.this, "Enter a valid mobile number", Toast.LENGTH_SHORT).show();
                } else if (useridNumber.isEmpty()) {
                    Toast.makeText(ProfileActivity.this, "NIC number field is required", Toast.LENGTH_SHORT).show();
                } else if (useridNumber.length() < 10) {
                    Toast.makeText(ProfileActivity.this, "Enter a valid ID number", Toast.LENGTH_SHORT).show();
                } else if (address.isEmpty()) {
                    Toast.makeText(ProfileActivity.this, "Address field is required", Toast.LENGTH_SHORT).show();
                } else{

                    if(!(newEmail.equals(email))){
                        DatabaseReference agentRef = FirebaseDatabase.getInstance().getReference("Users");
                        agentRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    for (DataSnapshot agentSnapshot : dataSnapshot.getChildren()) {

                                        String email = agentSnapshot.child("email").getValue(String.class);

                                        if (email.equals(newEmail)) {
                                            buttonupdate.setVisibility(View.VISIBLE);
                                            Toast.makeText(ProfileActivity.this, "Email address is already in use", Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                    buttonupdate.setVisibility(View.VISIBLE);
                    Intent intentNext = new Intent(ProfileActivity.this,PasswordValidationActivity.class);
                    intentNext.putExtra("name",name);
                    intentNext.putExtra("email",newEmail);
                    intentNext.putExtra("password",password);
                    intentNext.putExtra("mobileNumber",mobileNumber);
                    intentNext.putExtra("idNumber",useridNumber);
                    intentNext.putExtra("address",address);
                    intentNext.putExtra("from","settings");

                    startActivity(intentNext);

                }
            }
        });

        buttonChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentNext = new Intent(ProfileActivity.this,PasswordValidationActivity.class);
                intentNext.putExtra("password",password);
                intentNext.putExtra("idNumber",useridNumber);
                intentNext.putExtra("from","changepassword");
                startActivity(intentNext);
            }
        });

        buttonChangeSecQues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentNext = new Intent(ProfileActivity.this,PasswordValidationActivity.class);
                intentNext.putExtra("password",password);
                intentNext.putExtra("idNumber",useridNumber);
                intentNext.putExtra("from","changesecQues");
                startActivity(intentNext);
            }
        });

    }

    public void personalData(String userid){

        DatabaseReference agentRef = FirebaseDatabase.getInstance().getReference("Users");
        agentRef.child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    address = dataSnapshot.child("address").getValue(String.class);
                    email = dataSnapshot.child("email").getValue(String.class);
                    name = dataSnapshot.child("name").getValue(String.class);
                    password = dataSnapshot.child("password").getValue(String.class);
                    mobileNumber = dataSnapshot.child("phone").getValue(String.class);
                    photoURL = dataSnapshot.child("photo").getValue(String.class);
                    sequrityAnswerOne = dataSnapshot.child("sequrityAnswerOne").getValue(String.class);
                    sequrityAnswerTwo = dataSnapshot.child("sequrityAnswerTwo").getValue(String.class);
                    sequrityQuesOne = dataSnapshot.child("sequrityQuesOne").getValue(String.class);
                    sequrityQuesTwo = dataSnapshot.child("sequrityQuesTwo").getValue(String.class);
                }

                Glide.with(getApplicationContext()).load(photoURL).into(imageViewprofilepic);
                editTextname.setText(name);
                editTextemail.setText(email);
                editTextMobileNumber.setText(mobileNumber);
                editTextNICNumber.setText(userid);
                editTextAddress.setText(address);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void setLoadingText(){
        editTextname.setText("Loading...");
        editTextemail.setText("Loading...");
        editTextMobileNumber.setText("Loading...");
        editTextNICNumber.setText("Loading...");
        editTextAddress.setText("Loading...");
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

    public void menucomponents(){

        LinearLayout cartButton = findViewById(R.id.cartbtn);
        LinearLayout MarketButton = findViewById(R.id.marketbtn);
        LinearLayout OrdersButton = findViewById(R.id.ordersbtn);
        LinearLayout ProfileButton = findViewById(R.id.Profilebtn);

        ImageView activeIcon = ProfileButton.findViewById(R.id.imageVieddw6);
        TextView activeText = ProfileButton.findViewById(R.id.texttitlf2e);


        activeIcon.setColorFilter(getResources().getColor(R.color.PrimaryColor), PorterDuff.Mode.SRC_IN);
        activeText.setTextColor(getResources().getColor(R.color.PrimaryColor));

        cartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });

        MarketButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this,MarketActivity.class);
                startActivity(intent);
            }
        });

        OrdersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this,OrdersActivity.class);
                startActivity(intent);
            }
        });

        ProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this,ProfileActivity.class);
                startActivity(intent);
            }
        });

    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && data != null && data.getData() != null) {
            filePath = data.getData();
            uploadImage();
        }
    }

    private void uploadImage() {
        if (filePath != null) {
            progressDialog.show();

            StorageReference ref = mStorageRef.child("images/" + useridNumber);
            ref.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();

                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            updateImageUrlToDatabase(uri.toString());
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(ProfileActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void updateImageUrlToDatabase(String imageUrl) {

        Map<String, Object> updateData = new HashMap<>();
        updateData.put("photo", imageUrl);

        mDatabaseRef.child(useridNumber).updateChildren(updateData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ProfileActivity.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                        recreate();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProfileActivity.this, "Failed to update image URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}