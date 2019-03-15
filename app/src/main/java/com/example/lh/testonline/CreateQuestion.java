package com.example.lh.testonline;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import DataType.Question;
import WebUtil.WebConnection;
import okhttp3.FormBody;

import static staticSetting.Setting.projectName;
import static staticSetting.Setting.serverUrl;

public class CreateQuestion extends AppCompatActivity {
    //控件
    Button submit;
    EditText editQuestion,editAnswer,editScore;
    //打开方式
    String opentype;
    //问题存储
    Question question;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_question);
        init();
    }

    private void init(){

        //获得打开方式
        Intent intent=getIntent();
        opentype=intent.getStringExtra("type");
        //初始化数据
        question=new Question();
        //初始化控件
        submit=(Button)findViewById(R.id.buttonSubmit);
        editAnswer=(EditText)findViewById(R.id.editTextAnswer);
        editQuestion=(EditText)findViewById(R.id.editTextQuestion);
        editScore=(EditText)findViewById(R.id.editScore);
        if(opentype.equals("edit")){
            //若为编辑模式则直接填入,且上传时参数不同

        }
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    question.setQuestion(editQuestion.getText().toString());
                    question.setAnswer(editAnswer.getText().toString());
                    question.setScore(Integer.parseInt(editScore.getText().toString()));
                }catch (Exception e){
                    Toast.makeText(CreateQuestion.this, "问题或答案或分数输入不正确", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(opentype.equals("edit")){
                    //编辑模式下直接返回问题.

                    String url = serverUrl + "/" + projectName + "/CreateQuestionServlet";
                    new CreateQuestion.myTask().execute(url);

                }

            }
        });
    }
    public String submitQuestion(String url){
        String success= "";
        FormBody formBody = new FormBody.Builder()
                .add("question", new Gson().toJson(question))
                .add("operation","question")
                .build();
        success= WebConnection.doPost(url,formBody);
        return success;
    }

    class myTask extends AsyncTask<String,Integer,String> {
        @Override
        protected String doInBackground(String... params){
            //后台运行
            String url = params[0];
            String result=submitQuestion(url);
            return result;
        }
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            Intent intent = new Intent();
            if(result.equals("fail")){Toast.makeText(CreateQuestion.this, "创建失败", Toast.LENGTH_SHORT).show();return;}
            question.setId(result);
            intent.putExtra("question",new Gson().toJson(question));
            setResult(1,intent);
            finish();
        }
    }
}
