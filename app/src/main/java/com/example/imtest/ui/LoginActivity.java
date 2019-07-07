package com.example.imtest.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.imtest.BaseApplication;
import com.example.imtest.R;
import com.example.imtest.entity.UserEntity;
import com.example.imtest.net.IHttpRequest;
import com.example.imtest.net.NetService;
import com.example.imtest.net.StandardResponse;
import com.example.imtest.net.RetrofitFactory;
import com.google.gson.Gson;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    Button btnRegisterOrLog;

    EditText edUsername;
    EditText edPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btnRegisterOrLog = findViewById(R.id.btn_login);
        edPassword = findViewById(R.id.edit_password);
        edUsername = findViewById(R.id.edit_username);

        btnRegisterOrLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Retrofit retrofit = RetrofitFactory.getInstance();
                IHttpRequest request = retrofit.create(IHttpRequest.class);


                Disposable d =
                        request.registerOrLog(edUsername.getText().toString(),edPassword.getText().toString())
                                .subscribeOn(Schedulers.io())
                                .subscribe(new Consumer<StandardResponse>() {
                                    @Override
                                    public void accept(final StandardResponse registerOrLogResponse) throws Exception {

                                        Log.d(TAG, "login state: "+new Gson().toJson(registerOrLogResponse));

                                        if (registerOrLogResponse.code == 0) {
                                            initApplicationWithUsername(edUsername.getText().toString(),registerOrLogResponse.port);
                                        } else {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(LoginActivity.this, registerOrLogResponse.message, Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }

                                    }
                                }, new Consumer<Throwable>() {
                                    @Override
                                    public void accept(Throwable throwable) throws Exception {
                                        Log.e(TAG, "accept: ",throwable);
                                    }
                                });


            }
        });
    }


    private void initApplicationWithUsername(String username,int port){
        BaseApplication.setLoginUser(username);
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        startService(new Intent(LoginActivity.this, NetService.class));
        BaseApplication.setPort(port);
    }

}
