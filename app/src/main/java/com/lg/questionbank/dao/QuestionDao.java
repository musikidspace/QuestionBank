package com.lg.questionbank.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lg.questionbank.bean.Question;
import com.lg.questionbank.dao.helper.MySQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * SQLite数据库操作
 *
 * @author LuoYi on 2016/9/3
 */
public class QuestionDao {
    private MySQLiteOpenHelper helper;

    public QuestionDao(Context context) {
        helper = new MySQLiteOpenHelper(context);
    }

    /**
     * @param question 对象
     * @return 是否增加成功
     */
    public boolean add(Question question) {
        long result;
        //调用getReadableDatabase方法,来初始化数据库的创建或读取
        SQLiteDatabase db = helper.getReadableDatabase();
        ContentValues values = new MyContentValues().getContentValues(question);

        //返回值：代表添加这个新行的Id ，-1代表添加失败
        result = db.insert("question", null, values);
        db.close();
        return result != -1;
    }

    /**
     * 查询总条数\选择题数\单选题数\多选题数\判断题数
     *
     * @return
     */
    public int[] queryCounts() {
        int[] counts = new int[5];
        //调用getReadableDatabase方法,来初始化数据库的创建或读取
        SQLiteDatabase db = helper.getReadableDatabase();
        String sql = "select count(1) from question";
        String sql1 = "select count(1) from question where qtype in (0, 1, 2)";
        String sql2 = "select count(1) from question where qtype = 1";
        String sql3 = "select count(1) from question where qtype = 2";
        String sql4 = "select count(1) from question where qtype = 3";
        Cursor cursor = db.rawQuery(sql, null);
        //解析Cursor中的数据
        if (cursor != null && cursor.getCount() > 0) {//判断cursor中是否存在数据
            //游标移到第一条记录准备获取数据
            cursor.moveToFirst();
            // 获取数据中的LONG类型数据
            int count = (int) cursor.getLong(0);
            counts[0] = count;
        }
        cursor = db.rawQuery(sql1, null);
        //解析Cursor中的数据
        if (cursor != null && cursor.getCount() > 0) {//判断cursor中是否存在数据
            //游标移到第一条记录准备获取数据
            cursor.moveToFirst();
            // 获取数据中的LONG类型数据
            int count = (int) cursor.getLong(0);
            counts[1] = count;
        }
        cursor = db.rawQuery(sql2, null);
        //解析Cursor中的数据
        if (cursor != null && cursor.getCount() > 0) {//判断cursor中是否存在数据
            //游标移到第一条记录准备获取数据
            cursor.moveToFirst();
            // 获取数据中的LONG类型数据
            int count = (int) cursor.getLong(0);
            counts[2] = count;
        }
        cursor = db.rawQuery(sql3, null);
        //解析Cursor中的数据
        if (cursor != null && cursor.getCount() > 0) {//判断cursor中是否存在数据
            //游标移到第一条记录准备获取数据
            cursor.moveToFirst();
            // 获取数据中的LONG类型数据
            int count = (int) cursor.getLong(0);
            counts[3] = count;
        }
        cursor = db.rawQuery(sql4, null);
        //解析Cursor中的数据
        if (cursor != null && cursor.getCount() > 0) {//判断cursor中是否存在数据
            //游标移到第一条记录准备获取数据
            cursor.moveToFirst();
            // 获取数据中的LONG类型数据
            int count = (int) cursor.getLong(0);
            counts[4] = count;
        }
        if (cursor != null) {
            cursor.close();//关闭结果集
        }
        //关闭数据库对象
        db.close();
        return counts;
    }

    /**
     * 按关键字匹配查询记录
     *
     * @param keyWord 关键字
     * @return 查询到的list
     */
    public List<Question> query(String keyWord) {
        List<Question> list = null;
        Question question;
        //调用getReadableDatabase方法,来初始化数据库的创建或读取
        SQLiteDatabase db = helper.getReadableDatabase();
        String sql = "select * from question where qtitle like '%" + keyWord + "%' or qdetail like '%" + keyWord + "%'";
        Cursor cursor = db.rawQuery(sql, null);
        //解析Cursor中的数据
        if (cursor != null && cursor.getCount() > 0) {//判断cursor中是否存在数据
            list = new ArrayList<>();
            //循环遍历结果集，获取每一行的内容
            while (cursor.moveToNext()) {//条件，游标能否定位到下一行
                //获取数据
                question = new Question();
                setAll(question, cursor);
                list.add(question);
            }
            cursor.close();//关闭结果集
        }
        //关闭数据库对象
        db.close();
        return list;
    }

    /**
     * 按SQL语句查询记录
     *
     * @param sql 查询语句
     * @return 查询到的list
     */
    public List<Question> querySQL(String sql) {
        List<Question> list = null;
        Question question;
        //调用getReadableDatabase方法,来初始化数据库的创建或读取
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        //解析Cursor中的数据
        if (cursor != null && cursor.getCount() > 0) {//判断cursor中是否存在数据
            list = new ArrayList<>();
            //循环遍历结果集，获取每一行的内容
            while (cursor.moveToNext()) {//条件，游标能否定位到下一行
                //获取数据
                question = new Question();
                setAll(question, cursor);
                list.add(question);
            }
            cursor.close();//关闭结果集
        }
        //关闭数据库对象
        db.close();
        return list;
    }

    //设置属性
    private void setAll(Question question, Cursor cursor) {
        question.qlocation = cursor.getString(cursor.getColumnIndex("qlocation"));
        question.qtitle = cursor.getString(cursor.getColumnIndex("qtitle"));
        question.qdetail = cursor.getString(cursor.getColumnIndex("qdetail"));
        question.qtype = cursor.getInt(cursor.getColumnIndex("qtype"));
    }

}
