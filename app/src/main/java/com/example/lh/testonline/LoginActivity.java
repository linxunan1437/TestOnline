package com.example.lh.testonline;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import WebUtil.ConfigUtil;
import WebUtil.WebConnection;
import okhttp3.FormBody;

import static staticSetting.Setting.projectName;
import static staticSetting.Setting.serverUrl;


public class LoginActivity extends AppCompatActivity {
    EditText editTextName,editTextPass;
    Button loginbutton,signupbutton;
    CheckBox isTutor;
    ConfigUtil configUtil;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("登录");
        configUtil = new ConfigUtil(this);
        editTextName=(EditText)findViewById(R.id.editUsername);
        editTextPass=(EditText)findViewById(R.id.editPassword);
        loginbutton=(Button)findViewById(R.id.button);
        isTutor=(CheckBox)findViewById(R.id.checkBox);
        signupbutton=(Button)findViewById(R.id.buttonSignUp);
        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = serverUrl + "/" + projectName + "/LoginServlet";
                new myTask().execute(url,"login");
            }
        });
        signupbutton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent=new Intent(LoginActivity.this,SignupActivity.class);
                startActivity(intent);

            }
        });


    }
    public String dologin(String url,String type){
        //
        String success= "";
        String username=editTextName.getText().toString();
        String password=editTextPass.getText().toString();
        String tutor="no";
        if(isTutor.isChecked())tutor="yes";
        FormBody formBody = new FormBody.Builder()
                .add("type",type)
                .add("username", username)
                .add("password", password)
                .add("tutor",tutor)
                .build();
        success= WebConnection.doPost(url,formBody);
        return success;
    }


    class myTask extends AsyncTask<String,Integer,String>{
        @Override
        protected String doInBackground(String... params){
            //后台运行
            String url = params[0];
            String result=dologin(url,params[1]);

            return result;
        }
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            //返回登陆信息

            if(result.equals("fail")){Toast.makeText(getApplicationContext(),"用户名或密码错误",Toast.LENGTH_LONG).show();return;}
            configUtil.setUserJson(result);//若不失败则需要记录用户信息供后面使用
            Intent intent=new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);

        }
    }
}
