package com.example.lh.testonline;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
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
        return view;
    }

    public String changeQuestion(String id,String question){
        //String id = (String)getArguments().get("id");
        //String question = (String)getArguments().get("question");

        questionText.setText(id+".\n"+question);
        return editAnswerText.getText().toString();
    }
    public String getAnswer(){return editAnswerText.getText().toString();}

}
