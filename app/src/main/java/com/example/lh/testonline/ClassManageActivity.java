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

public class ClassManageActivity extends AppCompatActivity {
    ListView listView;
    String opentype;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_manage);
        init();
    }
    private void init(){
        listView=(ListView)findViewById(R.id.listview);
        ConfigUtil configUtil=new ConfigUtil(ClassManageActivity.this);
        User u= configUtil.getUser();
        String[] tools;
        if(u.getTutor().equals("yes"))
            tools=new String[]{"查找班级","删除班级","创建班级"};
        else
            tools=new String[]{"查找班级","删除班级"};

        listView.setAdapter(new ArrayAdapter<String>(ClassManageActivity.this,android.R.layout.simple_list_item_1,tools));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            Intent intent;

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                switch (position) {
                    case 0: {
                        intent = new Intent(ClassManageActivity.this, ModifyClassActivity.class);
                        startActivity(intent);
                    }
                    break;
                    case 1: {
                        intent = new Intent(ClassManageActivity.this,ChangeCourseActivity.class);
                        intent.putExtra("opentype","manage");
                        startActivity(intent);
                    }
                    break;
                    case 2: {
                        intent = new Intent(ClassManageActivity.this, CreateClassActivity.class);
                        startActivity(intent);
                    }
                    break;


                }
            }
        });
    }
}
