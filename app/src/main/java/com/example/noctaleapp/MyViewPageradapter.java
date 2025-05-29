package com.example.noctaleapp;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.noctaleapp.ui.profile.FanProfileFragment;
import com.example.noctaleapp.ui.profile.ProductProfileFragment;
import com.example.noctaleapp.ui.profile.FollowerProfileFragment;

public class MyViewPageradapter extends FragmentStateAdapter {


    public MyViewPageradapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new ProductProfileFragment();
            case 1:
                return new FanProfileFragment();
            case 2:
                return new FollowerProfileFragment();
            default:
                return new ProductProfileFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
