package com.example.kaloyan.marvelhelper;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClient;
import com.amazonaws.services.rekognition.model.BoundingBox;
import com.amazonaws.services.rekognition.model.CompareFacesMatch;
import com.amazonaws.services.rekognition.model.CompareFacesRequest;
import com.amazonaws.services.rekognition.model.CompareFacesResult;
import com.amazonaws.services.rekognition.model.ComparedFace;
import com.amazonaws.services.rekognition.model.Image;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.List;

public class TestCameraActivity extends AppCompatActivity {

    private Camera mCamera;
    private CameraPreview mPreview;
    private Camera.PictureCallback mPicture;
    private Button capture;
    private Context myContext;
    private LinearLayout cameraPreview;
    public static Bitmap bitmap;

    public static AWSAppSyncClient mAWSAppSyncClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        myContext = this;

        mCamera =  Camera.open();
        mCamera.setDisplayOrientation(90);
        cameraPreview = (LinearLayout) findViewById(R.id.cPreview);
        mPreview = new CameraPreview(myContext, mCamera);
        cameraPreview.addView(mPreview);

        capture = (Button) findViewById(R.id.btnSwitch);
        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });
        mCamera.startPreview();
        Camera.Parameters params = mCamera.getParameters();
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        mCamera.setParameters(params);

    }

    public void onResume() {

        super.onResume();
        if(mCamera == null) {
            mCamera = Camera.open();
            mCamera.setDisplayOrientation(180);
            mPicture = getPictureCallback();
            mPreview.refreshCamera(mCamera);
            Log.d("nu", "null");
        }else {
            Log.d("nu","no null");
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        //when on Pause, release camera in order to be used from other applications
        releaseCamera();
    }

    private void releaseCamera() {
        // stop and release camera
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

    public static List<CompareFacesMatch> faceDetails;
    public static List<ComparedFace> uncompared;

    private void takePhoto() {
        Camera.PictureCallback pictureCB = new Camera.PictureCallback() {
            public void onPictureTaken(byte[] data, Camera cam) {


                setContentView(R.layout.loading_activity);

                Float similarityThreshold = 70F;
                String sourceImage = "source.jpg";
                String targetImage = "target.jpg";
                ByteBuffer sourceImageBytes=null;
                ByteBuffer targetImageBytes=null;


                AWSCredentials credentials = new BasicAWSCredentials(System.getenv("AWS_Access_Key"),System.getenv("AWS_Secret_Key"));
                AmazonRekognition rekognitionClient = new AmazonRekognitionClient(credentials);
                // TODO: Regions
                rekognitionClient.setRegion(Region.getRegion(Regions.AP_SOUTHEAST_2));

                //Load source and target images and create input parameters
                Drawable myIcon = getResources().getDrawable( R.drawable.a );
                Bitmap bitmap = ((BitmapDrawable)myIcon).getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] bitmapdata = stream.toByteArray();
                sourceImageBytes = ByteBuffer.wrap(data);

                myIcon = getResources().getDrawable( R.drawable.theavengers);
                bitmap = ((BitmapDrawable)myIcon).getBitmap();
                stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                bitmapdata = stream.toByteArray();
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
                faceDetails = compareFacesResult.getFaceMatches();
                //
                for (CompareFacesMatch match: faceDetails){
                    ComparedFace face= match.getFace();
                    BoundingBox position = face.getBoundingBox();
                    Log.d("fuckshit","Face at " + position.getLeft().toString()
                            + " " + position.getTop()
                            + " matches with " + match.getSimilarity().toString()
                            + "% confidence.");

                }
                uncompared = compareFacesResult.getUnmatchedFaces();

                Log.d("fuckshit","There was " + uncompared.size()
                        + " face(s) that did not match");

                bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                Intent intent = new Intent(myContext, PictureActivity.class);
                startActivity(intent);

            }
        };
        mCamera.takePicture(null, null, pictureCB);
    }
    private Camera.PictureCallback getPictureCallback() {
        Camera.PictureCallback picture = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                Intent intent = new Intent(myContext, PictureActivity.class);
                startActivity(intent);
            }
        };
        return picture;
    }
}
