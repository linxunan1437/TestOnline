package com.example.lh.testonline;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by lh on 2018/8/25.
 */

public class MarkingFragment extends Fragment {
    TextView questionText;
    TextView answerText,correctAnswerText;

    EditText scoreMarking;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_marking_test, container, false);
        scoreMarking=(EditText)view.findViewById(R.id.editMarking);
        questionText=(TextView)view.findViewById(R.id.QuestionText);
        answerText=(TextView)view.findViewById(R.id.textAnswer);
        correctAnswerText=(TextView)view.findViewById(R.id.textCorrectAnswer);
        return view;
    }

    public int changeQuestion(String id,String question,String yourAnswer,String correctAnswer,String score){
        questionText.setText(id+".("+score+"分)\n"+question);
        int markscore=0;
        try {
            markscore = Integer.parseInt(scoreMarking.getText().toString());
        }catch (NumberFormatException e){
            return 0;
        }
        int totalmark=Integer.parseInt(score);
        if(totalmark<markscore){return -1;}//超过总分则不记录
        return markscore;
    }


}
