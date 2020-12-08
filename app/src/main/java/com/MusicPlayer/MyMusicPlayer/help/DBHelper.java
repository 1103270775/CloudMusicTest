package com.MusicPlayer.MyMusicPlayer.help;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {


    //创建数据库
    final String CREATE_TABLE_SQL =
            "create table stdb(_id integer primary " +
                    "key autoincrement , sname , spwd)";
    //重写构造方法
    public DBHelper(Context context, String name,
                    SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
        System.out.println(name+" created");
    }

    //创建用户表

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_SQL);
        System.out.println("stdb created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        System.out.println("Up!!!");
    }
}
