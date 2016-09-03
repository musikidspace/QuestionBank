package com.lg.questionbank.dao.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 创建数据库及表;SQLite帮助类
 * 创建带路径的db，需传入重写得contecxt对象--MyContextWrapper
 * 默认databases文件夹下，传默认context对象
 *
 * @author LuoYi on 2016/8/17
 */
public class MySQLiteOpenHelper extends SQLiteOpenHelper {

    public MySQLiteOpenHelper(Context context) {
        super(context, "travelsong.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql = "create table question (qid integer primary key autoincrement, qlocation varchar(10), qtitle varchar(1000)," +
                " qdetail varchar(1000), qtype integer)";
        sqLiteDatabase.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
