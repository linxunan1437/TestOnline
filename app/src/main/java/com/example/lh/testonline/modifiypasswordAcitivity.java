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

public class modifiypasswordAcitivity extends AppCompatActivity {
    EditText editTextOld,editTextNew,editAssert;
    Button loginbutton;
    CheckBox isTutor;
    ConfigUtil configUtil;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifiypassword);
        setTitle("修改密码");
        configUtil = new ConfigUtil(this);
        editTextOld=(EditText)findViewById(R.id.editOldPass);
        editTextNew=(EditText)findViewById(R.id.editNewPass);
        editAssert=(EditText)findViewById(R.id.editAssertPass);
        loginbutton=(Button)findViewById(R.id.buttonChangePassword);
        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editAssert.getText().toString().equals(editTextNew.getText().toString())) {
                    String pass = configUtil.getUser().getPassword();
                    if(!editTextOld.getText().toString().equals(pass)){
                        Toast.makeText(getApplicationContext(),"密码错误",Toast.LENGTH_LONG).show();
                        return;
                    }
                    String url = serverUrl + "/" + projectName + "/ModifyServlet";
                    new modifiypasswordAcitivity.myTask().execute(url);
                }else{
                    Toast.makeText(getApplicationContext(),"两次输入的新密码不一致",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    public String dologin(String url){
        String success= "";
        String oldpass=editTextOld.getText().toString();
        String newpass=editTextNew.getText().toString();
        int uid = configUtil.getUser().getUid();
        String suid=String.valueOf(uid);
        FormBody formBody = new FormBody.Builder()
                .add("modifyType","password")
                .add("oldpass", oldpass)
                .add("newpass", newpass)
                .add("uid",suid)
                .build();
//        List<NameValuePair> list = new ArrayList<NameValuePair>();
//        NameValuePair p1=new BasicNameValuePair("oldpass",oldpass);
//        NameValuePair p2=new BasicNameValuePair("newpass",newpass);
//        NameValuePair p3=new BasicNameValuePair("uid",suid);
//        list.add(p1);
//        list.add(p2);
//        list.add(p3);
        success= WebConnection.doPost(url,formBody);
        return success;
    }
    class myTask extends AsyncTask<String,Integer,String> {
        @Override
        protected String doInBackground(String... params){
            //后台运行
            String url = params[0];
            String result=dologin(url);
            return result;
        }
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            //返回修改信息
            if(result.equals("success"))Toast.makeText(getApplicationContext(),"修改成功",Toast.LENGTH_LONG).show();
            else Toast.makeText(getApplicationContext(),"修改失败",Toast.LENGTH_LONG).show();
        }
    }
}
