//Copyright 2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
//PDX-License-Identifier: MIT-0 (For details, see https://github.com/awsdocs/amazon-rekognition-developer-guide/blob/master/LICENSE-SAMPLECODE.)

package com.example.kaloyan.marvelhelper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.StrictMode;
import android.util.Pair;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClient;
import com.amazonaws.services.rekognition.model.CompareFacesMatch;
import com.amazonaws.services.rekognition.model.CompareFacesRequest;
import com.amazonaws.services.rekognition.model.CompareFacesResult;
import com.amazonaws.services.rekognition.model.Image;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.List;

public class CompareFaces{

    public static Pair<Float, Float> analyzeImage(byte[] data, Context context) {
        Float similarityThreshold = 70F;
        //String targetImage = "target.jpg";
        ByteBuffer sourceImageBytes = null;
        ByteBuffer targetImageBytes=null;


        AWSCredentials credentials = new BasicAWSCredentials("AKIAX5EZK5TBIWWF34PI","Ug0gEcJffDGWS/EwpkGn0tt1TuYQEJzHUTa7tzz9");
        AmazonRekognition rekognitionClient = new AmazonRekognitionClient(credentials);
        // TODO: Regions
        rekognitionClient.setRegion(Region.getRegion(Regions.EU_WEST_2));


//        AWSCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
//                getActivity().getApplicationContext(),
//                "COGNITO_POOL_ID",
//                Regions.EU_WEST_2
//        );
//          AmazonRekognition client = new AmazonRekognitionClient(credentialsProvider);


        //Load source and target images and create input parameter
        //SOURCE
        sourceImageBytes = ByteBuffer.wrap(data);

        // TARGET
        Drawable myIcon = context.getResources().getDrawable( R.drawable.allheroes);
        Bitmap bitmap = ((BitmapDrawable)myIcon).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] bitmapdata = stream.toByteArray();
        targetImageBytes = ByteBuffer.wrap(bitmapdata);

        Image source=new Image()
                .withBytes(sourceImageBytes);
        Image target=new Image()
                .withBytes(targetImageBytes);

        CompareFacesRequest request = new CompareFacesRequest()
                .withSourceImage(source)
                .withTargetImage(target)
                .withSimilarityThreshold(similarityThreshold);


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);


        CompareFacesResult compareFacesResult=rekognitionClient.compareFaces(request);


        // Display results
        List <CompareFacesMatch> faceDetails = compareFacesResult.getFaceMatches();
        if(faceDetails.isEmpty())
            return new Pair<Float, Float>(-1f, -1f);

        return new Pair<Float, Float>(faceDetails.get(0).getSimilarity(), faceDetails.get(0).getFace().getBoundingBox().getLeft() );

        /*
        for (CompareFacesMatch match: faceDetails){
            ComparedFace face= match.getFace();
            BoundingBox position = face.getBoundingBox();

            //System.out.println("Face at " + position.getLeft().toString()
                    + " " + position.getTop()
                    + " matches with " + match.getSimilarity().toString()
                    + "% confidence.");

        }
        List<ComparedFace> uncompared = compareFacesResult.getUnmatchedFaces();

        System.out.println("There was " + uncompared.size()
                + " face(s) that did not match");
        */

    }
}