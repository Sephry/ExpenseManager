package com.ismailkurt.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;


import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class home_screen extends AppCompatActivity {
    private EditText mEmail;
    private EditText mPass;
    private CheckBox remember;

private ProgressDialog mDialog;

private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        mAuth=FirebaseAuth.getInstance();
        mDialog=new ProgressDialog(this);

        loginDetails();
    }

    private void loginDetails() {
        mEmail = findViewById(R.id.email_login);
        mPass = findViewById(R.id.password_login);
        Button btnLogin = findViewById(R.id.btn_login);
        TextView mSignUpHere = findViewById(R.id.signup_reg);
        remember=findViewById(R.id.checkBox2);

        SharedPreferences preferences=getSharedPreferences("checkbox",MODE_PRIVATE);
        String checkbox=preferences.getString("remember","");
        if(checkbox.equals("true"))
        {
            Intent intent=new Intent(home_screen.this,first_home_page.class);
            startActivity(intent);
        }
        else if(!checkbox.equals("false"))
        {
        }

        remember.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(buttonView.isChecked())
                {
                    SharedPreferences preferences=getSharedPreferences("checkbox",MODE_PRIVATE);
                    SharedPreferences.Editor editor=preferences.edit();
                    editor.putString("remember","true");
                    editor.apply();
                    Toast.makeText(home_screen.this,"Beni hatırla..",Toast.LENGTH_SHORT).show();
                }
                else if(!buttonView.isChecked())
                {
                    SharedPreferences preferences=getSharedPreferences("checkbox",MODE_PRIVATE);
                    SharedPreferences.Editor editor=preferences.edit();
                    editor.putString("remember","false");
                    editor.apply();
                    Toast.makeText(home_screen.this,"Beni hatırlama..",Toast.LENGTH_SHORT).show();
                }
            }
        });

        mSignUpHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(home_screen.this,Registration.class);
                startActivity(intent);
            }
        });



        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString().trim();
                String pass = mPass.getText().toString().trim();
                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Email zorunlu..",null);
                    return;
                }
                if (TextUtils.isEmpty(pass)) {
                    mPass.setError("Şifre zorunlu..",null);
                    return;
                }
                mDialog.setMessage("Giriş yapılıyor..");
                mDialog.show();

                mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            mDialog.dismiss();
                            checkEmailVerification();
                        }else{
                            mDialog.dismiss();
                            Toast.makeText(getApplicationContext(),"Giriş başarısız..",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }
    private void checkEmailVerification()
    {
        FirebaseUser firebaseUser=mAuth.getInstance().getCurrentUser();
        Boolean emailflag=firebaseUser.isEmailVerified();
        if(emailflag)
        {
            finish();
            Toast.makeText(getApplicationContext(),"Giriş Başarılı..",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(home_screen.this,first_home_page.class));
        }
        else
        {
            Toast.makeText(this,"Lütfen emaili doğrulayın..",Toast.LENGTH_LONG).show();
            mAuth.signOut();
        }
    }
}