package com.example.lh.testonline;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import DataType.Classes;
import DataType.User;
import WebUtil.ConfigUtil;
import WebUtil.WebConnection;
import okhttp3.FormBody;

import static staticSetting.Setting.projectName;
import static staticSetting.Setting.serverUrl;

public class CreateClassActivity extends AppCompatActivity {
    //控件
    Button ensure;
    EditText editId,editorder,editclassname;
    //数据
    Classes classes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_class);
        init();
    }

    private void init(){
        classes=new Classes();
        ensure=(Button)findViewById(R.id.buttonEnsure);
        editId=(EditText)findViewById(R.id.editCourseId);
        editorder=(EditText)findViewById(R.id.editCourseOrder);
        editclassname=(EditText)findViewById(R.id.editClassName);
        ensure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editclassname.getText().toString().isEmpty()||editId.getText().toString().isEmpty()||editorder.getText().toString().isEmpty()){
                    Toast.makeText(CreateClassActivity.this, "信息不完整", Toast.LENGTH_SHORT).show();
                    return;
                }
                classes.setClassName(editclassname.getText().toString());
                classes.setId(editId.getText().toString()+' '+editorder.getText().toString());
                ConfigUtil configUtil=new ConfigUtil(CreateClassActivity.this);
                User u= configUtil.getUser();
                classes.setTeacher(u);
                String url = serverUrl + "/" + projectName + "/ModifyServlet";
                new CreateClassActivity.myTask().execute(url);
            }
        });
    }
    public String submitClasses(String url){
        String success= "";
        FormBody formBody = new FormBody.Builder()
                .add("classes", new Gson().toJson(classes))
                .add("modifyType","createClass")
                .add("operation","add")
                .build();
        success= WebConnection.doPost(url,formBody);

        return success;
    }

    class myTask extends AsyncTask<String,Integer,String> {
        @Override
        protected String doInBackground(String... params){
            //后台运行
            String url = params[0];
            String result=submitClasses(url);
            return result;
        }
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            if(result.equals("success"))
                Toast.makeText(getApplicationContext(),"创建成功",Toast.LENGTH_LONG).show();
            else
                Toast.makeText(getApplicationContext(),"创建失败",Toast.LENGTH_LONG).show();
        }
    }
}
