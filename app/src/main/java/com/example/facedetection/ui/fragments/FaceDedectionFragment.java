package com.example.facedetection.ui.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.facedetection.R;
import com.example.facedetection.databinding.FragmentFaceDedectionBinding;
import com.example.facedetection.models.FaceModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class FaceDedectionFragment extends Fragment {

    private FragmentFaceDedectionBinding fragmentFaceDedectionBinding;
    private ListenableFuture listenableFuture;
    private ExecutorService executorService;
    private PreviewView previewView;
    private ImageAnalyzer imageAnalyzer;

    private ViewPager viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentFaceDedectionBinding = FragmentFaceDedectionBinding.inflate(inflater, container, false);
        return fragmentFaceDedectionBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new AsyncTasks().execute();
    }

    private class AsyncTasks extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            viewPager = getActivity().findViewById(R.id.viewPager);
            previewView = fragmentFaceDedectionBinding.previewView;
            imageAnalyzer = new ImageAnalyzer(getActivity().getSupportFragmentManager());

        }

        @Override
        protected Void doInBackground(Void... voids) {
            action();
            return null;
        }

    }

    private void action() {

        fragmentFaceDedectionBinding.infoTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentFaceDedectionBinding.infoTv.setVisibility(View.GONE);
                setUpCamera();

            }
        });

        fragmentFaceDedectionBinding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentFaceDedectionBinding.infoTv.setVisibility(View.VISIBLE);
                executorService.shutdown();
                viewPager.setCurrentItem(0);

            }
        });

    }

    private void setUpCamera() {

        executorService = Executors.newSingleThreadExecutor();
        listenableFuture = ProcessCameraProvider.getInstance(requireContext());

        listenableFuture.addListener(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                try {
                    if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA) != (PackageManager.PERMISSION_GRANTED)) {
                        requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);

                    } else {
                        ProcessCameraProvider processCameraProvider = (ProcessCameraProvider) listenableFuture.get();
                        bindPreview(processCameraProvider);

                    }
                } catch (ExecutionException | InterruptedException e) {
                    Log.e("error", e.getMessage());

                }

            }

        }, ContextCompat.getMainExecutor(requireContext()));

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            ProcessCameraProvider processCameraProvider = null;

            try {
                processCameraProvider = (ProcessCameraProvider) listenableFuture.get();

            } catch (InterruptedException | ExecutionException e) {
                Log.e("error", e.getMessage());

            }
            bindPreview(processCameraProvider);
        }

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void bindPreview(ProcessCameraProvider processCameraProvider) {

        Preview preview = new Preview.Builder().build();
        CameraSelector cameraSelector = new CameraSelector
                .Builder()
                .requireLensFacing(
                        CameraSelector.LENS_FACING_FRONT
                )
                .build();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());
        ImageCapture imageCapture = new ImageCapture.Builder().build();

        ImageAnalysis imageAnalysis = new ImageAnalysis
                .Builder()
                .setTargetResolution(new Size(1280, 720))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        imageAnalysis.setAnalyzer(executorService, imageAnalyzer);
        processCameraProvider.unbindAll();
        processCameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture, imageAnalysis);

    }

    public class ImageAnalyzer implements ImageAnalysis.Analyzer {

        private FragmentManager fragmentManager;
        private BottomDialog dialog;

        public ImageAnalyzer(FragmentManager fragmentManager) {
            this.fragmentManager = fragmentManager;
            dialog = new BottomDialog();
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void analyze(@NonNull ImageProxy image) {
            scan(image);
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        private void scan(ImageProxy image) {

            @SuppressLint("UnsafeOptInUsageError")
            Image image1 = image.getImage();
            assert image1 != null;

            InputImage inputImage = InputImage.fromMediaImage(image1, image.getImageInfo().getRotationDegrees());

            FaceDetectorOptions highAccuracyOpts =
                    new FaceDetectorOptions.Builder()
                            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                            .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
                            .build();

            FaceDetector detector = FaceDetection.getClient(highAccuracyOpts);

            Task<List<Face>> result = detector.process(inputImage)
                    .addOnSuccessListener(
                            new OnSuccessListener<List<Face>>() {
                                @Override
                                public void onSuccess(List<Face> faces) {
                                    scanFace(faces);

                                }
                            })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(), "Oops, something went wrong. Try again.", Toast.LENGTH_SHORT).show();

                                }
                            }).addOnCompleteListener(new OnCompleteListener<List<Face>>() {
                        @Override
                        public void onComplete(@NonNull Task<List<Face>> task) {
                            image.close();

                        }
                    });

        }

        private void scanFace(List<Face> faces) {

            for (Face face : faces) {

                if (!dialog.isAdded()) {

                    dialog.show(fragmentManager, "dialog");
                    dialog.fetchData(new FaceModel(face.getSmilingProbability(), face.getRightEyeOpenProbability(), face.getLeftEyeOpenProbability()));

                    fragmentFaceDedectionBinding.infoTv.setVisibility(View.VISIBLE);
                    executorService.shutdown();

                }

            }

        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        fragmentFaceDedectionBinding = null;
    }

}