package com.example.pixabayapi;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    Bitmap[]images;
    Gallery simpleGallery;
    GalleryAdapter galleryAdapter;
    ImageView selectedImageView;
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        simpleGallery = (Gallery) findViewById(R.id.languagesGallery);
        selectedImageView = (ImageView) findViewById(R.id.imageView);
        simpleGallery.setOnItemClickListener((parent, view, position, id) -> {
            selectedImageView.setImageBitmap(images[position]);
        });
    }


    public void getImages(View view) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                EditText editText = findViewById(R.id.textSearch);
                if(!editText.getText().equals("")){
                    try {
                        URL githubEndpoint = new URL("https://pixabay.com/api/?key=29849703-9f3414504f38cae34a4c64380&q="+editText.getText()+"&image_type=photo");
                        HttpsURLConnection myConnection = (HttpsURLConnection) githubEndpoint.openConnection();
                        myConnection.setRequestMethod("GET");
                        BufferedReader reader = new BufferedReader(new InputStreamReader(myConnection.getInputStream()));
                        String inputLine;
                        StringBuilder streamBuilder = new StringBuilder();

                        while ((inputLine = reader.readLine()) != null) {
                            streamBuilder.append(inputLine);
                        }
                        reader.close();
                        JSONObject root = new JSONObject(streamBuilder.toString());
                        JSONArray picture = root.getJSONArray("hits");
                        ArrayList<Bitmap> temp=getBitmapArray(picture);
                        images = temp.toArray(new Bitmap[0]);

                        setGallery();
                        addImgToImgView(images[0]);
                        myConnection.disconnect();
                        } catch (JSONException | IOException ignored) {
                    }
                }
            }
        });
    }
    public ArrayList<Bitmap> getBitmapArray(JSONArray arr) throws IOException, JSONException {
        ArrayList<Bitmap> temp = new ArrayList<>();
        for(int i = 0; i < arr.length(); i ++){
            JSONObject c = arr.getJSONObject(i);
            String largeImageURL = c.getString("largeImageURL");
            URL imgUrl = new URL(largeImageURL);
            Bitmap bitMap = BitmapFactory.decodeStream(imgUrl.openConnection().getInputStream());
            temp.add(bitMap);
        }
        return temp;
    }
    public void addImgToImgView(Bitmap bm){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                selectedImageView.setImageBitmap(bm);
            }
        });
    }
    public void setGallery(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                galleryAdapter = new GalleryAdapter(getApplicationContext(), images);
                simpleGallery.setAdapter(galleryAdapter);
            }
        });
    }
}