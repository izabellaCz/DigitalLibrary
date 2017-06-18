package com.example.vlad.licenta;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.example.vlad.licenta.model.User;
import com.fasterxml.jackson.databind.type.TypeFactory;


public class LogIn extends AppCompatActivity {

    private AutoCompleteTextView emailView;
    private EditText passwordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        emailView = (AutoCompleteTextView) findViewById(R.id.email_id);
        passwordView = (EditText) findViewById(R.id.password_id);

        boolean value;
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            value = bundle.getBoolean("logout");
            if ( value )
                MiscFunctions.createToast(getApplicationContext(), "Logout successful!");
        }
    }

    public static final String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void logInMethod(View view){
        /*emailView.setText("admin");
        passwordView.setText("admin");*/

        emailView.setText("iza@iza.com");
        passwordView.setText("password");

        String email = emailView.getText().toString();
        String password = passwordView.getText().toString();

        boolean allValid = true;
        if (email.isEmpty()) {
            emailView.setError(getString(R.string.error_field_required));
            allValid = false;
        }
        if(password.isEmpty()){
            passwordView.setError(getString(R.string.error_field_required));
            allValid = false;
        }

        if (!allValid) {
            passwordView.setText("");
            return;
        }

            String url = ServerProperties.HOST;
            url += "/user/login?username=";
            url += emailView.getText();
            url += "&password=";
            url += md5(passwordView.getText().toString());
            ServerRequestGET<User> theServerRequest = new ServerRequestGET<>(url, TypeFactory.defaultInstance().constructType(User.class),
                    new AsyncResponse<User>() {
                        @Override
                        public void actionCompleted(User obj) {
                            if (obj == null) {
                                MiscFunctions.createToast(getApplicationContext(),"Login failed.");
                                passwordView.setText("");
                            }
                            else {
                                if ( obj.getType().equals("ADMINISTRATOR") ) {
                                    Intent intent = new Intent(getApplicationContext(), Administrator.class);
                                    intent.putExtra("currentUser", obj);
                                    startActivity(intent);
                                } else {
                                    Intent intent = new Intent(getApplicationContext(), Client.class);
                                    intent.putExtra("currentUser", obj);
                                    startActivity(intent);
                                }
                            }
                        }
                    });

            theServerRequest.execute();

    }

    public void registerMethod(View view){
        Intent intent1 = new Intent(this,Register.class);
        startActivity(intent1);
    }


}
