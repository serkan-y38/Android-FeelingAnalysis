package com.example.facedetection.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.facedetection.R;
import com.example.facedetection.helpers.FeelingAnalyzer;
import com.example.facedetection.models.FaceModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BottomDialog extends BottomSheetDialogFragment {
    private ImageView emotionIv;
    private FaceModel faceModel;

    private FeelingAnalyzer feelingAnalyzer = new FeelingAnalyzer();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstancesState) {
        View view = inflater.inflate(R.layout.bottom_dialog, container, false);

        emotionIv = view.findViewById(R.id.feelingIv);

        if (faceModel != null) {
            String emotion = feelingAnalyzer.analyze(faceModel.getLeftEyeOpenProb(), faceModel.getRightEyeOpenProb(), faceModel.getSmileProb());

            if (emotion.equals("sleepy"))
                emotionIv.setBackgroundResource(R.drawable.f_sleeping2);
            else if (emotion.equals("unhappy"))
                emotionIv.setBackgroundResource(R.drawable.feeling_unhappy);
            else if (emotion.equals("happy"))
                emotionIv.setBackgroundResource(R.drawable.feeling_happy);
            else if (emotion.equals("cheerful"))
                emotionIv.setBackgroundResource(R.drawable.feeling_cheerful);
            else
                emotionIv.setBackgroundResource(R.drawable.feeling_laughing);

        }
        return view;
    }

    public void fetchData(FaceModel model) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                faceModel = model;

            }

        });

    }

}
