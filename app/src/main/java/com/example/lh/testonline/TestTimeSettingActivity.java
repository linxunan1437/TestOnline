package com.example.lh.testonline;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import java.security.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import DataType.Test;

public class TestTimeSettingActivity extends AppCompatActivity {
    Button ensure;
    EditText startTime,startDate,endTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_time_setting);
        init();
    }
    private void init(){

        ensure=(Button)findViewById(R.id.buttonTimeSet);
        startTime=(EditText)findViewById(R.id.editStartTime);
        startDate=(EditText)findViewById(R.id.editStartDate);
        endTime=(EditText)findViewById(R.id.editEndTime) ;
        Intent intent=getIntent();
        ensure.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //检查输入的时间是否符合规范
                try{
                    String dat=startDate.getText().toString(),tims=startTime.getText().toString(),time=endTime.getText().toString();
                    if(!timeCheck(tims,time,dat)) {
                        Toast.makeText(getApplicationContext(),
                                "输入的时间格式不合法", Toast.LENGTH_LONG).show();
                        return;
                    }
                    Intent intent = new Intent();
                    Test s=new Test();s.setStarttime(dat+' '+tims);
                    s.setEndtime(dat+' '+time);
                    intent.putExtra("test",new Gson().toJson(s));
                    setResult(2,intent);
                    finish();
                }catch(Exception e){
                    Toast.makeText(getApplicationContext(),
                            "输入的时间格式不合法",Toast.LENGTH_LONG).show();
                }

            }
        });
    }
    //检测时间是否合法
    private boolean timeCheck(String s,String e,String dat){
        Date cstart,cend;
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        try{
//            String[] ts=s.split(":"),es=e.split(":");
//            String[] dats=dat.split("-");
//            int sth=Integer.parseInt(ts[0]),eth=Integer.parseInt(es[0]);
//            int stm=Integer.parseInt(ts[1]),etm=Integer.parseInt(es[1]);
//            int daty=Integer.parseInt(dats[0]),datm=Integer.parseInt(dats[1]),datd=Integer.parseInt(dats[2]);
//            cstart.set(daty,datm,datd,sth,stm);
//            cend.set(daty,datm,datd,eth,etm);
            String[] temp=dat.split("-");
            if(temp[1].length()==1)temp[1]='0'+temp[1];
            if(temp[2].length()==1)temp[2]='0'+temp[2];
            dat=temp[0]+'-'+temp[1]+'-'+temp[2];
            cstart  = format.parse(dat+' '+s);
            cend=format.parse(dat+' '+e);
            //结束时间不晚于开始时间
            if(cend.before(cstart))return false;
        }catch (Exception ex){
            return false;
        }
        return true;
    }
}
