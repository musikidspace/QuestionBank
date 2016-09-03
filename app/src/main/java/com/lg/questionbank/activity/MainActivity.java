package com.lg.questionbank.activity;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lg.questionbank.R;
import com.lg.questionbank.bean.Question;
import com.lg.questionbank.presenter.MainPresenter;
import com.lg.questionbank.presenter.impl.MainPresenterImpl;
import com.lg.questionbank.utils.MyDisplayUtils;
import com.lg.questionbank.utils.MySPUtils;
import com.lg.questionbank.widget.CustomDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LuoYi on 2016/9/2
 */
public class MainActivity extends BaseActivity implements View.OnClickListener, View.OnTouchListener {
    private Context mContext;
    private EditText et_search;
    private ImageView iv_search;
    private TextView tv_counts;
    private ViewPager vp_tv;
    private TextView tv_question;
    private LinearLayout ll_points;
    private ArrayList<View> mQuestionView;
    private boolean isCloseShow;
    private int lastPosition = 0;
    private long lasttime = 0;
    private MainPresenter mMainPresenter;
    private CustomDialog mCustomDialog;
    private static final int QUESTION_ADD_SUCCESS = 100;
    private static final int QUESTION_ADD_FAILURE = 101;
    private static final int QUESTION_SEARCH_SUCCESS = 102;
    private List<Question> questions;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mCustomDialog.dismiss();
            switch (msg.what) {
                case QUESTION_ADD_SUCCESS:
                    Toast.makeText(mContext, "加载成功", Toast.LENGTH_SHORT).show();
                    MySPUtils.putBoolean(mContext, "inSQL", true);
                    int[] counts = getCounts();
                    if (counts[0] == 0) {
                        tv_counts.setVisibility(View.GONE);
                    } else {
                        tv_counts.setVisibility(View.VISIBLE);
                        tv_counts.setText("共" + counts[0] + "题," + counts[1] + "道选择题," + counts[4] + "道判断题");
                    }
                    break;
                case QUESTION_ADD_FAILURE:
                    Toast.makeText(mContext, "加载失败" + msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;
                case QUESTION_SEARCH_SUCCESS:
                    if (questions != null) {
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                MyDisplayUtils.dp2px(mContext, 8), MyDisplayUtils.dp2px(mContext, 8));
                        int margin = MyDisplayUtils.dp2px(mContext, 2);
                        params.setMargins(margin, 0, margin, 0);
                        View point;
                        mQuestionView = new ArrayList<>();
                        int tsize = questions.size();
                        for (int i = 0; i < questions.size(); i++) {
                            if (questions.get(i).qtitle.trim().equals("")) {
                                tsize--;
                            } else {
                                View view = View.inflate(mContext, R.layout.viewpager_item, null);
                                TextView tv = (TextView) view.findViewById(R.id.tv_question);
                                tv.setTextSize(16);
                                String stext = et_search.getText().toString().trim();
                                String text = questions.get(i).qlocation + "." + questions.get(i).qtitle + "\n\n" + questions.get(i).qdetail;
                                SpannableString spannableString = new SpannableString(text);
                                BackgroundColorSpan bgColorSpan = new BackgroundColorSpan(Color.YELLOW);
                                spannableString.setSpan(bgColorSpan, text.indexOf(stext), text.indexOf(stext) + stext.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                tv.setText(spannableString);
                                mQuestionView.add(view);
                                //添加指示器
                                point = new View(mContext);
                                point.setBackgroundResource(R.drawable.selector_point);
                                if (i != 0) {
                                    point.setEnabled(false);
                                }
                                ll_points.addView(point, params);
                            }
                        }
                        Toast.makeText(mContext, "搜索到" + tsize + "道试题", Toast.LENGTH_SHORT).show();
                        vp_tv.setAdapter(new QuestionAdapter());

                        //滑动时，变动小白点状态
                        vp_tv.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                            @Override
                            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                            }

                            @Override
                            public void onPageSelected(int position) {
                                ll_points.getChildAt(lastPosition).setEnabled(false);
                                ll_points.getChildAt(position).setEnabled(true);
                                lastPosition = position;
                            }

                            @Override
                            public void onPageScrollStateChanged(int state) {
                            }
                        });
                    } else {
                        Toast.makeText(mContext, "未搜索到试题", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    private void initView() {
        mContext = this;
        tv_counts = (TextView) findViewById(R.id.tv_counts);
        vp_tv = (ViewPager) findViewById(R.id.vp_tv);
        tv_question = (TextView) findViewById(R.id.tv_question);
        ll_points = (LinearLayout) findViewById(R.id.ll_points);
        iv_search = (ImageView) findViewById(R.id.iv_search);
        iv_search.setOnClickListener(this);
        mMainPresenter = new MainPresenterImpl(mContext);
        mCustomDialog = new CustomDialog(mContext);
        mCustomDialog.setCanceledOnTouchOutside(false);

        //设置EditText的Drawable
        et_search = (EditText) findViewById(R.id.et_search);
        lasttime = System.currentTimeMillis();
        et_search.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_ENTER) {
                    long time = System.currentTimeMillis();
                    // 先隐藏键盘
                    ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(MainActivity.this.getCurrentFocus()
                                    .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    if (time - lasttime > 1000) {
                        //进行搜索操作的方法，在该方法中可以加入非空判断
                        if (et_search.getText().toString().length() == 0) {
                            Toast.makeText(mContext, "关键字为空", Toast.LENGTH_SHORT).show();
                        } else {
                            ll_points.removeAllViews();
                            lastPosition = 0;
                            search();
                        }
                        lasttime = time;
                    }

                }
                return false;
            }
        });
        final Drawable drawableRight = getResources().getDrawable(R.drawable.close);
        drawableRight.setBounds(0, 0, MyDisplayUtils.dp2px(mContext, 20), MyDisplayUtils.dp2px(mContext, 20));
        //设置触摸监听（清空文字）
        et_search.setOnTouchListener(this);
        //设置文字改变监听（没有文字不显示）
        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0 && !isCloseShow) {
                    et_search.setCompoundDrawables(null, null, drawableRight, null);
                    isCloseShow = true;
                } else if (charSequence.length() == 0 && isCloseShow) {
                    et_search.setCompoundDrawables(null, null, null, null);
                    isCloseShow = false;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        //设置焦点改变监听（没有焦点不显示）
        et_search.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus && ((EditText) view).getText().length() > 0) {
                    et_search.setCompoundDrawables(null, null, drawableRight, null);
                    isCloseShow = true;
                } else if (!hasFocus) {
                    et_search.setCompoundDrawables(null, null, null, null);
                    isCloseShow = false;
                }
            }
        });
    }

    private void initData() {
        if (!MySPUtils.getBoolean(mContext, "inSQL")) {
            mCustomDialog.setText("题库初始化，请稍候…");
            mCustomDialog.show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Message message = mHandler.obtainMessage();
                    try {
                        mMainPresenter.parseQuestionTxt("insurance.txt");
                        message.what = QUESTION_ADD_SUCCESS;
                    } catch (Exception e) {
                        e.printStackTrace();
                        message.what = QUESTION_ADD_FAILURE;
                        message.obj = e.getMessage();
                    }
                    mHandler.sendMessage(message);
                }
            }).start();
        } else {
            int[] counts = getCounts();
            if (counts[0] == 0) {
                tv_counts.setVisibility(View.GONE);
            } else {
                tv_counts.setVisibility(View.VISIBLE);
                tv_counts.setText("共" + counts[0] + "题," + counts[1] + "道选择题," + counts[4] + "道判断题");
            }
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (view == et_search) {//设置清除事件
            // et_account.getCompoundDrawables()得到一个长度为4的数组，分别表示左右上下四张图片
            Drawable drawable = et_search.getCompoundDrawables()[2];
            //如果右边没有图片，不再处理
            if (drawable == null)
                return false;
            //如果不是按下事件，不再处理
            if (motionEvent.getAction() != MotionEvent.ACTION_UP)
                return false;
            //找到位置
            if (motionEvent.getX() > et_search.getWidth()
                    - et_search.getPaddingRight()
                    - drawable.getIntrinsicWidth()) {
                et_search.setText("");
            }
            return false;
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_search:
                if (et_search.getText().toString().length() == 0) {
                    Toast.makeText(mContext, "关键字为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                ll_points.removeAllViews();
                lastPosition = 0;
                search();
                break;
        }
    }

    private void search() {
        mCustomDialog.setText("搜索中，请稍候…");
        mCustomDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = mHandler.obtainMessage();
                questions = mMainPresenter.searchQuestion(et_search.getText().toString().trim());
                message.what = QUESTION_SEARCH_SUCCESS;
                mHandler.sendMessage(message);
            }
        }).start();
    }

    private int[] getCounts() {
        return mMainPresenter.searchCounts();
    }

    private class QuestionAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mQuestionView.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            View view = mQuestionView.get(position);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

}
