package com.example.langjian.demoapp;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Layout;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by langjian on 2016/4/7.
 */
public class EditTextDemoActivity extends Activity{

    private EditText mEditText;
    private String topicTitle;
    /**
     * 是否显示默认提示文字
     */
    private static boolean isHintVisible = true;

    private String oldText;
    private TextView topic2;
    private TextView topic3;
    private float leftMarginOfFirstLine;
    boolean overFlowSolution[] = new boolean[]{true,true};
    private CharSequence[] blankSpannables = new CharSequence[2];

    /**
     * 控制提示文字展示
     * @param editText 编辑框
     * @param hint 默认文字
     */
    private void setHint(EditText editText, String hint) {

        if(editText!=null && editText.getText().length() == topicTitle.length() && editText.getSelectionStart() == topicTitle.length()){
            //设置hint文字颜色
            SpannableString hintSpannable = new SpannableString(hint);
            hintSpannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.c_999999)),0,hintSpannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            editText.append(hintSpannable);
            editText.setSelection(topicTitle.length());
            //注意，在赋值之后，才给，isHintVisible赋值true
            isHintVisible = true;
        }else{
            if(isHintVisible){//上一次编辑显示默认提示文字之后，本次编辑操作，需要隐藏该提示文字
                //隐藏hint文字，请注意，isHintVisible赋值操作优先于赋值操作setText，否则，循环抛出stackOverflow。
                isHintVisible = false;
                //保存最新的文本
                editText.setText(editText.getText().subSequence(0,editText.getSelectionStart()));
                //光标置于末尾
                editText.setSelection(editText.getText().length());
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edittext);

        //-------------------------------------------
        //demo1
        //-------------------------------------------
        mEditText = (EditText)findViewById(R.id.editText);
        topicTitle = "#test#";
        mEditText.setText(topicTitle);
        mEditText.setSelection(topicTitle.length());

        //禁用系统复制粘贴选择文本功能
        mEditText.setLongClickable(false);
        mEditText.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);

        setHint(mEditText,"hint");

        //点击将光标定位到“话题标题”和“提示文字”区域光标处理逻辑
        mEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击之后光标位置
                int selectionStart = mEditText.getSelectionStart();
                //如果当前cursor的位置在“话题标题”中间，将光标放在“话题标题”之后。
                if(selectionStart< topicTitle.length()){
                    mEditText.setSelection(topicTitle.length());
                }
                //如果当前cursor的位置在“提示文字”标题中间，将光标放在“提示文字”前面，“话题标题”之后。
                if(isHintVisible && selectionStart>topicTitle.length()){
                    mEditText.setSelection(topicTitle.length());
                }
            }
        });

        //编辑“话题标题”区域处理逻辑
        mEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction() == KeyEvent.ACTION_DOWN){
                    if(mEditText.getSelectionStart() <= topicTitle.length()){
                        //编辑“话题标题”区域无效
                        if (keyCode == KeyEvent.KEYCODE_DEL) {
                            return true;
                        }
                    }
                }
                return false;
            }
        });

        //覆盖父类QZPublishActivity中addTextChangedListener方法
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //如果发现用户编辑禁止编辑区域,例如“话题标题”或者“hint”。保存修改前的文字
                if(mEditText.getSelectionStart()<topicTitle.length()
                        || (isHintVisible && mEditText.getSelectionStart()>topicTitle.length())){
                    oldText = s.subSequence(0,topicTitle.length()).toString();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                mTempInput = s;
            }

            @Override
            public void afterTextChanged(Editable s) {

                if(oldText!=null && !oldText.equals("")){//如果发现用户禁止编辑“话题标题”区域，回复上次保存的文字，光标移动到全文末尾。
                    String oldTempText = oldText;
                    oldText = "";
                    mEditText.setSelection(topicTitle.length());
                    s.append(oldTempText);
                    mEditText.setSelection(oldTempText.length());
                }
                setHint(mEditText,"hint");//文字更新之后，判断是否显示hint文字
            }
        });

        //-------------------------------------------
        //demo2 英文话题
        //-------------------------------------------
        topic2 = (TextView) findViewById(R.id.topic2);
        topic3 = (TextView) findViewById(R.id.topic3);
        SpannableString blankSpannable0 = new SpannableString(topic2.getText());
        blankSpannable0.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.c_ffffff)),0,blankSpannable0.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        SpannableString blankSpannable1 = new SpannableString(topic3.getText());
        blankSpannable1.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.c_ffffff)),0,blankSpannable1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        blankSpannables[0] = blankSpannable0;
        blankSpannables[1] = blankSpannable1;

        buildEditText(R.id.editText2,topic2);

        //-------------------------------------------
        //demo3 中文话题
        //-------------------------------------------

        buildEditText(R.id.editText3,topic3);

    }

    private EditText buildEditText(int dex, final TextView topic) {

        final EditText editText = (EditText) findViewById(dex);

        //根据话题文字的长度初始化空格的数量，本人采用的方法，还请高人指点。

        TextPaint myPaint = topic.getPaint();
        leftMarginOfFirstLine = Layout.getDesiredWidth(topic.getText(),0,topic.getText().toString().length(),myPaint);


        setLayoutParams(editText, (int) leftMarginOfFirstLine);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }


            @Override
            public void afterTextChanged(Editable s) {
                setPadding(editText,topic);
            }
        });

        return editText;
    }


    private void setPadding(EditText editText, TextView topic) {

        int index = 0;
        if(editText.getId() == R.id.editText2){
            index = 0;
        }else if(editText.getId() == R.id.editText3){
            index = 1;
        }

        if(editText.getLineCount()>1){
            if(overFlowSolution[index]){
                overFlowSolution[index]=false;
                editText.getText().insert(0, blankSpannables[index]);
                setLayoutParams(editText, 0);
                editText.invalidate();
            }
        }else{
            if(editText.getText().toString().contains(blankSpannables[index])){
                overFlowSolution[index]=true;
                editText.getText().delete(0,blankSpannables[index].length());
                //editText.setText(editText.getText().toString().substring(stringBuilder.toString().length()));
            }
            setLayoutParams(editText, (int) leftMarginOfFirstLine);
        }
    }

    private void setLayoutParams(EditText editText, int leftMargin) {
        RelativeLayout.LayoutParams frameLayout = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        editText.getLayoutParams();
        frameLayout.leftMargin = leftMargin;
        editText.setLayoutParams(frameLayout);
    }
}
