package com.example.lh.testonline;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.CountDownTimer;
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import DataType.ClassAnswer;
import DataType.Question;
import DataType.Test;
import DataType.User;
import GUISetting.myFragmentStatePagerAdapter;
import WebUtil.ConfigUtil;
import WebUtil.WebConnection;
import okhttp3.FormBody;

import static staticSetting.Setting.projectName;
import static staticSetting.Setting.serverUrl;

public class TestAcitivity extends FragmentActivity {

    //考试数据
    int presentQuestionID,numOfQuestions;
    long presentTime;
    String testID;
    Test test,testResult;
    ClassAnswer classAnswer;
    //控制参数
    String opentype;
    boolean readTest;
    boolean tutor;
    //ui对象
    TextView textPage,textTime;
    MutipleChoiceFragment singleChoose;
    ResultFragment resultShow;
    MarkingFragment markingShow;
    TeacherTestResultFragment teacherTestResultFragment;
    LinearLayout next,former;
    Button buttonSubmit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_acitivity);
        init();
    }
    private void init(){
        //从上一个活动获得开启类型(考试,阅卷,查看结果)和启动编号
        Intent intent=getIntent();
        opentype=intent.getStringExtra("type");
        testID= intent.getStringExtra("testID");
        readTest=false;
        ConfigUtil configUtil=new ConfigUtil(TestAcitivity.this);
        User u= configUtil.getUser();
        if(u.getTutor().equals("yes"))tutor=true;else tutor=false;
        //控件添加
        next=(LinearLayout)findViewById(R.id.nextQuestion);
        former=(LinearLayout)findViewById(R.id.formerQuestion);
        textPage=(TextView)findViewById(R.id.textPage) ;
        textTime=(TextView)findViewById(R.id.textTime);
        buttonSubmit=(Button)findViewById(R.id.buttonSubmit);

        if(opentype.equals("result")||opentype.equals("teacherResult"))buttonSubmit.setVisibility(View.INVISIBLE);
        if(opentype.equals("marking")){
            next.setVisibility(View.INVISIBLE);
            former.setVisibility(View.INVISIBLE);
        }
        //添加试题碎片到本activity
        singleChoose= new MutipleChoiceFragment();
        resultShow=new ResultFragment();
        markingShow=new MarkingFragment();
        teacherTestResultFragment=new TeacherTestResultFragment();
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        if(opentype.equals("result"))
            transaction.add(R.id.fragment_container, resultShow);
        if(opentype.equals("test"))
            transaction.add(R.id.fragment_container, singleChoose);
        if(opentype.equals("marking"))
            transaction.add(R.id.fragment_container, markingShow);
        if(opentype.equals("teacherResult"))
            transaction.add(R.id.fragment_container, teacherTestResultFragment);
        transaction.commit();

        String url;
        //读取试卷
        if(!opentype.equals("teacherResult")) {
            url = serverUrl + "/" + projectName + "/ReadTestServlet";
            new TestAcitivity.myTask().execute(url, "initTest");
        }
        if(opentype.equals("teacherResult")) {
            url = serverUrl + "/" + projectName + "/ReadTestServlet";
            new TestAcitivity.myTask().execute(url, "initTeacher");
        }
        if(opentype.equals("result")) {
            url = serverUrl + "/" + projectName + "/ReadTestServlet";
            new TestAcitivity.myTask().execute(url, "initResult");
        }
        if(opentype.equals("marking")){
            url = serverUrl + "/" + projectName + "/ReadTestServlet";
            new TestAcitivity.myTask().execute(url, "initMarking");
        }




        //按钮点击事件
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(opentype.equals("test"))nextQuestion();
                if(opentype.equals("result"))nextResult();
                if(opentype.equals("marking"))nextMarking();
                if(opentype.equals("teacherResult"))nextTeacherResult();
            }
        });
        former.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(opentype.equals("test"))formerQuestion();
                if(opentype.equals("result"))formerResult();
                if(opentype.equals("marking"))formerMarking();
                if(opentype.equals("teacherResult"))formerTeacherResult();
            }
        });
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(opentype.equals("test"))submit();
                if(opentype.equals("marking"))submitMarking();
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
            if(presentQuestionID!=1) {
                //修改问题
                if(tutor==false) {
                    String answer = singleChoose.changeQuestion(
                            String.valueOf(presentQuestionID),
                            test.getQuestions().get(presentQuestionID - 1).getQuestion(),
                            test.getQuestions().get(presentQuestionID - 1).getScore(),
                            test.getQuestions().get(presentQuestionID - 1).getAnswer()
                    );

                    //保存答案
                    test.getQuestions().get(presentQuestionID - 2).setAnswer(answer);
                }else{
                    singleChoose.changeQuestion(
                            String.valueOf(presentQuestionID),
                            test.getQuestions().get(presentQuestionID - 1).getQuestion(),
                            test.getQuestions().get(presentQuestionID - 1).getScore(),
                            test.getQuestions().get(presentQuestionID - 1).getAnswer()
                    );
                }
            }
            else {
                if (tutor == false) {
                    singleChoose.changeQuestion(
                            String.valueOf(presentQuestionID),
                            test.getQuestions().get(presentQuestionID - 1).getQuestion(),
                            test.getQuestions().get(presentQuestionID-1).getScore(),
                            ""
                    );

                }else {
                singleChoose.changeQuestion(
                        String.valueOf(presentQuestionID),
                        test.getQuestions().get(presentQuestionID - 1).getQuestion(),
                        test.getQuestions().get(presentQuestionID - 1).getScore(),
                        test.getQuestions().get(presentQuestionID - 1).getAnswer()
                );
                }
            }
            textPage.setText(presentQuestionID + "/" + numOfQuestions);
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
            if(tutor==false) {
                String answer = singleChoose.changeQuestion(
                        String.valueOf(presentQuestionID),
                        test.getQuestions().get(presentQuestionID - 1).getQuestion(),
                        test.getQuestions().get(presentQuestionID - 1).getScore(),
                        test.getQuestions().get(presentQuestionID - 1).getAnswer()
                );

                //保存答案
                test.getQuestions().get(presentQuestionID).setAnswer(answer);
            }else{
                singleChoose.changeQuestion(
                        String.valueOf(presentQuestionID),
                        test.getQuestions().get(presentQuestionID - 1).getQuestion(),
                        test.getQuestions().get(presentQuestionID - 1).getScore(),
                        test.getQuestions().get(presentQuestionID - 1).getAnswer()
                );
            }
        }
        textPage.setText(presentQuestionID + "/" + numOfQuestions);

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
    private void submitMarking(){
        String url = serverUrl+"/"+projectName+"/SubmitTestServlet";
        new TestAcitivity.myTask().execute(url,"submitMarking");
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

    //本方法实现阅卷时翻页功能
    private void nextMarking(){
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
            int getscore=markingShow.changeQuestion(
                    String.valueOf(presentQuestionID),
                    test.getQuestions().get(presentQuestionID-1).getQuestion(),
                    testResult.getQuestions().get(presentQuestionID-1).getAnswer(),
                    test.getQuestions().get(presentQuestionID-1).getAnswer(),
                    String.valueOf(test.getQuestions().get(presentQuestionID-1).getScore())
            );
            textPage.setText(presentQuestionID+"/"+numOfQuestions);
            //保存分数
            if(presentQuestionID>1)
            testResult.getQuestions().get(presentQuestionID-2).setScore(getscore);
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
    private void formerMarking(){
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
            int getscore=markingShow.changeQuestion(
                    String.valueOf(presentQuestionID),
                    test.getQuestions().get(presentQuestionID-1).getQuestion(),
                    testResult.getQuestions().get(presentQuestionID-1).getAnswer(),
                    test.getQuestions().get(presentQuestionID-1).getAnswer(),
                    String.valueOf(test.getQuestions().get(presentQuestionID-1).getScore())
            );
            textPage.setText(presentQuestionID+"/"+numOfQuestions);
            //保存分数
            testResult.getQuestions().get(presentQuestionID).setScore(getscore);
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
    //本方法实现教师查看每题详情时翻页的功能
    private void nextTeacherResult(){
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
            teacherTestResultFragment.changeQuestion(
                    String.valueOf(presentQuestionID),
                    classAnswer.getTest().getQuestions().get(presentQuestionID-1).getQuestion(),
                    classAnswer.getTest().getQuestions().get(presentQuestionID-1).getAnswer(),
                    String.valueOf(classAnswer.getTest().getQuestions().get(presentQuestionID-1).getScore()),
                    classAnswer.getStatus(presentQuestionID-1)
            );
            textPage.setText(presentQuestionID+"/"+numOfQuestions);
        }
    }
    private void formerTeacherResult(){
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
            teacherTestResultFragment.changeQuestion(
                    String.valueOf(presentQuestionID),
                    classAnswer.getTest().getQuestions().get(presentQuestionID-1).getQuestion(),
                    classAnswer.getTest().getQuestions().get(presentQuestionID-1).getAnswer(),
                    String.valueOf(classAnswer.getTest().getQuestions().get(presentQuestionID-1).getScore()),
                    classAnswer.getStatus(presentQuestionID-1)
            );
            textPage.setText(presentQuestionID+"/"+numOfQuestions);
        }
    }


    //提交考试
    private String submitTest(String url){
        if(tutor==true)return "";
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
    //提交阅卷
    private String submitMarking(String url){
        String success= "";
        String uid=String.valueOf(new ConfigUtil(this).getUser().getUid());
        List<String> answers=new ArrayList<String>();
        String mark=new Gson().toJson(this.testResult);
        FormBody formBody = new FormBody.Builder()
                .add("uid",uid )
                .add("mark",mark)
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
    //上传编号获得考试结果
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
    //上传编号获得阅卷信息
    private String initMarking(String url){

        String success= "";
        String uid=String.valueOf(new ConfigUtil(this).getUser().getUid());
        FormBody formBody = new FormBody.Builder()
                .add("uid",uid)
                .add("testID",testID)
                .add("type","marking")
                .build();
        success= WebConnection.doPost(url,formBody);
        return success;
    }
    //上传编号获得考试答案
    private String initTeacher(String url){

        String success= "";
        String uid=String.valueOf(new ConfigUtil(this).getUser().getUid());
        FormBody formBody = new FormBody.Builder()
                .add("uid",uid)
                .add("testID",testID)
                .add("type","teacherResult")
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
            if(operation.equals("initMarking"))
                return initMarking(url);
            if(operation.equals("initTeacher"))
                return initTeacher(url);
            if(operation.equals("submitMarking"))
                return submitMarking(url);
            return "fail";
        }
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            //返回题目信息
            //Toast.makeText(getApplicationContext(),result,Toast.LENGTH_LONG).show();
            if(result.equals("submit success")){Toast.makeText(getApplicationContext(),"提交成功",Toast.LENGTH_LONG).show();
                finish();
                Intent intent = new Intent(TestAcitivity.this, TestManagerActivity.class);
                startActivity(intent);
                return;}
            if(result.equals("fail")){Toast.makeText(getApplicationContext(),"连接失败",Toast.LENGTH_LONG).show();return;}
            if(result.isEmpty())return;
            //用GSON解释成考试类
            Gson gson = new Gson();

            if(opentype.equals("test")) {

                if(tutor==true){
                    buttonSubmit.setVisibility(View.INVISIBLE);
                    singleChoose.seteditable(false);
                }
                test = gson.fromJson(result, Test.class);
                presentQuestionID = 0;
                numOfQuestions = test.getQuestions().size();

                //把问题的答案清除用于存储用户的答案
                List<Question> qlist=test.getQuestions();
                if(tutor==false) {
                    for (int i = 0; i < numOfQuestions; i++) {
                        qlist.get(i).setAnswer("");
                    }
                }
                test.setQuestions(qlist);
                //初始指向第一题
                nextQuestion();
                //考试的倒计时

                    //直接用结束时间减去当前时间作为倒计时的工具
                    DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    try {
                        long end= format.parse(test.getEndtime()).getTime();
                        long start = new Date().getTime();
                        //由于new Date()获得的是英国时间,我们需要加上八个小时来变成东八区
//                        start+=28800000;
                        presentTime = (end-start)/1000;
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    CountDownTimer timer = new CountDownTimer(presentTime * 1000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            presentTime--;
                            long h=presentTime/3600;
                            long m=(presentTime%3600)/60;
                            long s=presentTime%60;
                            textTime.setText(String.valueOf(h)+':'+String.valueOf(m)+':'+String.valueOf(s));

                        }

                        @Override
                        public void onFinish() {

                            Toast.makeText(TestAcitivity.this, "time up", Toast.LENGTH_LONG);
                            submit();
                            finish();
                        }
                    }.start();

            }
            if(opentype.equals("result")){

                if(readTest==false) {
                    //读取标准答案
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
                    textPage.setText(1+"/"+numOfQuestions);
                    nextResult();
                }
            }
            if(opentype.equals("marking")){
                if(readTest==false) {
                    //读取标准答案
                    test = gson.fromJson(result, Test.class);
                    presentQuestionID = 0;
                    numOfQuestions = test.getQuestions().size();
                    readTest=true;
                }
                else{
                    //在获得结果后再对界面进行操作
                    testResult= gson.fromJson(result, Test.class);
                    //去掉不必要的标准答案
                    for(int i =0;i<numOfQuestions;i++)
                    {
                        boolean e=false;
                        for(int j=0;j<testResult.getQuestions().size();j++)
                        {
                            if(test.getQuestions().get(i).getId().equals(testResult.getQuestions().get(j).getId()))e=true;
                        }
                        if(e)continue;
                        test.getQuestions().remove(i);i--;numOfQuestions--;
                    }
                    presentQuestionID = 0;
                    numOfQuestions = testResult.getQuestions().size();
                    nextMarking();
                }
            }
            if(opentype.equals("teacherResult")){
                classAnswer= gson.fromJson(result, ClassAnswer.class);
                presentQuestionID=1;
                numOfQuestions=classAnswer.getTest().getQuestions().size();
                String status;
                textPage.setText(1+"/"+numOfQuestions);
                teacherTestResultFragment.changeQuestion(
                        String.valueOf(presentQuestionID),
                        classAnswer.getTest().getQuestions().get(0).getQuestion(),
                        classAnswer.getTest().getQuestions().get(0).getAnswer(),
                        String.valueOf(classAnswer.getTest().getQuestions().get(0).getScore()),
                        classAnswer.getStatus(0)
                );
            }
        }
    }
}
