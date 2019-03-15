package com.example.lh.testonline;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import DataType.Classes;
import WebUtil.ConfigUtil;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import static staticSetting.Setting.socketUrl;

public class ChatActivity extends AppCompatActivity {
    //控件变量
    TextView text;
    EditText texttobeSend;
    Button sendText;
    Classes thisclasses;
    //socket
    WebSocket thissocket;
    //用户信息
    String username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        init();
    }
    @Override
    protected void onStop(){
        super.onStop();
        disconnect();
    }
    private void init() {
        text = (TextView)findViewById(R.id.TextText);
        texttobeSend=(EditText)findViewById(R.id.TextQuestion);
        sendText=(Button)findViewById(R.id.buttonSend);
        username=new ConfigUtil(this).getUser().getUsername();
        text.setMovementMethod(ScrollingMovementMethod.getInstance());
        Intent intent=getIntent();
        thisclasses=new Classes();
        thisclasses.setId(intent.getStringExtra("class"));
        connect();
        sendText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thissocket.send("message:"+texttobeSend.getText().toString());
                texttobeSend.setText("");
            }
        });

    }
    private void connect() {

        MyListener listener = new MyListener();
        Request request = new Request.Builder()
                .url(socketUrl)
                .build();
        OkHttpClient client = new OkHttpClient();
        thissocket=client.newWebSocket(request, listener);

        client.dispatcher().executorService().shutdown();
    }
    private void disconnect(){
        thissocket.close(1000,"appclose");
    }
    public class MyListener extends WebSocketListener {

        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            webSocket.send("name:"+username);
            webSocket.send("class:"+thisclasses.getId());
            thissocket.send("history:");
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            output(text);
        }

        @Override
        public void onMessage(WebSocket webSocket, ByteString bytes) {
            output("onMessage byteString: " + bytes);
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            webSocket.close(1000, null);
            output("onClosing: " + code + "/" + reason);
        }

        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
            output("onClosed: " + code + "/" + reason);
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            output("onFailure: " + t.getMessage());
        }


        private void output(final String content) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    text.setText(text.getText().toString() + content+"\n");
                }
            });
        }
    }
}
