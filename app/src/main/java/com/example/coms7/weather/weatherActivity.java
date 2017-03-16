package com.example.coms7.weather;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by Coms7 on 2016-11-29.
 */

public class weatherActivity extends Activity {
    TextView textview1, textview2, textview3;
    Document doc = null;
    double lat = 0.0;//위도
    double lon= 0.0;//경도
    Map<String, Object> map;
    int notifyState=0;//init
    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.weather);
        //검색하기 클릭 시
        Button btnSearch =(Button)findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                EditText getAddress = (EditText)findViewById(R.id.inAddress);
                ////////////////////사용자가 입력한 주소를 이용해 google api로 위도 경도 정보 얻기
                getlocation(getAddress.getText().toString());
                map = getGridxy(lat,lon);//위도 경도를 기상청에서 사용하는 방식으로 변환(정수형)
                //기상청 API를 통해 해당 XML 주소의 날씨 정보 데이터를 얻어온다(위도, 경도에 따라)
                //http://www.kma.go.kr/weather/lifenindustry/sevice_rss.jsp에서 결과 확인*
                GetXML task = new GetXML();
                task.execute("http://www.kma.go.kr/wid/queryDFS.jsp?gridx=" + map.get("x") + "&gridy=" + map.get("y"));
            }
        });

        //사용자 주소 등록 주소로 날씨 조회 버튼 클릭시
        Button btnUserAddress = (Button) findViewById(R.id.btnUserAddress);
        btnUserAddress.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                StringBuffer data = new StringBuffer();
                FileInputStream fis = null;//파일명
                String str=null;
                map = getGridxy(lat,lon);
                try {
                    fis = openFileInput("myfile.txt");//사용자 주소가 등록된 파일
                    BufferedReader buffer = new BufferedReader(new InputStreamReader(fis));//buffer로 옮긴다음
                    str = buffer.readLine();//한줄 리드
                    getlocation(str);//검색하기 클릭했을 때와 같게 동작함
                    GetXML task = new GetXML();
                    task.execute("http://www.kma.go.kr/wid/queryDFS.jsp?gridx=" + map.get("x") + "&gridy=" + map.get("y"));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        //이전 화면으로 돌아가는 버튼
        Button btnReturn = (Button) findViewById(R.id.btnReturn);
        btnReturn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                finish();
            }
        });

        textview1 = (TextView) findViewById(R.id.textView1);//오늘
        textview2 = (TextView) findViewById(R.id.textView2);//내일
        textview3 = (TextView) findViewById(R.id.textView3);//모레
    }

    public void getlocation(String location) {
        final Geocoder geocoder = new Geocoder(this);
        List<Address> list = null;
        try {
            list = geocoder.getFromLocationName(location, 5);//geocoder로 구글맵의 이름으로 변경 && 위도, 경도 얻음
            if(list !=null){
                if(list.size()==0){ }
                else{
                    Address addr = list.get(0);
                    lat = addr.getLatitude();//list에 있는 값에서 위도 가져옴
                    lon = addr.getLongitude();
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    //http://blog.naver.com/PostView.nhn?blogId=tkddlf4209&logNo=220632424141&parentCategoryNo=78&categoryNo=&viewDate=&isShowPopularPosts=false&from=postView 참고
    public static Map<String, Object> getGridxy(double v1, double v2) {

       double RE = 6371.00877; // 지구 반경(km)
        double GRID = 5.0; // 격자 간격(km)
        double SLAT1 = 30.0; // 투영 위도1(degree)
        double SLAT2 = 60.0; // 투영 위도2(degree)
        double OLON = 126.0; // 기준점 경도(degree)
        double OLAT = 38.0; // 기준점 위도(degree)
        double XO = 43; // 기준점 X좌표(GRID)
        double YO = 136; // 기1준점 Y좌표(GRID)
        double DEGRAD = Math.PI / 180.0;

        double re = RE / GRID;
        double slat1 = SLAT1 * DEGRAD;
        double slat2 = SLAT2 * DEGRAD;
        double olon = OLON * DEGRAD;
        double olat = OLAT * DEGRAD;

        double sn = Math.tan(Math.PI * 0.25 + slat2 * 0.5) / Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sn = Math.log(Math.cos(slat1) / Math.cos(slat2)) / Math.log(sn);
        double sf = Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sf = Math.pow(sf, sn) * Math.cos(slat1) / sn;
        double ro = Math.tan(Math.PI * 0.25 + olat * 0.5);
        ro = re * sf / Math.pow(ro, sn);
        Map<String, Object> map = new HashMap<String, Object>();

        double ra = Math.tan(Math.PI * 0.25 + (v1) * DEGRAD * 0.5);
        ra = re * sf / Math.pow(ra, sn);
        double theta = v2 * DEGRAD - olon;
        if (theta > Math.PI)
            theta -= 2.0 * Math.PI;
        if (theta < -Math.PI)
            theta += 2.0 * Math.PI;
        theta *= sn;
        map.put("lat", v1);
        map.put("lng", v2);
        map.put("x", (int)Math.floor(ra * Math.sin(theta) + XO + 0.5));
        map.put("y", (int)Math.floor(ro - ra * Math.cos(theta) + YO + 0.5));
        return map;
    }

    private class GetXML extends AsyncTask<String, Void, Document> {
        @Override
        protected Document doInBackground(String... urls) {
            URL url;
            try {
                url = new URL(urls[0]);
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                doc = db.parse(new InputSource(url.openStream()));//xml문서 파싱
                doc.getDocumentElement().normalize();
            } catch (Exception e) {
                Toast.makeText(getBaseContext(), "Parsing Error", Toast.LENGTH_SHORT).show();
            }
            return doc;
        }

        @Override
        protected void onPostExecute(Document doc) {
            String s_cur = "", s_tom = "", s_far = "";
            NodeList nodeList = doc.getElementsByTagName("data");//parent <data>부터 XML 파싱
            s_cur += "오늘\n";
            s_tom += "내일\n";
            s_far += "모레\n";
            for (int i = 0; i < nodeList.getLength(); i++) {
                //날씨 데이터를 추출
                Node node = nodeList.item(i); //data엘리먼트 노드
                Element fstElmnt = (Element) node;
                NodeList tempList   = fstElmnt.getElementsByTagName("temp");  //XML 각 항목에 따라 파싱
                NodeList dayList    = fstElmnt.getElementsByTagName("day");
                NodeList timeList   = fstElmnt.getElementsByTagName("hour");
                NodeList weatherList = fstElmnt.getElementsByTagName("wfKor");

                //오늘, 내일, 모레에 따라 string을 다르게 받음
                if (dayList.item(0).getChildNodes().item(0).getNodeValue().matches("0")) {//<day>0</day>는 오늘
                    s_cur += timeList.item(0).getChildNodes().item(0).getNodeValue() + "시, ";
                    s_cur += "온도 = " + tempList.item(0).getChildNodes().item(0).getNodeValue() + " ,";
                    s_cur += "날씨 = " + weatherList.item(0).getChildNodes().item(0).getNodeValue() + "\n";
                } else if (dayList.item(0).getChildNodes().item(0).getNodeValue().matches("1")) {//내일
                    s_tom += timeList.item(0).getChildNodes().item(0).getNodeValue() + "시, ";
                    s_tom += "온도 = " + tempList.item(0).getChildNodes().item(0).getNodeValue() + " ,";
                    s_tom += "날씨 = " + weatherList.item(0).getChildNodes().item(0).getNodeValue() + "\n";
                } else {                                                                          //모레
                    s_far += timeList.item(0).getChildNodes().item(0).getNodeValue() + "시, ";
                    s_far += "온도 = " + tempList.item(0).getChildNodes().item(0).getNodeValue() + " ,";
                    s_far += "날씨 = " + weatherList.item(0).getChildNodes().item(0).getNodeValue() + "\n";
                }
                if( 0==notifyState && weatherList.item(0).getChildNodes().item(0).getNodeValue().matches("비")){
                    notifyState = 1;   //opt 1이면 비 알람---setResult를 통해 mainactivity 에게 전달
                }
            }//for end

            if(0==notifyState){
                notifyState = 2;   //opt2이면 비소식 없다는 알람
            }

            //최대 15개의 기상정보를 보내줌. 시간이 밤이면 이전의 데이터는 제공하지 않음
            //이전 데이터가 없으면 '오늘' 항목에 메시지 전달
            if (s_cur.length() == 3) s_cur += "서비스 제공 시간이 지났습니다.\n";

            textview1.setText(s_cur);//textView에 쓰기
            textview2.setText(s_tom);
            textview3.setText(s_far);
            super.onPostExecute(doc);
            setResult(notifyState); //MainActivity에 int값 전달
        }
    }//GetXML end
}
