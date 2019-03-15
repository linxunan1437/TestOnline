package com.example.lh.testonline;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

import DataType.Question;

public class QuestionDetailActivity extends AppCompatActivity {
    TextView question,answer,score;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_detail);
        init();
    }
    private void init(){
        Intent intent=getIntent();
        Question q=new Gson().fromJson(intent.getStringExtra("question"),Question.class);
        answer=(TextView)findViewById(R.id.textViewAnswer);
        question=(TextView)findViewById(R.id.textViewQuestion);
        button=(Button)findViewById(R.id.buttonReturn);
        score=(TextView)findViewById(R.id.textViewScore);
        question.setText(q.getQuestion());
        answer.setText(q.getAnswer());
        score.setText(String.valueOf(q.getScore()));
        question.setMovementMethod(ScrollingMovementMethod.getInstance());
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
