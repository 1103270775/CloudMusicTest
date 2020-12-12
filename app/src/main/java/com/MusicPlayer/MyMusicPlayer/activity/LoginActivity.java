package com.MusicPlayer.MyMusicPlayer.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

;import com.MusicPlayer.MyMusicPlayer.R;

public class LoginActivity extends AppCompatActivity {


    private SharedPreferences user ;
    private EditText username =null;
    private EditText pwd = null;
    private String login_username = null;
    private String login_password = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //获取注册按钮
        Button button_register = findViewById(R.id.register);

        username = findViewById(R.id.login_account);
        pwd = findViewById(R.id.login_password);


        //进入注册界面
        button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_login = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent_login);
            }
        });

        //获取登录按钮
        Button button_login = findViewById(R.id.login);

        login_username = username.getText().toString();
        login_password = pwd.getText().toString();

        //验证登录问题
        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user  = getSharedPreferences("user",MODE_PRIVATE);
                System.out.println(user.getAll()+"=========================================");
                System.out.println(username.getText().toString()+" "+login_username);
                if (user.getString("username",null).equals(username.getText().toString())&&user.getString("pwd",null).equals(pwd.getText().toString())){
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(LoginActivity.this,"密码错误或用户不存在",Toast.LENGTH_SHORT).show();
                }

            }
        });



    }
}
