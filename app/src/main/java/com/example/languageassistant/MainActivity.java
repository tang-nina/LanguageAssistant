package com.example.languageassistant;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.languageassistant.fragments.GradedFragment;
import com.example.languageassistant.fragments.GradingFragment;
import com.example.languageassistant.fragments.HomeFragment;
import com.example.languageassistant.fragments.ProfileFragment;
import com.example.languageassistant.fragments.RespondFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity  implements HomeFragment.OnItemSelectedListener, RespondFragment.OnItemSelectedListener{
    BottomNavigationView bottomNavigationView;
    final FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;

                switch (item.getItemId()) {
                    case R.id.action_profile:
                        //FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        //ProfileFragment fragmentDemo = ProfileFragment.newInstance(ParseUser.getCurrentUser().getObjectId());
                        //ft.replace(R.id.flContainer, fragmentDemo);
                        //ft.addToBackStack(null);
                        //ft.commit();

                        fragment = new ProfileFragment();
                        fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                        break;

                    case R.id.action_home:
                        fragment = new HomeFragment();
                        fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                        break;
                    case R.id.action_to_grade:
                        fragment = new GradingFragment();
                        fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                        break;
                    case R.id.action_graded:
                        fragment = new GradedFragment();
                        fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                        // do something here
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + item.getItemId());
                }
                return true;
            }
        });

        bottomNavigationView.setSelectedItemId(R.id.action_home); //default tab open

    }

    @Override
    public void onPromptSelected(String prompt) {
      FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
      RespondFragment fragmentRespond = RespondFragment.newInstance(prompt);
      ft.replace(R.id.flContainer, fragmentRespond);
      ft.addToBackStack(null);
      ft.commit();
    }


    @Override
    public void onAnswerSubmitted() {
        HomeFragment fragmentHome = new HomeFragment();
        fragmentManager.beginTransaction().replace(R.id.flContainer, fragmentHome).commit();
    }
}