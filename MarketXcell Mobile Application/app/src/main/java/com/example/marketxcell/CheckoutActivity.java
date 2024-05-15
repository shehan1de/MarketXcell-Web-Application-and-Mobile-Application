package com.example.marketxcell;

import androidx.annotation.NonNull;
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

import com.example.marketxcell.model.OrderModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class CheckoutActivity extends AppCompatActivity {

    EditText editTextClientName, editTextClientAddress;
    Spinner spinnerpaymentOption;
    LinearLayout backBtn;
    Button buttonplaceOrder;
    String userId, agentEmail, agentName, UniqueOrderid;
    String cardfid;
    float totalbill,totalcommission, CouponDiscount, orderValue;
    String couponCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        SharedPreferences sharedPreferences = getSharedPreferences("agentprefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("agentid", "");
        agentName = sharedPreferences.getString("agentname", "");
        cardfid = sharedPreferences.getString("cartrefid", "");
        agentEmail = sharedPreferences.getString("email", "");

        editTextClientName = (EditText) findViewById(R.id.CheckoutPage_clientName);
        editTextClientAddress = (EditText) findViewById(R.id.CheckoutPage_clientAddress);
        spinnerpaymentOption = (Spinner) findViewById(R.id.checkoutPage_paymentOptions);
        buttonplaceOrder = (Button) findViewById(R.id.checkoutPage_placeOrder);
        backBtn = (LinearLayout) findViewById(R.id.Checkout_page_backBtn);

        //Get IntentValue


        Intent intent = getIntent();
        totalcommission = intent.getFloatExtra("TotalCommission",0);
        totalbill = intent.getFloatExtra("totalsales",0);
        CouponDiscount = intent.getFloatExtra("CouponDiscountValve",0);
        couponCode = intent.getStringExtra("couponCode");
        orderValue = totalbill- CouponDiscount;
        String orderValueinString = Float.toString(orderValue);
        String offerclamedValueinString = Float.toString(CouponDiscount);
        String totalcommissioninString = Float.toString(totalcommission);

        //Payment Option
        String paymentOption[] = {"Select", "Cash On Delivery"};
        ArrayAdapter<String> adapterone = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, paymentOption);
        adapterone.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerpaymentOption.setAdapter(adapterone);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        buttonplaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String clientName = editTextClientName.getText().toString();
                String clientAddress = editTextClientAddress.getText().toString();
                String paymentMode = spinnerpaymentOption.getSelectedItem().toString();

                if(clientName.isEmpty()){
                    Toast.makeText(CheckoutActivity.this, "Name field is required", Toast.LENGTH_SHORT).show();
                } else if ( clientName.matches(".*\\d.*")) {
                    Toast.makeText(CheckoutActivity.this, "Enter a valid name", Toast.LENGTH_SHORT).show();
                } else if (clientAddress.isEmpty()) {
                    Toast.makeText(CheckoutActivity.this, "Address field is required", Toast.LENGTH_SHORT).show();
                } else if (clientAddress.length() <2) {
                    Toast.makeText(CheckoutActivity.this, "Invalid Address", Toast.LENGTH_SHORT).show();
                } else if(paymentMode == "Select") {
                    Toast.makeText(CheckoutActivity.this, "Please Select a Payment Option", Toast.LENGTH_SHORT).show();
                } else{

                    updateProductCartCount(userId,cardfid);
                    addOrder(userId,cardfid,orderValueinString,clientName,clientAddress,"Processing",couponCode,offerclamedValueinString,totalcommissioninString,paymentMode);
                    updateAgentSales(userId,orderValueinString);
                    sendEmail(agentEmail,UniqueOrderid,agentName,clientName,clientAddress,orderValueinString,totalcommissioninString);
                    changeCartId();
                    Intent intent1 = new Intent(CheckoutActivity.this,MarketActivity.class);
                    startActivity(intent1);
                }
            }
        });


    }

    public void addOrder(String userId,String cardfid,String orderValueinString,String clientName,String clientAddress,String orderStatus,String couponCode,String offerclamedValueinString,String totalcommissioninString, String paymentMode){

        Random random = new Random();
        int randomNumber = random.nextInt(90000000) + 10000000;
        UniqueOrderid = "Order-00"+randomNumber;
        OrderModel orderModel = new OrderModel(UniqueOrderid,userId,cardfid,orderValueinString,clientName,clientAddress,orderStatus,couponCode,offerclamedValueinString,totalcommissioninString,paymentMode);
        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("Orders");
        String orderDbId = ordersRef.push().getKey();
        ordersRef.child(orderDbId).setValue(orderModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(CheckoutActivity.this, "Order Placed Successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CheckoutActivity.this, "Order Failed, Try again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void updateProductCartCount(String agentNIC,String cartId){
        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("cartItems").child(agentNIC).child(cartId);
        ordersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot cartSnapshot : dataSnapshot.getChildren()){
                        String Dbproductid = cartSnapshot.child("ProductDBId").getValue(String.class);
                        String qty = cartSnapshot.child("qty").getValue(String.class);
                        if(Dbproductid!=null){
                            DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("product").child(Dbproductid);
                            productRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot productsnapshot) {
                                    if(productsnapshot.exists()) {
                                        String cartcount = productsnapshot.child("cartcount").getValue(String.class);
                                        if(cartcount!=null){
                                                int cartCountInInt = Integer.parseInt(cartcount);
                                                int qtyInInt = Integer.parseInt(qty);
                                                int updateCartCount = cartCountInInt-qtyInInt;
                                                String updateCartCountInString = Integer.toString(updateCartCount);
                                                productRef.updateChildren(Collections.singletonMap("cartcount",updateCartCountInString));
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void updateAgentSales(String agentNIC, String sales){

        Map<String, Object> salesMap = new HashMap<>();
        float orderValue = Float.parseFloat(sales);

        DatabaseReference salesRef = FirebaseDatabase.getInstance().getReference("Users").child(agentNIC);
        salesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String totalAgentSales = snapshot.child("sales").getValue(String.class);
                    if(totalAgentSales !=null){
                        float totalAgentSalesInInt = Float.parseFloat(totalAgentSales);
                        totalAgentSalesInInt = totalAgentSalesInInt + orderValue;
                        String totalAgentSalesInString = Float.toString(totalAgentSalesInInt);
                        salesMap.put("sales", totalAgentSalesInString);
                        salesRef.updateChildren(salesMap);
                    }
                }else{
                    Toast.makeText(CheckoutActivity.this, "not found the dp", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        
    }

    public void changeCartId(){

        Random random = new Random();
        long lowerBound = 1000000000L;
        long upperBound = 9999999999L;
        long random10DigitNumber = lowerBound + (long) (random.nextDouble() * (upperBound - lowerBound));
        String randomNumber = Long.toString(random10DigitNumber);

        SharedPreferences sharedPreferences = getSharedPreferences("agentprefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("cartrefid", randomNumber);
        editor.apply();

    }

    public void sendEmail(String email, String orderId,String agentName, String customerName, String customerAddress, String orderValue, String TotoalCommision){
        try{

            String stringSenderEmail = "marketxcellinfo@gmail.com";
            String stringPasswordSenderEmail = "tbwouuprzljalvsl";

            String stringHost = "smtp.gmail.com";

            Properties properties = System.getProperties();

            properties.put("mail.smtp.host", stringHost);
            properties.put("mail.smtp.port", "465");
            properties.put("mail.smtp.ssl.enable", "true");
            properties.put("mail.smtp.auth", "true");

            javax.mail.Session session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(stringSenderEmail, stringPasswordSenderEmail);
                }
            });

            MimeMessage mimeMessage = new MimeMessage(session);
            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(email));

            mimeMessage.setSubject("Order Confirmation from MarketXcell - "+orderId);

            String emailContent = "Dear " + agentName + ",\n\n" +
                    "Thank you for your order!\n\n" +
                    "We are pleased to confirm that your order has been successfully placed.\n" +
                    "Please find the details below:\n\n" +
                    "Customer Name: " + customerName + "\n" +
                    "Customer Address: " + customerAddress + "\n" +
                    "Order Value: RS " + orderValue + "\n" +
                    "Your Total Commission: Rs " + TotoalCommision + "\n\n" +
                    "If you have any questions or concerns regarding your order, feel free to contact us.\n\n" +
                    "Thank you!";

            mimeMessage.setText(emailContent);

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Transport.send(mimeMessage);
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();

        }catch (Exception e){
            e.printStackTrace();
        }
    }


}