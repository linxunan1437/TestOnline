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
import WebUtil.ConfigUtil;

public class AccountManageActivity extends AppCompatActivity {
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_manage);
        this.setTitle("账号管理");
        listView=(ListView)findViewById(R.id.listview);
        String[] tools=new String[]{"修改密码","修改班级"};
        listView.setAdapter(new ArrayAdapter<String>(AccountManageActivity.this,android.R.layout.simple_list_item_1,tools));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            Intent intent;
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                switch (position){
                    case 0:
                    {
                        intent = new Intent(AccountManageActivity.this,modifiypasswordAcitivity.class);
                        startActivity(intent);
                    }break;
                    case 1:
                    {
                        intent = new Intent(AccountManageActivity.this,TestManagerActivity.class);
                        startActivity(intent);
                    }break;

                }
            }
        });
    }


}
