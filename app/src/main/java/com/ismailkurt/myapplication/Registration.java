package com.ismailkurt.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class Registration extends AppCompatActivity {
    private EditText mEmail;
    private EditText mPass;


    private ProgressDialog mDialog;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth=FirebaseAuth.getInstance();
        mDialog=new ProgressDialog(this);

        registration();
    }
    private void registration(){
        EditText mName = findViewById(R.id.name_reg);
        mEmail=findViewById(R.id.email_reg);
        mPass=findViewById(R.id.password_reg);
        Button btnReg = findViewById(R.id.btn_reg);
        TextView mSignin = findViewById(R.id.signin_here);

        mSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(Registration.this,home_screen.class);
                startActivity(intent);
            }
        });

        btnReg.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String email=mEmail.getText().toString().trim();
                String pass=mPass.getText().toString().trim();
                if(TextUtils.isEmpty(email)){
                    mEmail.setError("Email zorunlu..",null);
                    return;
                }
                if(TextUtils.isEmpty(pass)){
                    mPass.setError("Password zorunlu..",null);
                    return;
                }
                if(pass.length()<6)
                {
                    mPass.setError("En az 6 karakter içermeli..",null);
                    return;
                }

                mDialog.setMessage("Yükleniyor..");
                mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                mDialog.dismiss();
                                sendEmailVerification();
                                mEmail=findViewById(R.id.email_reg);
                                String email=mEmail.getText().toString().trim();
                                FirebaseUser mUser=mAuth.getCurrentUser();
                                if(mAuth!=null) {
                                    String uid = mUser.getUid();
                                    DatabaseReference myRootRef = FirebaseDatabase.getInstance().getReference().child("UserInfo").child(uid);
                                    DatabaseReference userNameRef =  myRootRef.child("Email");
                                    userNameRef.setValue(email);
                                }
                            }
                            else {
                                mDialog.dismiss();

                                Toast.makeText(getApplicationContext(),"Kayıt başarısız..",Toast.LENGTH_SHORT).show();

                            }
                        }

                });
            }
        });
    }
    private void sendEmailVerification()
    {
        FirebaseUser firebaseUser=mAuth.getCurrentUser();
        if(firebaseUser!=null)
        {
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(Registration.this,"Kayıt başarılı.Doğrulama başarılı..",Toast.LENGTH_LONG).show();
                        mAuth.signOut();
                        finish();
                        startActivity(new Intent(Registration.this,home_screen.class));
                    }
                    else
                    {
                        Toast.makeText(Registration.this,"Hata! Doğrulama maili gönderiliyor..",Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}