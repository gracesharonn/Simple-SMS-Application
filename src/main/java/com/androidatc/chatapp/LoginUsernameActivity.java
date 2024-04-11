package com.androidatc.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.androidatc.chatapp.R.layout;
import com.androidatc.chatapp.model.UserModel;
import com.androidatc.chatapp.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

import kotlin.Metadata;
import org.jetbrains.annotations.Nullable;

public class LoginUsernameActivity extends AppCompatActivity {

    EditText usernameInput;
    Button finishBtn;
    ProgressBar progressBar;
    String phoneNumber;
    UserModel userModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(layout.activity_login_username);

        usernameInput = findViewById(R.id.loginUsername);
        finishBtn = findViewById(R.id.loginFinish);
        progressBar = findViewById(R.id.loginProgressBar);

        phoneNumber = getIntent().getExtras().getString("phone");
        getUsername();

        finishBtn.setOnClickListener((v -> {
            setUsername();
        }));

    }

    void setUsername(){
        String username = usernameInput.getText().toString();
        if(username.isEmpty()) {
            usernameInput.setError("Please input a username!");
            return;
        }
        if(username.length() <= 3) {
            usernameInput.setError("Username length should be at least 3 characters");
            return;
        }
        setInProgress(true);


        if(userModel!=null){
            userModel.setUsername(username);
        }else{
            userModel = new UserModel(phoneNumber, username, Timestamp.now(), FirebaseUtil.currentUserId());
        }

        FirebaseUtil.currentUserDetails().set(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                setInProgress(false);
                if(task.isSuccessful()){
                    Intent intent = new Intent(LoginUsernameActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        });
    }

    void getUsername() {
        setInProgress(true);
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                setInProgress(false);
                if(task.isSuccessful()){
                    userModel = task.getResult().toObject(UserModel.class);
                    if(userModel!=null){
                        usernameInput.setText(userModel.getUsername());
                    }
                }
            }
        });
    }

    void setInProgress(boolean inProgress) {
        if (inProgress) {
            progressBar.setVisibility(View.VISIBLE);
            finishBtn.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            finishBtn.setVisibility(View.VISIBLE);
        }
    }
}