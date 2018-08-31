package com.example.lh.testonline;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class TestManagerActivity extends AppCompatActivity {
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_manager);
        this.setTitle("考试管理");
        listView=(ListView)findViewById(R.id.listview);
        String[] tools=new String[]{"当前考试","考试结果","考试阅卷"};
        listView.setAdapter(new ArrayAdapter<String>(TestManagerActivity.this,android.R.layout.simple_list_item_1,tools));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            Intent intent;
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                switch (position){
                    case 0:
                    {
                        intent = new Intent(TestManagerActivity.this,OpeningTestActivity.class);
                        intent.putExtra("type","test");
                        startActivity(intent);
                    }break;
                    case 1:
                    {
                        intent = new Intent(TestManagerActivity.this,OpeningTestActivity.class);
                        intent.putExtra("type","result");
                        startActivity(intent);
                    }break;

                }
            }
        });
    }
}
