package com.example.lh.testonline;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import DataType.Classes;
import DataType.Question;
import WebUtil.ConfigUtil;
import WebUtil.WebConnection;
import okhttp3.FormBody;

import static staticSetting.Setting.projectName;
import static staticSetting.Setting.serverUrl;

public class SearchQuestionActivity extends AppCompatActivity {
    EditText textSearch;
    Button search,quitQuestion;
    MutipleChoiceFragment singleChoose;
    ListView questionlist;

    SearchQuestionActivity.ClassesAdapter classesAdapter;

    List<Question> qlist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_question);
        init();
    }
    private void init(){
        search=(Button)findViewById(R.id.buttonSearch);
        questionlist=(ListView)findViewById(R.id.questionList);
        quitQuestion=(Button)findViewById(R.id.buttonReturn);
        textSearch=(EditText)findViewById(R.id.editText);
        quitQuestion.setVisibility(View.INVISIBLE);
        String url = serverUrl+"/"+projectName+"/CreateQuestionServlet";
        new SearchQuestionActivity.myTask().execute(url,"search");
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = serverUrl+"/"+projectName+"/CreateQuestionServlet";
                new SearchQuestionActivity.myTask().execute(url,"search");
            }
        });
        quitQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //关掉这个问题
                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.remove(singleChoose);
                transaction.commit();
            }
        });
    }
    public String addQuestion(String url){
        String success= "";
        String key=textSearch.getText().toString();
        FormBody formBody = new FormBody.Builder()
                .add("modifyType", "class")
                .add("operation","add")
                .add("uid", String.valueOf(new ConfigUtil(this).getUser().getUid()))
                .build();
        success= WebConnection.doPost(url,formBody);
        return success;
    }
    public String deleteQuestion(String url,String ques){
        String success= "";
        String key=textSearch.getText().toString();
        FormBody formBody = new FormBody.Builder()
                .add("operation","deleteQuestion")
                .add("question",ques )
                .build();
        success= WebConnection.doPost(url,formBody);
        return success;
    }
    public String searchQuestion(String url){
        String success= "";
        String key=textSearch.getText().toString();
        FormBody formBody = new FormBody.Builder()
                .add("key", key)
                .add("operation","searchQuestion")
                .build();
        success= WebConnection.doPost(url,formBody);
        return success;

    }
    public String initQuestion(String url){
        String success= "";
        String key=textSearch.getText().toString();
        FormBody formBody = new FormBody.Builder()
                .add("key", key)
                .add("operation","initQuestion")
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
                return searchQuestion(url);
            if(operation.equals("add"))
                return addQuestion(url);
            if(operation.equals("delete"))
                return deleteQuestion(url,params[2]);
            return result;
        }

        protected void onPostExecute(String result){
            super.onPostExecute(result);
            //将搜索得到的问题信息放到容器中
            if(result.equals("fail")){
                Toast.makeText(SearchQuestionActivity.this, "失败", Toast.LENGTH_SHORT).show();return;
            }
            if(result.equals("null")){Toast.makeText(SearchQuestionActivity.this, "没有相应结果", Toast.LENGTH_SHORT).show();return;}
            if(result.equals("delete success")){Toast.makeText(SearchQuestionActivity.this, "删除成功", Toast.LENGTH_SHORT).show();return;}
            Gson gson= new Gson();
            qlist=gson.fromJson(result,new TypeToken<List<Question>>(){}.getType());
            //gui设定
            classesAdapter= new SearchQuestionActivity.ClassesAdapter(SearchQuestionActivity.this,R.layout.questionlistsearch,qlist);
            questionlist.setAdapter(classesAdapter);
            questionlist.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                Intent intent;
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    singleChoose= new MutipleChoiceFragment();
                    singleChoose.changeQuestion(qlist.get(position).getId(),qlist.get(position).getQuestion(),qlist.get(position).getScore(),qlist.get(position).getAnswer());
                    FragmentManager manager = getSupportFragmentManager();
                    FragmentTransaction transaction = manager.beginTransaction();
                    transaction.add(R.id.fragment_container, singleChoose);
                    transaction.commit();
                    quitQuestion.setVisibility(View.VISIBLE);
                }
            });
        }
    }




    public class ClassesAdapter extends ArrayAdapter<Question> {
        private int resourceId;
        public ClassesAdapter(Context context, int textViewId, List<Question> list){
            super(context,textViewId,list);
            resourceId=textViewId;
        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent){
            final Question question= getItem(position);
            View view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
            TextView textView= (TextView)view.findViewById(R.id.textClassSearchResult);
            textView.setText(question.getQuestion());
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v){
                    Intent intent=new Intent(SearchQuestionActivity.this, QuestionDetailActivity.class);
                    intent.putExtra("question",new Gson().toJson(qlist.get(position)));
                    startActivity(intent);
                }
            });
            Button buttonAdd=(Button)view.findViewById(R.id.buttonAdd);
            Button buttonDelete=(Button)view.findViewById(R.id.buttonDelete);
            buttonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v){
                    final AlertDialog.Builder builder = new AlertDialog.Builder(SearchQuestionActivity.this);
                    final View dialogView = LayoutInflater.from(SearchQuestionActivity.this)
                            .inflate(R.layout.dialog_input,null);
                    //    设置Title的内容
                    builder.setTitle("确认");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {

                            String url = serverUrl+"/"+projectName+"/CreateQuestionServlet";
                            new SearchQuestionActivity.myTask().execute(url,"delete",new Gson().toJson(qlist.get(position)));
                            qlist.remove(position);
                            questionlist.setAdapter(classesAdapter);
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
            buttonAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //    通过AlertDialog.Builder这个类来实例化我们的一个AlertDialog的对象
                    final AlertDialog.Builder builder = new AlertDialog.Builder(SearchQuestionActivity.this);
                    final View dialogView = LayoutInflater.from(SearchQuestionActivity.this)
                            .inflate(R.layout.dialog_input,null);
                    //    设置Title的内容
                    builder.setTitle("确认");
                    builder.setView(dialogView);
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            EditText edit_text =
                                    (EditText) dialogView.findViewById(R.id.editTextScore);
                            try {
                                qlist.get(position).setScore(Integer.parseInt(edit_text.getText().toString()));
                            }catch(Exception e){
                                Toast.makeText(SearchQuestionActivity.this, "分数设置不正确", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            Intent intent = new Intent();
                            intent.putExtra("question",new Gson().toJson(qlist.get(position)));
                            setResult(1,intent);

                            finish();
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
