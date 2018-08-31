package WebUtil;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import DataType.User;

/**
 * Created by lh on 2018/6/5.
 */

public class ConfigUtil {
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    String userJson;
    public static final String USER_KEY = "user_key";
    public String getUserJson(){
        return sp.getString(USER_KEY,"");
    }

    public User getUser(){
        //获得本用户的信息.
        String s = this.getUserJson();
        User temp=new Gson().fromJson(s,User.class);
        return temp;
    }

    public void setUserJson(String userJson){
        editor.putString(USER_KEY,userJson);
        editor.commit();
    }

    public ConfigUtil(Context ctx){
        sp=ctx.getSharedPreferences("my_sp",Context.MODE_PRIVATE);
        editor = sp.edit();
    }
}
