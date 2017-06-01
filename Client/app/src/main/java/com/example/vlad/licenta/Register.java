package com.example.vlad.licenta;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.fasterxml.jackson.databind.type.TypeFactory;

public class Register extends AppCompatActivity {
    private  AutoCompleteTextView nameView;
    private AutoCompleteTextView email_registerView;
    private TextView password_registerView;
    private TextView re_passwordView;
    private CheckBox termsView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    // email sa contina @
    // parola sa fie > 6 caractere
    // pass si repass sa corespunda
    // checkbox sa fie bifat

    public void registerMethod(View view) {

        nameView = (AutoCompleteTextView) findViewById(R.id.name_id);
        email_registerView = (AutoCompleteTextView) findViewById(R.id.email_register_id);
        password_registerView = (EditText) findViewById(R.id.password_register_id);
        re_passwordView = (EditText) findViewById(R.id.re_password_id);
        termsView = (CheckBox) findViewById(R.id.checkBox_register);

        String name = nameView.getText().toString();
        String email_r = email_registerView.getText().toString();
        String password_r = password_registerView.getText().toString();
        String re_password = re_passwordView.getText().toString();

        boolean allValid = true;
        if (email_r.isEmpty()) {
            email_registerView.setError(getString(R.string.error_field_required));
            allValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email_r).matches()) {
            email_registerView.setError(getString(R.string.error_invalid_email));
            allValid = false;
        }

        if (password_r.isEmpty()) {
            password_registerView.setError(getString(R.string.error_field_required));
            allValid = false;
        } else if (password_r.length() < 6) {
            password_registerView.setError(getString(R.string.password_short));
            allValid = false;
        }

        if (re_password.isEmpty()) {
            re_passwordView.setError(getString(R.string.error_field_required));
            allValid = false;
        } else if (!password_r.equals(re_password)) {
            re_passwordView.setError(getString(R.string.error_repassword));
            allValid = false;
        }

        if (!termsView.isChecked()){
            termsView.setError(getString(R.string.error_check_box));
            allValid = false;
        }

        if (!allValid) {
            password_registerView.setText("");
            re_passwordView.setText("");
            return;
        }

       /* String name = "name";
        String email_r = "email";
        String password_r = LogIn.md5("pass");*/




//        if (name.isEmpty())
//            nameView.setError(getString(R.string.error_field_required));
//            else if (email_r.isEmpty())
//                email_registerView.setError(getString(R.string.error_field_required));
//            else if (!email_r.contains("@"))
//                email_registerView.setError(getString(R.string.error_invalid_email));
//            else if (password_r.isEmpty())
//                password_registerView.setError(getString(R.string.error_field_required));
//            else if ( password_r.length()<6)
//                password_registerView.setError(getString(R.string.password_short));
//            else if (re_password.isEmpty())
//                re_passwordView.setError(getString(R.string.error_field_required));
//            else if (re_password.compareTo(password_r) != 0)
//                re_passwordView.setError(getString(R.string.error_repassword));
//            else if (termsView.isChecked() == false)
//                termsView.setError(getString(R.string.error_check_box));

//        else
//        {
            // VERIFICAM IN BAZA DE DATE DACA EXISTA EMAIL-UL INTRODUS
            // DACA NU, VOM PUNE IN BAZA DE DATE CONTUL NOU. ---- Eventual mesaj SUCCES


            String url = ServerProperties.HOST;
            url += "/user/register?fullname=";
            url += name;
            url += "&username=";
            url += email_r;
            url += "&password=";
            url += LogIn.md5(password_r);
            url += "&type=REGULAR";
            ServerRequestGET<String> theServerRequest = new ServerRequestGET<>(url, TypeFactory.defaultInstance().constructType(String.class),
                    new AsyncResponse<String>() {
                        @Override
                        public void actionCompleted(String res) {

                            if(res.equals("1")) {
                                MiscFunctions.createToast(getApplicationContext(), "Successfully registered!");
                                Intent intent = new Intent(getApplicationContext(), LogIn.class);
                                startActivity(intent);
                            }
                            else
                            {
                                MiscFunctions.createToast(getApplicationContext(), "Email already registered!");
                            }
                        }
                    });

            theServerRequest.execute();
        }

}
//    }



