package com.example.facedetection.ui.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.example.facedetection.databinding.FragmentViewPageBinding;
import com.example.facedetection.ui.adapters.ViewPagerAdapter;


public class ViewPageFragment extends Fragment {

    private ViewPagerAdapter viewPagerAdapter;
    private FragmentViewPageBinding fragmentViewPageBinding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentViewPageBinding = FragmentViewPageBinding.inflate(inflater, container, false);

        return fragmentViewPageBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setViewPager();

    }

    private void setViewPager() {

        viewPagerAdapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        viewPagerAdapter.addFragment(new HomeFragment(), "Home");
        viewPagerAdapter.addFragment(new FaceDedectionFragment(), "Dedection");

        fragmentViewPageBinding.viewPager.setAdapter(viewPagerAdapter);

    }

}