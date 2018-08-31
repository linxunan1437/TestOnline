package com.example.lh.testonline;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import DataType.Question;
import DataType.Test;
import GUISetting.myFragmentStatePagerAdapter;
import WebUtil.ConfigUtil;
import WebUtil.WebConnection;
import okhttp3.FormBody;

import static staticSetting.Setting.projectName;
import static staticSetting.Setting.serverUrl;

public class TestAcitivity extends FragmentActivity {

    //考试数据
    int presentQuestionID,numOfQuestions;
    String testID;
    Test test,testResult;
    //控制参数
    String opentype;
    boolean readTest;
    //ui对象
    TextView textPage;
    MutipleChoiceFragment singleChoose;
    ResultFragment resultShow;
    LinearLayout next,former;
    Button buttonSubmit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_acitivity);
        init();
    }
    private void init(){
        //从上一个活动获得开启类型和启动编号
        Intent intent=getIntent();
        opentype=intent.getStringExtra("type");
        testID= intent.getStringExtra("testID");
        readTest=false;
        //控件添加
        next=(LinearLayout)findViewById(R.id.nextQuestion);
        former=(LinearLayout)findViewById(R.id.formerQuestion);
        textPage=(TextView)findViewById(R.id.textPage) ;
        buttonSubmit=(Button)findViewById(R.id.buttonSubmit);
        if(opentype.equals("result"))buttonSubmit.setVisibility(View.INVISIBLE);
        if(opentype.equals("test"))buttonSubmit.setVisibility(View.VISIBLE);
        //添加试题碎片到本activity
        singleChoose= new MutipleChoiceFragment();
        resultShow=new ResultFragment();
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        if(opentype.equals("result"))
            transaction.add(R.id.fragment_container, resultShow);
        if(opentype.equals("test"))
            transaction.add(R.id.fragment_container, singleChoose);
        transaction.commit();

        //从上一个活动获取考试编号

        //读取试卷
        String url = serverUrl+"/"+projectName+"/ReadTestServlet";
        new TestAcitivity.myTask().execute(url,"initTest");

        if(opentype.equals("result")) {
            url = serverUrl + "/" + projectName + "/ReadTestServlet";
            new TestAcitivity.myTask().execute(url, "initResult");
        }
        //final ViewPager viewPager=(ViewPager)findViewById(R.id.questionPager);
        //final List<Fragment> list = new ArrayList<Fragment>();
        /*viewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return list.get(position);
            }

            @Override
            public int getCount() {
                return list.size();
            }
        });*/
        //按钮点击事件
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(opentype.equals("test"))nextQuestion();
                if(opentype.equals("result"))nextResult();
            }
        });
        former.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(opentype.equals("test"))formerQuestion();
                if(opentype.equals("result"))formerResult();
            }
        });
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });

    }
    //本方法实现下一题的功能
    private void nextQuestion(){
        //保存本题答案
        //点击下一题时显示下一题
        presentQuestionID++;
        if(presentQuestionID>numOfQuestions)
        {
            //已经到卷尾
            presentQuestionID--;
            return;
        }
        else
        {
            //修改问题
            String answer=singleChoose.changeQuestion(
                    String.valueOf(presentQuestionID),
                    test.getQuestions().get(presentQuestionID-1).getQuestion()
            );
            textPage.setText(presentQuestionID+"/"+numOfQuestions);
            //保存答案
            if(presentQuestionID-2>=0)test.getQuestions().get(presentQuestionID-2).setAnswer(answer);
        }
                /*
                switch (test.getQuestions().get(presentQuestionID).getType()){
                    case "singleChoose":{
                        viewPager.setCurrentItem(0);//单选题
                    }break;
                    case "multiChoose":{
                        viewPager.setCurrentItem(0);//多选题
                    }break;
                    default:{
                        viewPager.setCurrentItem(0);//非选择题
                    }
                }*/

    }
    //本方法实现上一题的功能
    private void formerQuestion(){
        //保存本题答案
        //点击上一题时显示上一题
        presentQuestionID--;
        if(presentQuestionID<=0)
        {
            //已经到卷尾
            presentQuestionID++;
            return;
        }
        else
        {
            //修改问题
            String answer=singleChoose.changeQuestion(String.valueOf(presentQuestionID),test.getQuestions().get(presentQuestionID).getQuestion());
            textPage.setText(presentQuestionID+"/"+numOfQuestions);
            //保存答案
            test.getQuestions().get(presentQuestionID).setAnswer(answer);
        }
                /*
                switch (test.getQuestions().get(presentQuestionID).getType()){
                    case "singleChoose":{
                        viewPager.setCurrentItem(0);//单选题
                    }break;
                    case "multiChoose":{
                        viewPager.setCurrentItem(0);//多选题
                    }break;
                    default:{
                        viewPager.setCurrentItem(0);//非选择题
                    }
                }*/

    }
    //本方法实现提交功能
    private void submit(){
        test.getQuestions().get(presentQuestionID-1).setAnswer(singleChoose.getAnswer());
        String url = serverUrl+"/"+projectName+"/SubmitTestServlet";
        new TestAcitivity.myTask().execute(url,"submitTest");

    }

    //本方法实现下一个结果的功能
    private void nextResult(){
        //点击下一题时显示下一题
        presentQuestionID++;
        if(presentQuestionID>numOfQuestions)
        {
            //已经到卷尾
            presentQuestionID--;
            return;
        }
        else
        {
            //修改问题
            resultShow.changeQuestion(
                    String.valueOf(presentQuestionID),
                    test.getQuestions().get(presentQuestionID-1).getQuestion(),
                    testResult.getQuestions().get(presentQuestionID-1).getAnswer(),
                    test.getQuestions().get(presentQuestionID-1).getAnswer(),
                    testResult.getQuestions().get(presentQuestionID-1).getScore()+"/"+test.getQuestions().get(presentQuestionID-1).getScore()
            );
            textPage.setText(presentQuestionID+"/"+numOfQuestions);
       }
                /*
                switch (test.getQuestions().get(presentQuestionID).getType()){
                    case "singleChoose":{
                        viewPager.setCurrentItem(0);//单选题
                    }break;
                    case "multiChoose":{
                        viewPager.setCurrentItem(0);//多选题
                    }break;
                    default:{
                        viewPager.setCurrentItem(0);//非选择题
                    }
                }*/

    }

    private void formerResult(){
        //点击上一题时显示上一题
        presentQuestionID--;
        if(presentQuestionID<=0)
        {
            //已经到卷头
            presentQuestionID++;
            return;
        }
        else
        {
            //修改问题
            resultShow.changeQuestion(
                    String.valueOf(presentQuestionID),
                    test.getQuestions().get(presentQuestionID-1).getQuestion(),
                    testResult.getQuestions().get(presentQuestionID-1).getAnswer(),
                    test.getQuestions().get(presentQuestionID-1).getAnswer(),
                    testResult.getQuestions().get(presentQuestionID-1).getScore()+"/"+test.getQuestions().get(presentQuestionID-1).getScore()
            );
            textPage.setText(presentQuestionID+"/"+numOfQuestions);
        }
    }

    //提交考试
    private String submitTest(String url){

        String success= "";
        String uid=String.valueOf(new ConfigUtil(this).getUser().getUid());
        List<String> answers=new ArrayList<String>();
        String answer=new Gson().toJson(this.test);
        FormBody formBody = new FormBody.Builder()
                .add("uid",uid )
                .add("answer",answer)

                .build();
        success= WebConnection.doPost(url,formBody);
        return success;
    }
    //上传考试编号获得考试内容
    private String initTest(String url){

        String success= "";
        int uid=0;
        FormBody formBody = new FormBody.Builder()
                .add("uid",String.valueOf(uid))
                .add("testID",testID)
                .add("type","test")
                .build();
        success= WebConnection.doPost(url,formBody);
        return success;
    }

    //上传编号获得结果
    private String initResult(String url){

        String success= "";
        String uid=String.valueOf(new ConfigUtil(this).getUser().getUid());
        FormBody formBody = new FormBody.Builder()
                .add("uid",uid)
                .add("testID",testID)
                .add("type","result")
                .build();
        success= WebConnection.doPost(url,formBody);
        return success;
    }
    class myTask extends AsyncTask<String,Integer,String> {
        @Override
        protected String doInBackground(String... params){
            //后台运行
            String url = params[0];
            String operation = params[1];
            String result=new String();
            if(operation.equals("initTest"))
                return initTest(url);
            if(operation.equals("submitTest"))
                return submitTest(url);
            if(operation.equals("initResult"))
                return initResult(url);
            return "fail";
        }
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            //返回题目信息
            //Toast.makeText(getApplicationContext(),result,Toast.LENGTH_LONG).show();
            if(result.equals("submit success")){Toast.makeText(getApplicationContext(),result,Toast.LENGTH_LONG).show();return;}
            if(result.equals("fail")){Toast.makeText(getApplicationContext(),result,Toast.LENGTH_LONG).show();return;}
            //用GSON解释成考试类
            Gson gson = new Gson();

            if(opentype.equals("test")) {

                test = gson.fromJson(result, Test.class);
                presentQuestionID = 0;
                numOfQuestions = test.getQuestions().size();
                //初始指向第一题
                nextQuestion();
            }
            if(opentype.equals("result")){

                if(readTest==false) {
                    test = gson.fromJson(result, Test.class);
                    presentQuestionID = 0;
                    numOfQuestions = test.getQuestions().size();
                    readTest=true;
                }
                else{
                    //在获得结果后再对界面进行操作
                    testResult= gson.fromJson(result, Test.class);
                    presentQuestionID = 0;
                    numOfQuestions = test.getQuestions().size();
                }
            }
        }
    }
}
