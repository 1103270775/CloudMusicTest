package com.MusicPlayer.MyMusicPlayer.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.MusicPlayer.MyMusicPlayer.R;
import com.MusicPlayer.MyMusicPlayer.help.DBHelper;

public class LoginActivity extends AppCompatActivity {
    private DBHelper dbHelper;


    //实现插入数据功能
    private void insertData(SQLiteDatabase db, String count, String pwd) {
        ContentValues values = new ContentValues();
        values.put("sname", count);
        values.put("spwd", pwd);
        db.insert("stdb", null, values);
    }

    //数据库查询功能
    private void selectDate(SQLiteDatabase db, String count, String pwd) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);


        dbHelper = new DBHelper(LoginActivity.this, "stdb", null, 1);

        //获取账号输入框
        final EditText count_view = (EditText) findViewById(R.id.account);

        //获取密码输入框
        final EditText pwd_view = (EditText) findViewById(R.id.password);

        //获取注册按钮
        final Button btn1 = (Button) findViewById(R.id.register);

        //获取登录按钮
        final Button btn2 = (Button) findViewById(R.id.login);

        //注册功能
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = count_view.getText().toString();
                String pwd = pwd_view.getText().toString();

                if (name.equals("") || pwd.equals("")) {
                    Toast.makeText(LoginActivity.this, "填写的账号或者密码为空或", Toast.LENGTH_SHORT).show();
                } else {
                    insertData(dbHelper.getReadableDatabase(), name, pwd);
                    Toast.makeText(LoginActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                }
                //
            }
        });

        //登录功能
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                startActivity(intent);
                Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();

            }
        });


    }
}
