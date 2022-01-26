package com.example.sdaassign4_2021;


import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * @author Rafael Izarra, 2022
 * create an instance of this fragment.
 */
public class Welcome extends Fragment {


    public Welcome() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_welcome, container, false);

        // Initiate the title of the page
        TextView textView = (TextView) root.findViewById(R.id.welcomeText);
        String text = "All Rights reserved by DCU";

        // add a span color and underline to the "DCU's" word target
        SpannableString spannableString = new SpannableString(text);
        ForegroundColorSpan foregroundColorSpanCustom = new ForegroundColorSpan(Color.rgb(204, 174, 98));
        UnderlineSpan underlineSpan = new UnderlineSpan();
        spannableString.setSpan(foregroundColorSpanCustom, 23, 26, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(underlineSpan, 23, 26, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        textView.setText(spannableString);


        return root;
    }

}
