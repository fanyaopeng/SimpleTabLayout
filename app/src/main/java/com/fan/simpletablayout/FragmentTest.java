package com.fan.simpletablayout;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by f on 2018/5/3.
 */

public class FragmentTest extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        TextView tv = new TextView(getActivity());
        tv.setText("fragment: " + getArguments().getInt("pos"));
        tv.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        tv.setBackgroundColor(Color.RED);
        tv.setGravity(Gravity.CENTER);
        return tv;
    }
}
