package com.example.lh.testonline;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import DataType.Test;
import WebUtil.GsonUtils;
import WebUtil.WebConnection;
import WebUtil.ConfigUtil;
import okhttp3.FormBody;

import static staticSetting.Setting.projectName;
import static staticSetting.Setting.serverUrl;

public class OpeningTestActivity extends AppCompatActivity {
    private ListView listView;
    private List<Test> tlist;
    private String opentype;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opening_test);

        init();


    }
    private void init(){
        listView=(ListView)findViewById(R.id.listview);

        Intent intent=getIntent();
        opentype=intent.getStringExtra("type");
        //读取可用考试
        String url = serverUrl+"/"+projectName+"/OpeningTest";
        new OpeningTestActivity.myTask().execute(url,opentype);

    }
    public String getAvailableTest(String url,String type){
        //找出所有符合条件的考试
        String success= "";
        FormBody formBody = new FormBody.Builder()
                .add("uid",String.valueOf(new ConfigUtil(this).getUser().getUid()))
                .add("type",type)
                .build();
        success= WebConnection.doPost(url,formBody);
        return success;
    }
    class myTask extends AsyncTask<String,Integer,String> {
        @Override
        protected String doInBackground(String... params){
            //后台运行
            String url = params[0];
            String result=getAvailableTest(url,params[1]);
            return result;
        }

        protected void onPostExecute(String result){
            super.onPostExecute(result);
            //返回数据转为数组
            if(result.equals("no test")||result.equals("fail"))return;
            Gson gson= new Gson();
            tlist=gson.fromJson(result,new TypeToken<List<Test>>(){}.getType());

            //修改显示的部分
            List<String> tools=new ArrayList<>();
            if(tlist!=null){
                //若无可用考试则为空
                for(int i=0;i<tlist.size();i++){
                    //获得可用考试的名字,并显示在表上
                    if(opentype.equals("test"))tools.add(tlist.get(i).getName()+" 开始时间:"+tlist.get(i).getStarttime()+" 结束时间"+tlist.get(i).getEndtime());
                    if(opentype.equals("result"))tools.add(tlist.get(i).getName()+"   "+tlist.get(i).getTotalScore()+"分");
                }
            }else{tools.add("no test");}
            listView.setAdapter(new ArrayAdapter<String>(OpeningTestActivity.this,android.R.layout.simple_list_item_1,tools));
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                Intent intent;
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    if(tlist==null)return;//若无考试则无响应
                    if(tlist.get(position).getTotalScore()==-1)return;
                    intent = new Intent(OpeningTestActivity.this,TestAcitivity.class);
                    intent.putExtra("testID",tlist.get(position).getId());
                    intent.putExtra("type",opentype);
                    startActivity(intent);
                }
            });
        }
    }
}
