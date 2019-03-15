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

import WebUtil.WebConnection;
import okhttp3.FormBody;

import static staticSetting.Setting.projectName;
import static staticSetting.Setting.serverUrl;

public class SignupActivity extends AppCompatActivity {
    Button submit;
    EditText editTextName,editTextPass,editTextTwice;
    CheckBox isTutor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        init();
    }
    private void init(){
        editTextName=(EditText)findViewById(R.id.editUserName) ;
        editTextPass=(EditText)findViewById(R.id.editPassword);
        editTextTwice=(EditText)findViewById(R.id.editTwicePass);
        submit=(Button)findViewById(R.id.buttonSubmit);
        isTutor=(CheckBox)findViewById(R.id.checkIsTutor);
        submit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(editTextName.getText().toString().isEmpty()||editTextPass.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"用户名和密码不能为空",Toast.LENGTH_LONG).show();
                    return;
                }
                if(editTextPass.getText().toString().equals(editTextTwice.getText().toString())){
                    String url = serverUrl + "/" + projectName + "/LoginServlet";
                    new SignupActivity.myTask().execute(url,"signup");
                }
                else{
                    Toast.makeText(getApplicationContext(),"两次输入的密码不一致",Toast.LENGTH_LONG).show();
                }
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
    class myTask extends AsyncTask<String,Integer,String> {
        @Override
        protected String doInBackground(String... params){
            //后台运行
            String url = params[0];
            String result=dologin(url,params[1]);

            return result;
        }
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            //返回注册信息

            if(result.equals("fail")){Toast.makeText(getApplicationContext(),"注册失败",Toast.LENGTH_LONG).show();return;}//失败则无反应
            if(result.equals("existed username")){Toast.makeText(getApplicationContext(),"用户名已经存在",Toast.LENGTH_LONG).show();return;}//已经存在的账号
            Toast.makeText(getApplicationContext(),"注册成功",Toast.LENGTH_LONG).show();
//            Intent intent=new Intent(SignupActivity.this,LoginActivity.class);
//            startActivity(intent);

        }
    }
}
