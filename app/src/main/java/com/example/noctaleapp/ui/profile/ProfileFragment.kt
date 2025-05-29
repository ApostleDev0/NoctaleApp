package com.example.noctaleapp.ui.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.noctaleapp.R


class ProfileFragment : Fragment() {

    TabLayout profileTabLayout;
    ViewPager profileViewPager;
    ProfileAdapter profileAdapter;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_profile);

//        Tablayout = findViewById(R.id.profileTabLayout);
//        ViewPager = findViewById(R.id.profileViewPager);
//        profileAdapter = new MyViewPagerAdapter(fragmentActivity;this);
   }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }
}