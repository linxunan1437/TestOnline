package com.example.lh.testonline;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.security.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import DataType.Classes;
import DataType.Question;
import DataType.Test;
import WebUtil.ConfigUtil;
import WebUtil.WebConnection;
import okhttp3.FormBody;

import static staticSetting.Setting.projectName;
import static staticSetting.Setting.serverUrl;

public class GiveTestActivity extends AppCompatActivity {
    //控件
    Button submitButton,newQuestion,findFromDatabase,timeSetting;
    ListView questionList;
    TextView textTime,textViewClasses;
    EditText testName,classid,classorder;
    //数据
    Test test;
    String cls;
    //适配器
    QuestionsAdapter adapter;
    final int QUESTION=1,TIME=2,CLASS=3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_give_test);
        init();
    }
    @Override
    protected void onStop(){
        //当退出此界面时保存这次的设置
        super.onStop();
        ConfigUtil configUtil=new ConfigUtil(this);
        configUtil.setTestJson(new Gson().toJson(test,Test.class));

    }

    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        //获得返回值
        if(resultCode==QUESTION){
            Question q;
            q=new Gson().fromJson(data.getStringExtra("question"),Question.class);
            test.getQuestions().add(q);
            questionList.setAdapter(adapter);
        }
        if(resultCode==TIME){
            Test s=new Gson().fromJson(data.getStringExtra("test"),Test.class);
            this.test.setStarttime(s.getStarttime());
            this.test.setEndtime(s.getEndtime());
            textTime.setText(test.getStarttime());
        }
        if(resultCode==CLASS){

            Classes cl=new Gson().fromJson(data.getStringExtra("class"),Classes.class);
            test.setClassid(cl.getId());
            textViewClasses.setText(cl.getClassName());
        }


    }
    private void init(){
        test=new Test();
        ConfigUtil configUtil=new ConfigUtil(this);
        if(configUtil.getTest()!=null)this.test=configUtil.getTest();
        adapter=new QuestionsAdapter(GiveTestActivity.this,R.layout.questionlist,test.getQuestions());
        //获得控件并设定点击事件
        questionList=(ListView)findViewById(R.id.questionList);
        submitButton=(Button)findViewById(R.id.buttonSubmit);
        newQuestion=(Button)findViewById(R.id.buttonNewQuestion);
        findFromDatabase=(Button)findViewById(R.id.buttonSearchQuestion);
        timeSetting=(Button)findViewById(R.id.buttonTimeSetting);
        textTime=(TextView)findViewById(R.id.textTime);
        textViewClasses=(TextView)findViewById(R.id.textViewClasses);
        testName=(EditText)findViewById(R.id.editName);
        classid=(EditText)findViewById(R.id.editCourseId);
        classorder=(EditText)findViewById(R.id.editCourseOrder);
        questionList.setAdapter(adapter);
        textViewClasses.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent= new Intent(GiveTestActivity.this, ChangeCourseActivity.class);
                intent.putExtra("opentype","selectClass");
                startActivityForResult(intent,CLASS);
            }
        });
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                test.setName(testName.getText().toString());
                int ttscore=0;
                for(int i=0;i<test.getQuestions().size();i++){
                    if(test.getQuestions().get(i).getScore()==0){
                        Toast.makeText(getApplicationContext(),"第"+(i+1)+"题没有设置分数",Toast.LENGTH_LONG).show();
                        return ;
                    }
                    ttscore+=test.getQuestions().get(i).getScore();
                }
                if(test.getEndtime().isEmpty())
                {
                    Toast.makeText(getApplicationContext(),"没有设置时间",Toast.LENGTH_LONG).show();
                    return;
                }
                if(test.getName().isEmpty())
                {
                    Toast.makeText(getApplicationContext(),"没有设置考试名称",Toast.LENGTH_LONG).show();
                    return;
                }
                String url = serverUrl + "/" + projectName + "/CreateQuestionServlet";
                new GiveTestActivity.myTask().execute(url,"test");
            }
        });
        newQuestion.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //创建问题
                Intent intent= new Intent(GiveTestActivity.this, CreateQuestion.class);
                intent.putExtra("type","edit");
                startActivityForResult(intent,1);
            }
        });
        findFromDatabase.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //数据库中搜索问题
                Intent intent= new Intent(GiveTestActivity.this, SearchQuestionActivity.class);
                intent.putExtra("type","edit");
                startActivityForResult(intent,1);
            }

        });
        timeSetting.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //设置时间
                Intent intent= new Intent(GiveTestActivity.this, TestTimeSettingActivity.class);
                intent.putExtra("type","edit");
                startActivityForResult(intent,2);
            }

        });

    }
    public String submitTest(String url){
        String success= "";

        String teststring=new Gson().toJson(test);

        FormBody formBody = new FormBody.Builder()
                .add("test", teststring)
                .add("operation","test")
                .build();
        success= WebConnection.doPost(url,formBody);
        return success;
    }
    class myTask extends AsyncTask<String,Integer,String> {
        @Override
        protected String doInBackground(String... params){
            //后台运行
            String url = params[0];
            String result=submitTest(url);

            return result;
        }
        protected void onPostExecute(String result){
            super.onPostExecute(result);

            if(result.equals("fail")){Toast.makeText(getApplicationContext(),"发布失败",Toast.LENGTH_LONG).show();return;}
            Toast.makeText(getApplicationContext(),"发表成功",Toast.LENGTH_LONG).show();

        }
    }
    private class QuestionsAdapter extends ArrayAdapter<Question> {
        private int resourceId;
        public QuestionsAdapter(Context context, int textViewId, List<Question> list){
            super(context,textViewId,list);
            resourceId=textViewId;
        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent){
            Question question= getItem(position);
            View view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
            if(question==null)return view;
            TextView textView= (TextView)view.findViewById(R.id.textClassSearchResult);
            textView.setText(position+1+". "+question.getQuestion());//列表中显示问题
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v){
                    Intent intent=new Intent(GiveTestActivity.this, QuestionDetailActivity.class);
                    intent.putExtra("question",new Gson().toJson(test.getQuestions().get(position)));
                    startActivity(intent);
                }
            });
            Button buttonDelete=(Button)view.findViewById(R.id.buttonDelete);
            buttonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //    通过AlertDialog.Builder这个类来实例化我们的一个AlertDialog的对象
                    AlertDialog.Builder builder = new AlertDialog.Builder(GiveTestActivity.this);
                    //    设置Title的内容
                    builder.setTitle("确认");
                    //    设置Content来显示一个信息
                    builder.setMessage("确定删除吗？");
                    //    设置一个PositiveButton
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            test.getQuestions().remove(position);//删除问题
                            questionList.setAdapter(adapter);
                            Toast.makeText(GiveTestActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
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
