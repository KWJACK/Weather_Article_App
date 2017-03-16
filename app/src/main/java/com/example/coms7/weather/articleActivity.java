package com.example.coms7.weather;

/**
 * Created by Coms7 on 2016-12-02.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import java.net.URL;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class articleActivity extends Activity {
    Document doc = null;//XML 문서 파싱용
    //상위 10개만 뽑음
    String[] s_title = new String[15];
    String[] s_article = new String[15];
    String[] s_link = new String[15];
    @Override

    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.article);

        TextView textInfo = (TextView)findViewById(R.id.txtinfo);
        String str ="클릭시 해당 페이지로 이동합니다";
        SpannableStringBuilder sstr = new SpannableStringBuilder(str);//글자 색 부분 변경
        sstr.setSpan(new ForegroundColorSpan(Color.parseColor("#00FF7F")), 0,2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        sstr.setSpan(new ForegroundColorSpan(Color.parseColor("#5F00FF")), 12,14, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textInfo.setText(sstr);         //적용

        //돌아가기 버튼
        Button btnReturn2 = (Button) findViewById(R.id.btnReturn1);
        btnReturn2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                finish();
            }
        });
        //SBS 뉴스 파싱
        GetXML task2 = new GetXML();
        task2.execute("http://api.sbs.co.kr/xml/news/rss.jsp?pmDiv=all");
    }

    private class GetXML extends AsyncTask<String, Void, Document> {
        @Override
        protected Document doInBackground(String... urls) {
            URL url;
            try {
                url = new URL(urls[0]);             //0번째 인자값을통해 주소 받기
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
            ArrayList<NewsArticle> tempArticle = new ArrayList<NewsArticle>();
            NodeList nodeList = doc.getElementsByTagName("item");//XML parent <item>의 데이터 읽어오기
            for (int i = 0; i < 15; i++) {//10개만
                Node node = nodeList.item(i); //data엘리먼트 노드
                Element fstElmnt = (Element) node;//<item>의 하위 항목을 갖고오기 위해 엘러먼트로 분할
                NodeList titleList = fstElmnt.getElementsByTagName("title");     //<title> 항목 갖고오기
                NodeList linkList = fstElmnt.getElementsByTagName("link");       //<link> 항목 갖고오기
                NodeList desList = fstElmnt.getElementsByTagName("description");//<description> 항목 갖고오기

                s_title[i] = titleList.item(0).getChildNodes().item(0).getNodeValue().toString();//상위부터 순서대로 갖고오기
                s_article[i] = desList.item(0).getChildNodes().item(0).getNodeValue().toString();
                s_link[i] = linkList.item(0).getChildNodes().item(0).getNodeValue().toString();

                NewsArticle temp = new NewsArticle(s_title[i], s_article[i]);//새로 만든 NewsArticle 클래스로 묶는 temp변수
                tempArticle.add(temp);//Arraylist에 추가
            }
            printArticle(tempArticle);//printArticle에 ArrayList전달
            super.onPostExecute(doc);
        }
    }

    public class ListAdapter extends ArrayAdapter<NewsArticle> {//ListView를 위한 Adapter
        public ArrayList<NewsArticle> items;
        public ListAdapter(Context context, int textViewResourceId, ArrayList<NewsArticle> items) {
            super(context, textViewResourceId, items);
            this.items = items;//item에 connection
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;//ListView 받기
            if (v == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.item_row, null);
            }
            NewsArticle Info = items.get(position);//각 index에 매칭하는 정보 얻기
            if (Info != null) {//item_row.xml 파일 textView 매칭
                TextView tv1 = (TextView) v.findViewById(R.id.item_v1);
                TextView tv2 = (TextView) v.findViewById(R.id.item_v2);
                tv1.setText(Info.getTitle());   //제목 설정
                tv2.setText(Info.getContent()); //내용 설정
            }
            return v;
        }
    }

    //어댑터와 연결하여 뉴스 기사 출력
    public void printArticle(ArrayList<NewsArticle> arg) {
        ListView listView = (ListView) findViewById(R.id.listview);
        ListAdapter _adapter = new ListAdapter(this, R.layout.item_row, arg);//데이터와 어댑터 연결
        listView.setAdapter(_adapter);      //리스트뷰와 연결
        listView.setOnItemClickListener(new ListViewItemClickListener());
    }

    //ListView 각 항목 클릭시 XML파싱하여 얻은 주소를 이용해 이동
    private class ListViewItemClickListener implements AdapterView.OnItemClickListener
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            String url =s_link[position];       //position(index)에 맞는 url 가져오기
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);               //해당 페이지 바로 실행
        }
    }

    class NewsArticle{//XML에서 파싱한 데이터를 저장하는 변수, ArrayList에 제네릭으로 사용
        String title;
        String content;
        public NewsArticle(String _title, String _content){
            title   =   _title;
            content =   _content;
        }
        public String getTitle(){
            return title;
        }
        public String getContent(){
            return content;
        }
    }
}
