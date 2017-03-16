package com.example.coms7.weather;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import java.io.FileOutputStream;


/**
 * Created by Coms7 on 2016-12-02.
 */
//http://blog.naver.com/PostView.nhn?blogId=man8408&logNo=110104525158참고
public class initActivity extends Activity{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.init);
        try {//로컬 파일 생성, 있다면 덮어쓰지않음
            FileOutputStream fos = openFileOutput("myfile.txt", Context.MODE_APPEND);// 저장모드
        }catch (Exception e){
            e.printStackTrace();
        }
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                finish();
            }
        };
        handler.sendEmptyMessageDelayed(0,3000);//3초 대기
    }
}
