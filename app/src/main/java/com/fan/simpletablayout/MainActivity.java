package com.fan.simpletablayout;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.fan.library.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    List<FragmentTest> fragmentTests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TabLayout tab = findViewById(R.id.tab);
        ViewPager vp = findViewById(R.id.vp);
        fragmentTests = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            fragmentTests.add(new FragmentTest());
        }
        vp.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                FragmentTest item = fragmentTests.get(position);
                Bundle bundle = new Bundle();
                bundle.putInt("pos", position);
                item.setArguments(bundle);
                return item;
            }

            @Override
            public int getCount() {
                return fragmentTests.size();
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                if (position > 10)
                    return "长文本长文本" + position;
                return "测试" + position;
            }
        });
        tab.setupWithViewPager(vp);
    }
}
