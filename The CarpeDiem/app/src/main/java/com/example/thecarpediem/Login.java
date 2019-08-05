package com.example.thecarpediem;

import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Login extends AppCompatActivity {
    TextInputEditText name, email, age, area;
    Button register;
    String nameet,ageet,emailet,areaet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        name=findViewById(R.id.textInputEditTextName);
        age=findViewById(R.id.textInputEditTextAge);
        email=findViewById(R.id.textInputEditTextEmail);
        area=findViewById(R.id.textInputEditTextArea);

        register=findViewById(R.id.appCompatButtonRegister);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                nameet=name.getText().toString().trim();
                ageet=age.getText().toString().trim();
                emailet=email.getText().toString().trim();
                areaet=area.getText().toString().trim();

                if(nameet.equals(""))
                {
                    Toast.makeText(Login.this,"Enter name!",Toast.LENGTH_SHORT).show();
                }









            }
        });


    }
}