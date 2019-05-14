package com.example.kaloyan.marvelhelper;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.amazonaws.services.rekognition.model.BoundingBox;
import com.amazonaws.services.rekognition.model.CompareFacesMatch;
import com.amazonaws.services.rekognition.model.ComparedFace;


public class PictureActivity extends AppCompatActivity {

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preview);
        TextView titleText = findViewById(R.id.textViewTitle);
        TextView descriptionText = findViewById(R.id.textViewDescription);
        String str = new String();
        String description = new String();
        Bitmap bitmap;
        Drawable myIcon = null;
        Button back = (Button) findViewById(R.id.backButton);
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                startActivity(new Intent(PictureActivity.this, TestCameraActivity.class));
            }
        });


        for (CompareFacesMatch match: TestCameraActivity.faceDetails){
            ComparedFace face= match.getFace();
            BoundingBox position = face.getBoundingBox();
            String xPos = position.getLeft().toString();
            if(xPos.equals("0.041505456"))
            {
                str = "Nick Fury";
                description = "A veteran S.H.I.E.L.D. operative, Nick Fury continues the legacy as one of the greatest super spies in the world.";
                myIcon = getResources().getDrawable( R.drawable.nickfurry );
            }
            else if(xPos.equals("0.15615286"))
            {
                str = "Hawk Eye";
                description = "A master marksman and longtime friend of Black Widow, Clint Barton serves as the Avengers’ amazing archer.";
                myIcon = getResources().getDrawable( R.drawable.hawkeye );
            }
            else if(xPos.equals("0.2802096"))
            {
                str = "Hulk";
                description = "Dr. Bruce Banner lives a life caught between the soft-spoken scientist he’s always been and the uncontrollable green monster powered by his rage.";
                myIcon = getResources().getDrawable( R.drawable.hulk );
            }
            else if(xPos.equals("0.43984345"))
            {
                str = "Iron Man";
                description = "Genius. Billionaire. Playboy. Philanthropist. Tony Stark's confidence is only matched by his high-flying abilities as the hero called Iron Man.";
                myIcon = getResources().getDrawable( R.drawable.ironman );
            }
            else if(xPos.equals("0.6117907"))
            {
                str = "Captain America";
                description = "Recipient of the Super-Soldier serum, World War II hero Steve Rogers fights for American ideals as one of the world’s mightiest heroes and the leader of the Avengers";
                myIcon = getResources().getDrawable( R.drawable.a );
            }
            else if(xPos.equals("0.73791736"))
            {
                str = "Thor";
                description = "The son of Odin uses his mighty abilities as the God of Thunder to protect his home Asgard and planet Earth alike.";
                myIcon = getResources().getDrawable( R.drawable.thor );
            }
            else if(xPos.equals("0.8900938"))
            {
                str = "Black Widow";
                description = "Despite super spy Natasha Romanoff’s checkered past, she’s become one of S.H.I.E.L.D.’s most deadly assassins and a frequent member of the Avengers.";
                myIcon = getResources().getDrawable( R.drawable.blackwidow);
            }
            //str += "\n";
//            str += "Face at " + position.getLeft().toString()
//                    + " matches with " + match.getSimilarity().toString()
//                    + "% confidence.\n";

        }

        if(str.equals(""))
            str += "NO MATCH";

        titleText.setText(str);
        descriptionText.setText(description);

        if(myIcon != null) {
            bitmap = ((BitmapDrawable) myIcon).getBitmap();

            ImageView img = findViewById(R.id.imageViewHero);
            img.setImageBitmap(bitmap);
        }
//        imageView = findViewById(R.id.img);
//
//        imageView.setImageBitmap(TestCameraActivity.bitmap);
    }
}