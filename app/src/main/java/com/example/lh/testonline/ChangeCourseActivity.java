package com.example.lh.testonline;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import DataType.Classes;
import WebUtil.ConfigUtil;
import WebUtil.WebConnection;
import okhttp3.FormBody;

import static staticSetting.Setting.projectName;
import static staticSetting.Setting.serverUrl;
/*
    本界面用于用户删除自己的课程
    同时也用于选择班级


 */
public class ChangeCourseActivity extends AppCompatActivity {
    Button deleteCourse;
    ListView classListView;
    List<Classes> classeslist;
    String opentype;
    ChangeCourseActivity.ClassesAdapter classesAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_course);
        init();
    }
    private void init(){
        //初始化界面元素
        classListView=(ListView)findViewById(R.id.ListMyClass);
        Intent intent=getIntent();
        opentype=intent.getStringExtra("opentype");

        //搜索并填充当前用户的课程
        String url = serverUrl + "/" + projectName + "/ModifyServlet";
        new ChangeCourseActivity.myTask().execute(url,"search");

    }

    //搜索课程
    public String searchClass(String url){
        String success= "";
        String key=String.valueOf(new ConfigUtil(this).getUser().getUid());
        FormBody formBody = new FormBody.Builder()
                .add("modifyType", "SearchUserClass")
                .add("key", key)
                .build();
        success= WebConnection.doPost(url,formBody);
        return success;
    }
    //删除课程
    public String deleteClass(String url,String classid){
        String success= "";
        String uid=String.valueOf(new ConfigUtil(this).getUser().getUid());

        FormBody formBody = new FormBody.Builder()
                .add("modifyType", "createClass")
                .add("operation","delete")
                .add("classes",classid)
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
                return searchClass(url);
            if(operation.equals("delete"))
                return deleteClass(url,params[2]);
            return result;
        }
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            //将搜索得到的班级信息放到容器中
            if(result.equals("fail")){
                Toast.makeText(ChangeCourseActivity.this, "搜索失败", Toast.LENGTH_SHORT).show();return;}
            if(result.equals("null")){Toast.makeText(ChangeCourseActivity.this, "没有相应结果", Toast.LENGTH_SHORT).show();return;}
            if(result.equals("database fail")){Toast.makeText(ChangeCourseActivity.this, "数据库错误", Toast.LENGTH_SHORT).show();return;}
            if(result.equals("delete success")){
                Toast.makeText(ChangeCourseActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                return;
            }
            //搜索成功的处理
            Gson gson= new Gson();
            classeslist=gson.fromJson(result,new TypeToken<List<Classes>>(){}.getType());
            //gui设定
             classesAdapter= new ChangeCourseActivity.ClassesAdapter(ChangeCourseActivity.this,R.layout.classlist,classeslist);
            classListView.setAdapter(classesAdapter);
            classListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                Intent intent;
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    //选班
                    if(opentype.equals("selectChat")){
                        Intent intent1=new Intent(ChangeCourseActivity.this,ChatActivity.class);
                        intent1.putExtra("class",classeslist.get(position).getId());
                        startActivity(intent1);
                    }
                    if(opentype.equals("selectClass")){
                        Intent intent1=new Intent();
                        intent1.putExtra("class",new Gson().toJson(classeslist.get(position)));
                        setResult(3,intent1);
                        finish();
                    }
                }
            });
        }
    }

    public class ClassesAdapter extends ArrayAdapter<Classes> {
        private int resourceId;
        public ClassesAdapter(Context context, int textViewId, List<Classes> list){
            super(context,textViewId,list);
            resourceId=textViewId;
        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent){
            final Classes classes= getItem(position);
            View view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
            TextView textView= (TextView)view.findViewById(R.id.textClassSearchResult);
            textView.setText(classes.toString());
            Button buttonAddClass=(Button)view.findViewById(R.id.buttonAddClass);
            if(!opentype.equals("manage")){
                buttonAddClass.setVisibility(View.INVISIBLE);//选择班级时不显示删除按钮
            }
            buttonAddClass.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                            //    通过AlertDialog.Builder这个类来实例化我们的一个AlertDialog的对象
                            AlertDialog.Builder builder = new AlertDialog.Builder(ChangeCourseActivity.this);
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
                                    String url = serverUrl+"/"+projectName+"/ModifyServlet";
                                    new ChangeCourseActivity.myTask().execute(url,"delete",new Gson().toJson(classes));
                                    classeslist.remove(position);
                                    classListView.setAdapter(classesAdapter);
                                    Toast.makeText(ChangeCourseActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
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
