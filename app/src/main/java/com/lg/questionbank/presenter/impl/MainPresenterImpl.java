package com.lg.questionbank.presenter.impl;

import android.content.Context;
import android.content.res.AssetManager;

import com.lg.questionbank.bean.Question;
import com.lg.questionbank.dao.QuestionDao;
import com.lg.questionbank.presenter.MainPresenter;
import com.lg.questionbank.utils.MyCloseUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author LuoYi on 2016/9/2
 */
public class MainPresenterImpl implements MainPresenter {
    private QuestionDao mQuestionDao;
    private Context mContext;

    public MainPresenterImpl(Context context) {
        mContext = context;
    }

    @Override
    public void parseQuestionTxt(String name) throws Exception {
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        AssetManager am = mContext.getResources().getAssets();
        try {
            is = am.open(name);
            isr = new InputStreamReader(is);
            br = new BufferedReader(isr);
            String line;
            StringBuffer sb = new StringBuffer();
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            String text = sb.toString();
            String[] base = text.split("“保险基础知识”题库解析<\\d{1,2}>\\d{4}-\\d{2}-\\d{2}");
            System.out.println(base.length);
            for (int i = 1; i < base.length; i++) {
                System.out.println("“保险基础知识”题库解析<" + i + ">");
                String[] type = base[i].split("判断题");
                System.out.println(type.length);
                // 区分是否分多选
                String[] stype = type[0].split("多选题");
                System.out.println(stype.length);
                if (stype.length == 1) {
                    System.out.println("【【选择题】】");
                    splitQuestion(i + ".0", 0, type[0]);
                } else {
                    System.out.println("【【单选题】】");
                    splitQuestion(i + ".1", 1, stype[0]);
                    System.out.println("【【多选题】】");
                    splitQuestion(i + ".2", 2, stype[1]);
                }
                // 有判断题
                if (type.length > 1) {
                    System.out.println("【【判断题】】");
                    splitQuestion(i + ".3", 3, type[1]);
                }
            }
        } catch (IOException e) {
            throw e;
        } finally {
            MyCloseUtils.doClose(is, isr, br);
        }
    }

    @Override
    public void parseQuestionFromAssets(String name) {
    }

    @Override
    public void parseQuestion(String path) {
    }

    @Override
    public List<Question> searchQuestion(String keyWord) {
        mQuestionDao = new QuestionDao(mContext);
        return mQuestionDao.query(keyWord);
    }

    @Override
    public int[] searchCounts() {
        mQuestionDao = new QuestionDao(mContext);
        return mQuestionDao.queryCounts();
    }

    private void splitQuestion(String qlocation, int type, String text) {
        Question question = new Question();
        mQuestionDao = new QuestionDao(mContext);
        question.qlocation = qlocation;
        question.qtype = type;
        String[] squestion = text.split("【解析】|\\[解析]|【答案解析】");// 通过【解析】分割题
        System.out.println(squestion.length);
        String tqdetail = "";
        for (int i = 0; i <= squestion.length; i++) {

            if (i == squestion.length) {
                question.qdetail = tqdetail;
                mQuestionDao.add(question);
            }
            if (i < squestion.length) {
                Pattern pattern = Pattern.compile("(\\d[\\.|\\、][\\S\\s]*)");
                Matcher m = pattern.matcher(squestion[i]);
                String pp = "";
                if (m.find()) {
                    pp = m.group();
                }
                String plocation = "";
                if (i != 0) {
                    plocation = "【解析】";
                }

                tqdetail = plocation + m.replaceFirst("");
                if (i != 0) {
                    question.qdetail = tqdetail;
                    if (question.qtitle != null && !question.qtitle.trim().equals("")){
                        mQuestionDao.add(question);
                    }
                }
                question.qtitle = pp;
                i=i;
//            System.out.println(plocation + m.replaceFirst(""));
//            System.out.println(pp);
            }
        }
    }

}
