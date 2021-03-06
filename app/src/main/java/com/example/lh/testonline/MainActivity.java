package com.example.lh.testonline;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;

import DataType.User;
import WebUtil.ConfigUtil;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("主菜单");

        listView=(ListView)findViewById(R.id.listview);
        String[] tools=new String[]{"个人账号管理","考试管理","课程/班级管理","班级群聊"};
        listView.setAdapter(new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1,tools));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            Intent intent;

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    switch (position) {
                        case 0: {
                            intent = new Intent(MainActivity.this, AccountManageActivity.class);
                            startActivity(intent);
                        }
                        break;
                        case 1: {
                            intent = new Intent(MainActivity.this, TestManagerActivity.class);
                            startActivity(intent);
                        }
                        break;
                        case 2: {

                            intent = new Intent(MainActivity.this, ClassManageActivity.class);
                            startActivity(intent);
                        }
                        break;
                        case 3:{
                            intent = new Intent(MainActivity.this, ChangeCourseActivity.class);
                            intent.putExtra("opentype","selectChat");
                            startActivity(intent);
                        }break;

                    }
                }
        });
    }
}



