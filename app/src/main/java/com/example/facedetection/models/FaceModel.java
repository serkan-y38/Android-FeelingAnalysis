package com.example.facedetection.models;

public class FaceModel {

    private float smileProb;
    private float rightEyeOpenProb;
    private float leftEyeOpenProb;

    public FaceModel(float smileProb, float rightEyeOpenProb, float leftEyeOpenProb) {
        this.smileProb = smileProb;
        this.rightEyeOpenProb = rightEyeOpenProb;
        this.leftEyeOpenProb = leftEyeOpenProb;
    }

    public float getSmileProb() {
        return smileProb;
    }

    public void setSmileProb(float smileProb) {
        this.smileProb = smileProb;
    }

    public float getRightEyeOpenProb() {
        return rightEyeOpenProb;
    }

    public void setRightEyeOpenProb(float rightEyeOpenProb) {
        this.rightEyeOpenProb = rightEyeOpenProb;
    }

    public float getLeftEyeOpenProb() {
        return leftEyeOpenProb;
    }

    public void setLeftEyeOpenProb(float leftEyeOpenProb) {
        this.leftEyeOpenProb = leftEyeOpenProb;
    }
}
