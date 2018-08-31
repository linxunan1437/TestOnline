package com.example.lh.testonline;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by lh on 2018/8/25.
 */

public class ResultFragment extends Fragment {
    TextView questionText;
    TextView yourAnswerText,correctAnswerText,scoreText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test_result, container, false);
        questionText = (TextView)view.findViewById(R.id.resultQuestionText);
        yourAnswerText = (TextView)view.findViewById(R.id.textYourAnswer);
        correctAnswerText= (TextView)view.findViewById(R.id.textCorrectAnswer);
        scoreText=(TextView)view.findViewById(R.id.textScore);
        return view;
    }

    public void changeQuestion(String id,String question,String yourAnswer,String correctAnswer,String score){
        questionText.setText(id+".\n"+question);
        yourAnswerText.setText(yourAnswer);
        correctAnswerText.setText(correctAnswer);
        scoreText.setText(score);
    }
}
