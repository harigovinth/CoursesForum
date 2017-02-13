package com.apps.hari.coursesforum;

/**
 * Created by Hari on 04/02/17.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;



public class SignUp extends AppCompatActivity {

    private EditText mUserName;
    private EditText mEmailView;
    private EditText mPasswordView;
    private EditText mDeptView;
    private Button mSignUpButton;
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference mref;
    private Context context;
    private Button mSignInBotton;
    private ImageButton mImageButton;
    private ProgressDialog progressDialog;
    private Switch mSwitch;
    private boolean isSwitchOn = false;
    FirebaseUser firebaseUser;
    private static final String TAG = "SignUp";
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        mref = firebaseDatabase.getReference();


        progressDialog = new ProgressDialog(this);
        context = SignUp.this;
        mUserName = (EditText) findViewById(R.id.username);
        mEmailView = (EditText) findViewById(R.id.email);
        mDeptView=(EditText) findViewById(R.id.dept);
        mPasswordView = (EditText) findViewById(R.id.password);
        mSignUpButton = (Button) findViewById(R.id.email_sign_in_button);
        mSignInBotton = (Button) findViewById(R.id.sign_in_button);
        mSwitch = (Switch) findViewById(R.id.switch1);
        mImageButton=(ImageButton) findViewById(R.id.imageButton);


        mImageButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                Intent intent =new Intent(SignUp.this,Profileimage.class);
                return false;
            }
        });




        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                registerUser();


            }
        });
        mSignInBotton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUp.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    isSwitchOn = true;
                }
                else
                    isSwitchOn = false;
            }
        });

        mAuthListener= new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null ) {
                    Log.e(TAG, firebaseUser.isEmailVerified() ? "User is signed in and email is verified" : "Email is not verified");
                } else {
                    Log.e(TAG, "onAuthStateChanged:signed_out");
                }

            }
        };
    }

    public void registerUser(){

        final String userName = mUserName.getText().toString().trim();
        final String email = mEmailView.getText().toString().trim();
        String password = mPasswordView.getText().toString().trim();
        final String department = mDeptView.getText().toString().trim();

        if (TextUtils.isEmpty(email)){
            mEmailView.setError("Email cannot be empty");
            return;


        }
        if (TextUtils.isEmpty(password)){
            mPasswordView.setError("Password cannot be empty");
            return;

        }

        if (TextUtils.isEmpty(department)){
            mDeptView.setError("Department field cannot be empty");
            return;

        }


        progressDialog.setMessage("Registering User....");
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //checking if success
                        if (task.isSuccessful()){
                            sendVerificationEmail();
                            UserProfileChangeRequest.Builder builder = new UserProfileChangeRequest.Builder();
                            builder.setDisplayName(userName);
                            firebaseUser = mAuth.getCurrentUser();
                            saveUserInformation(userName,email,department,isSwitchOn);
                            progressDialog.dismiss();
                            Toast.makeText(context,"Successfully Registered", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(SignUp.this, LoginActivity.class);
                            startActivity(intent);


                        }

                        if(!task.isSuccessful()){
                            //display some message here
                            progressDialog.dismiss();
                            Toast.makeText(context,"Registration Error", Toast.LENGTH_LONG).show();
                        }

                    }
                });


    }

    private void sendVerificationEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(SignUp.this, "Signup successful Verification email sent", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }


    }

    public void saveUserInformation(String userName,String email,String department,boolean isSwitchOn){

        String userId = firebaseUser.getUid();

        User user = new User(userId,userName,email,department,isSwitchOn);
        mref.child("users").child(userId).setValue(user);


    }
}
