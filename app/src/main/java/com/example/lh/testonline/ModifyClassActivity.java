package com.example.lh.testonline;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import DataType.Classes;
import DataType.Test;
import WebUtil.ConfigUtil;
import WebUtil.WebConnection;
import okhttp3.FormBody;

import static staticSetting.Setting.projectName;
import static staticSetting.Setting.serverUrl;

public class ModifyClassActivity extends AppCompatActivity {
    Button buttonSearch;
    EditText textSearch;
    ListView classListView;

    List<Classes> classeslist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_class);
        init();
    }
    private void init(){
        buttonSearch=(Button)findViewById(R.id.buttonSearchClass);
        textSearch=(EditText)findViewById(R.id.editSearchClass);
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = serverUrl+"/"+projectName+"/ModifyServlet";
                new ModifyClassActivity.myTask().execute(url,"search");
            }
        });
        classListView=(ListView)findViewById(R.id.ListSearchClass);


    }
    //
    //搜索post
    public String searchClass(String url){
        String success= "";
        String key=textSearch.getText().toString();
        FormBody formBody = new FormBody.Builder()
                .add("modifyType", "SearchClass")
                .add("key", key)
                .build();
        success= WebConnection.doPost(url,formBody);
        return success;
    }
    //添加post
    public String addClass(String url,String classid){
        String success= "";
        String key=textSearch.getText().toString();
        FormBody formBody = new FormBody.Builder()
                .add("modifyType", "class")
                .add("operation","add")
                .add("uid", String.valueOf(new ConfigUtil(this).getUser().getUid()))
                .add("classid",classid)
                .build();
        success= WebConnection.doPost(url,formBody);
        return success;

    }
    class myTask extends AsyncTask<String,Integer,String> {
        @Override
        protected String doInBackground(String... params){
            //后台运行
            String url = params[0];
            String operation  = params[1];
            String result="fail";
            if(operation.equals("search"))
                return searchClass(url);
            if(operation.equals("add"))
                return addClass(url,params[2]);
            return result;
        }
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            //将搜索得到的班级信息放到容器中
            if(result.equals("fail")){Toast.makeText(ModifyClassActivity.this, "搜索失败", Toast.LENGTH_SHORT).show();return;}
            if(result.equals("null")){Toast.makeText(ModifyClassActivity.this, "没有相应结果", Toast.LENGTH_SHORT).show();return;}
            if(result.equals("success")){Toast.makeText(ModifyClassActivity.this, "添加成功", Toast.LENGTH_SHORT).show();return;}
            Gson gson= new Gson();
            classeslist=gson.fromJson(result,new TypeToken<List<Classes>>(){}.getType());
            //gui设定
            ClassesAdapter classesAdapter= new ClassesAdapter(ModifyClassActivity.this,R.layout.classlist,classeslist);
            classListView.setAdapter(classesAdapter);
            classListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                Intent intent;
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {

                }
            });
        }
    }

    public class ClassesAdapter extends ArrayAdapter<Classes> {
        private int resourceId;
        public ClassesAdapter(Context context,int textViewId,List<Classes> list){
            super(context,textViewId,list);
            resourceId=textViewId;
        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent){
            Classes classes= getItem(position);
            View view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
            TextView textView= (TextView)view.findViewById(R.id.textClassSearchResult);
            textView.setText(classes.toString());
            Button buttonAddClass=(Button)view.findViewById(R.id.buttonAddClass);
            buttonAddClass.setText("添加");
            buttonAddClass.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //    通过AlertDialog.Builder这个类来实例化我们的一个AlertDialog的对象
                    AlertDialog.Builder builder = new AlertDialog.Builder(ModifyClassActivity.this);
                    //    设置Title的内容
                    builder.setTitle("确认");
                    //    设置Content来显示一个信息
                    builder.setMessage("确定添加吗？");
                    //    设置一个PositiveButton
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            String url = serverUrl+"/"+projectName+"/ModifyServlet";
                            new ModifyClassActivity.myTask().execute(url,"add",classeslist.get(position).getId());
                        }
                    });
                    //    设置一个NegativeButton
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                        }
                    });

                    builder.show();

                }
            });
            return view;
        }
    }
}



