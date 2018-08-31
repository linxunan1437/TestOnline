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

public class MarkingFragment extends Fragment {
    TextView questionText;
    TextView AnswerText,scoreText;
    EditText yourScore;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_marking_test, container, false);
        scoreText=(TextView)view.findViewById(R.id.textScore);
        return view;
    }

    public int changeQuestion(String id,String question,String yourAnswer,String correctAnswer,String score){
        questionText.setText(id+".\n"+question);
        scoreText.setText(score);
        int markscore=Integer.parseInt(yourScore.toString());
        return markscore;
    }
}
