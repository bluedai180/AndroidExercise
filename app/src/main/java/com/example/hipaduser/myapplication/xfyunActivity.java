package com.example.hipaduser.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.iflytek.cloud.RecognizerResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by blue on 17-3-13.
 */

public class xfyunActivity extends Activity{

    private Button mButton;
    private EditText mResultText;
    private RecognizerDialog mDialog;
    private SpeechRecognizer mIat;
    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();
    private final static String TAG = "bluedai";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xfyun);
        mButton = (Button) findViewById(R.id.btn);
        mResultText = (EditText) findViewById(R.id.et_result);
        mDialog = new RecognizerDialog(getApplicationContext(), mInitListener);
        mIat = SpeechRecognizer.createRecognizer(xfyunActivity.this, mInitListener);
        mDialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        mDialog.setParameter(SpeechConstant.ACCENT, "mandarin");
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.setListener(mRecognizerDialogListener);
                mDialog.show();
            }
        });
        SpeechUtility.createUtility(this, SpeechConstant.APPID +"=58c6ad01");
    }
    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            Log.d(TAG, "SpeechRecognizer init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                Toast.makeText(getApplicationContext(), "初始化失败，错误码：" + code, Toast.LENGTH_SHORT).show();
            }
        }
    };

    private RecognizerDialogListener mRecognizerDialogListener = new RecognizerDialogListener() {
        @Override
        public void onResult(com.iflytek.cloud.RecognizerResult recognizerResult, boolean b) {
            printResult(recognizerResult);
        }

        @Override
        public void onError(SpeechError speechError) {
            Log.d(TAG, "onError: " + speechError.getPlainDescription(true));
        }
//        @Override
//        public void onResult(com.iflytek.cloud.RecognizerResult recognizerResult, boolean b) {
//            printResult(recognizerResult);
//        }
//
//        /**
//         * 识别回调错误.
//         */
//        public void onError(SpeechError error) {
////            showTip(error.getPlainDescription(true));
//            Log.d(TAG, "onError: " + error.getPlainDescription(true));
//        }

    };

    private void printResult(RecognizerResult results) {
        String text = JsonParser.parseIatResult(results.getResultString());

        String sn = null;
        // 读取json结果中的sn字段
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mIatResults.put(sn, text);

        StringBuffer resultBuffer = new StringBuffer();
        for (String key : mIatResults.keySet()) {
            resultBuffer.append(mIatResults.get(key));
        }

        mResultText.setText(resultBuffer.toString());
        mResultText.setSelection(mResultText.length());
    }
}
