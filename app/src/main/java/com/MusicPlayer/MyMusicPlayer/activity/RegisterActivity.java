package com.MusicPlayer.MyMusicPlayer.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.MusicPlayer.MyMusicPlayer.R;

public class RegisterActivity extends AppCompatActivity {

    private SharedPreferences user ;
    private  EditText account = null;
    private EditText password = null ;
    private EditText repwd = null;
    private String r_pwd=null;
    private String r_account=null;
    private String r_rpwd=null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        user = getSharedPreferences("user",MODE_PRIVATE);

        account = findViewById(R.id.account);
        r_account = account.getText().toString().trim();

        password = findViewById(R.id.password);
        r_pwd = password.getText().toString().trim();
        repwd = findViewById(R.id.repwd);
        r_rpwd = repwd.getText().toString().trim();

        Button button = findViewById(R.id.to_register);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!r_pwd.equals(r_rpwd)){
                    Toast.makeText(RegisterActivity.this,"两次输入的密码不一致",Toast.LENGTH_SHORT).show();
                }
                else if (r_account.equals(null)||r_pwd.equals(null))
                {
                    System.out.println(r_account+"  "+r_pwd);
                    Toast.makeText(RegisterActivity.this,"输入不得为空,请重新输入",Toast.LENGTH_SHORT).show();
                }

                else {
                    Toast.makeText(RegisterActivity.this,"注册成功,请登录!",Toast.LENGTH_SHORT).show();
                    user = getSharedPreferences("user",MODE_PRIVATE);
                    SharedPreferences.Editor editor = user.edit();
                    editor.putString("username",account.getText().toString());
                    editor.putString("pwd",password.getText().toString());
                    editor.apply();

                    Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                    startActivity(intent);
                }
            }
        });





    }
}
