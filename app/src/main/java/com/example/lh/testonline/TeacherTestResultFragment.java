package com.example.lh.testonline;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by lh on 2018/9/14.
 */

public class TeacherTestResultFragment extends Fragment {
    TextView questionText;
    TextView correctAnswerText;
    TextView studentAnswerStatus;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_teacher_result, container, false);
        questionText = (TextView)view.findViewById(R.id.QuestionText);
        studentAnswerStatus = (TextView)view.findViewById(R.id.textStudentStatus);
        correctAnswerText= (TextView)view.findViewById(R.id.textCorrectAnswer);
        questionText.setMovementMethod(ScrollingMovementMethod.getInstance());
        return view;
    }

    public void changeQuestion(String id,String question,String correctAnswer,String score,String status){
        questionText.setText(id+"."+"("+score+"分)\n"+question);
        studentAnswerStatus.setText(status);
        correctAnswerText.setText(correctAnswer);
    }
}
