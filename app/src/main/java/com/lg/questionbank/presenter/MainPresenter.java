package com.lg.questionbank.presenter;

import com.lg.questionbank.bean.Question;

import java.util.List;

/**
 * @author LuoYi on 2016/9/2
 */
public interface MainPresenter {
    void parseQuestionTxt(String name) throws Exception;

    void parseQuestionFromAssets(String name);

    void parseQuestion(String path);

    List<Question> searchQuestion(String keyWord);

    int[] searchCounts();
}
