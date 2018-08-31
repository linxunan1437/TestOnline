package WebUtil;


import org.apache.http.NameValuePair;

import java.io.IOException;

import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by lh on 2018/6/10.
 */

public class WebConnection  {
    //使用OKhttp来与服务端交互
    public static String doPost(String url,FormBody formBody) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                return response.body().string();
            } else {
                return "fail";
                //throw new IOException("Unexpected code " + response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "fail";
    }

    //以下为原post方法,使用HttpPost,已过时
    /*public static String doPost(String url,List<NameValuePair> list){


        HttpPost post = new HttpPost(url);
        HttpEntity entity = null;
        if(list!=null){
            try{
                entity= new UrlEncodedFormEntity(list,"UTF-8");

            }catch (UnsupportedEncodingException e){
                e.printStackTrace();
            }
            post.setEntity(entity);
        }
        HttpClient client = new DefaultHttpClient();
        try {
            HttpResponse response = client.execute(post);
            if (response.getStatusLine().getStatusCode() == 200) {
                String result = EntityUtils.toString(response.getEntity());
                result=new String(result.getBytes("iso-8859-1"),"gbk");
                return result;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return "connection fail";
    }*/
}
