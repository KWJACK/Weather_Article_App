package com.example.coms7.weather;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import android.widget.TextView;
import java.io.FileOutputStream;
import java.io.IOException;

import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.Date;

import jxl.write.WriteException;

public class MainActivity extends Activity{
    View dialogView;
    EditText dlgEdtAddress;
    ArrayAdapter<String> arrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startActivity(new Intent(this, initActivity.class));//로딩 인텐트 불러오기

        //버튼 클릭시 날씨 인텐트 이동
        Button btnWeatherActivity =(Button)findViewById(R.id.btnWeatherAct);
        btnWeatherActivity.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent intent = new Intent(getApplicationContext(), weatherActivity.class);
                startActivityForResult(intent, 0);//0 is requestCode
            }
        });

        //SBS 8뉴스 버튼 클릭시 웹페이지로 바로 이동
        Button btnNewsActivity =(Button)findViewById(R.id.btnNews);
        btnNewsActivity.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                String url ="http://news.sbs.co.kr/news/programMain.do?prog_cd=R1&plink=SNB&cooper=SBSNEWS#playerPopup";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);//해당 인텐트로 바로 이동
            }
        });

        //SBS 뉴스 기사 버튼 클릭시 XML 파싱 결과 보여주는 인텐트로 이동
        Button btnArticleActivity =(Button)findViewById(R.id.btnArticle);
        btnArticleActivity.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent intent = new Intent(MainActivity.this, articleActivity.class);
                startActivity(intent);//따로 request받지않음음
            }
       });

        //현재 날짜 출력
        TextView dateDisplay = (TextView)findViewById(R.id.date);
        String currentDate = DateFormat.getDateInstance().format(new Date());
        dateDisplay.setText(currentDate);

        //알림 테스트용 버튼
        Button btntestNotify =(Button)findViewById(R.id.btnNotify);
        btntestNotify.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                WeatherNofify(1);//강수예정임을 출력
            }
        });

        //사용자 주소 등록 버튼
        Button btnAddadderss=(Button)findViewById(R.id.btnAddadress);
        btnAddadderss.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                dialogView = (View)View.inflate(MainActivity.this, R.layout.dialogaddress, null);
                AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
                dlg.setTitle("사용자 주소 등록");
                dlg.setView(dialogView);
                dlg.setPositiveButton("확인", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which){
                        try{
                            dlgEdtAddress = (EditText)dialogView.findViewById(R.id.dlgEdt1);
                            writeUserAddress(dlgEdtAddress.getText().toString());
                        }catch (Exception e){

                        }
                    }
                });
                dlg.setNegativeButton("취소", null);
                dlg.show();
            }
        });

    }

    //Local DB용 텍스트파일 만들고 관리하기
    public void writeUserAddress(String userAddress) throws IOException,WriteException {
        try {
            // 파일에서 읽은 데이터를 저장하기 위해서 만든 변수
            StringBuffer data = new StringBuffer();
            FileOutputStream fos = openFileOutput("myfile.txt", Context.MODE_APPEND);// 저장모드
            PrintWriter out = new PrintWriter(fos);
            out.println(userAddress);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        //subActivity의 리턴값을 받는 함수
        switch (requestCode)//리퀘스트 값에 따라
        {
            case 0:{
                WeatherNofify(resultCode);//resultCode으로 받은 opt에 따라 실행
                break;
            }
        }
    }

    public void WeatherNofify(int opt) {
        if(opt==0)return;//0이면 종료
        //알림을 관리하는 NotificationManager 얻어오기
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(this, MainActivity.class);//액티비티에서 알림보내기
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //알림을 만들어내는 Builder객체 생성
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), android.R.drawable.star_on));
        builder.setSmallIcon(android.R.drawable.star_on);//상태표시줄에 보이는 아이콘 모양
        if(opt==1) {//opt 1이면 강수 메세지
            builder.setContentTitle("강수 예정");    //알림창에서의 제목
            builder.setTicker("날씨 조심하세요"); //알림이 발생될 때 잠시 보이는 글씨
            builder.setContentText("가방에 우산 두고 다니는게 어떨까요?");   //알림창에서의 글씨
        }else{//opt 2이면 화창함 메시지
            builder.setContentTitle("화창한 날씨");    //알림창에서의 제목
            builder.setTicker("날씨 조심하세요"); //알림이 발생될 때 잠시 보이는 글씨
            builder.setContentText("실외에서 활동하기 좋습니다");   //알림창에서의 글씨
        }
        builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);//사운드 + 진동
        builder.setAutoCancel(true);//터치시 자동 삭제
        builder.setContentIntent(pendingIntent);
        manager.notify(0, builder.build());//매니저를 통해 알림 출력
    }
}




