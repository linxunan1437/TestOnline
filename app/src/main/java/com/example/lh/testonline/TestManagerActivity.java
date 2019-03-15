package com.example.lh.testonline;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import DataType.User;
import WebUtil.ConfigUtil;

public class TestManagerActivity extends AppCompatActivity {
    ListView listView;
    Boolean tutor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_manager);
        this.setTitle("考试管理");
        listView=(ListView)findViewById(R.id.listview);
        ConfigUtil configUtil=new ConfigUtil(TestManagerActivity.this);
        User u= configUtil.getUser();
        if(u.getTutor().equals("yes"))tutor=true;else tutor=false;
        String[] tools;
        if(tutor==true)
            tools=new String[]{"当前考试","考试结果","教师出卷"};
        else
            tools=new String[]{"当前考试","考试结果"};
        listView.setAdapter(new ArrayAdapter<String>(TestManagerActivity.this,android.R.layout.simple_list_item_1,tools));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            Intent intent;
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                switch (position)
                {
                    case 0:
                    {
                        intent = new Intent(TestManagerActivity.this,OpeningTestActivity.class);
                        intent.putExtra("type","test");
                        startActivity(intent);
                    }break;
                    case 1:
                    {
                        if(tutor==false){
                            intent = new Intent(TestManagerActivity.this,OpeningTestActivity.class);
                            intent.putExtra("type","result");
                            startActivity(intent);
                        }else{
                            intent = new Intent(TestManagerActivity.this,OpeningTestActivity.class);
                            intent.putExtra("type","teacherResult");
                            startActivity(intent);
                        }
                    }break;
//                    case 2:
//                    {
//                        intent = new Intent(TestManagerActivity.this,OpeningTestActivity.class);
//                        intent.putExtra("type","marking");
//                        startActivity(intent);
//                    }break;
                    case 2:
                    {
                        intent = new Intent(TestManagerActivity.this,GiveTestActivity.class);
                        startActivity(intent);
                    }break;
                }
            }

        });
    }
}
