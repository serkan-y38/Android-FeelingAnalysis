package com.example.facedetection.helpers;

public class FeelingAnalyzer {

    public String analyze(float leftEyeOpenProb, float rightEyeOpenProb, float smileProb) {

        String feeling = "";

        if ((rightEyeOpenProb <= 0.02 || leftEyeOpenProb <= 0.02) && smileProb < 0.1)
            feeling = "sleepy";
        else if (smileProb <= 0.01)
            feeling = "unhappy";
        else if (smileProb <= 0.2)
            feeling = "happy";
        else if (smileProb <= 0.7)
            feeling = "cheerful";
        else
            feeling = "laughing";

        return feeling;
    }

}
