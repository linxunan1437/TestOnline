package com.example.lh.testonline;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;


public class MutipleChoiceFragment extends Fragment {
    TextView questionText;
    EditText editAnswerText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mutiple_choice, container, false);
        editAnswerText=(EditText) view.findViewById(R.id.editAnswerText);
        questionText = (TextView)view.findViewById(R.id.questionText);
        questionText.setMovementMethod(ScrollingMovementMethod.getInstance());
        return view;
    }

    public String changeQuestion(String id,String question,int score,String nextanswer){
        //String id = (String)getArguments().get("id");
        //String question = (String)getArguments().get("question");
        questionText.setText(id+".("+score+"分)\n"+question);
        String answer=editAnswerText.getText().toString();
        editAnswerText.setText(nextanswer);
        return answer;
    }
    public String getAnswer(){return editAnswerText.getText().toString();}
    public void seteditable(boolean e){
        editAnswerText.setEnabled(e);
    }
}
