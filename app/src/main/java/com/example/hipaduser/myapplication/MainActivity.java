package com.example.hipaduser.myapplication;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.MultipartBody.Builder;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private VideoView mVideo;
    private Button mUpload;
    private Button mPlay;
    private String mSite;
    private final static String TAG = "bluedai";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("http://lemonade2017.pythonanywhere.com/index").build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String videoURL = response.body().string();
                try {
                    JSONObject jsonObject = new JSONObject(videoURL);
                    JSONArray array = jsonObject.getJSONArray("tasks");
                    JSONObject object = array.getJSONObject(0);
                    mSite = object.getString("description");
//                    for (int i = 0; i < array.length(); i++) {
//                        JSONObject object = array.getJSONObject(i);
//                        Log.d("bluedai", object.getString("title"));
//                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        mVideo = (VideoView) findViewById(R.id.video);
        mVideo.setMediaController(new MediaController(this));
        mUpload = (Button) findViewById(R.id.upload);
        mPlay = (Button) findViewById(R.id.play);
        mPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(mSite);

                mVideo.setVideoURI(uri);
                mVideo.start();
            }
        });
        mUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File("/storage/emulated/0/DCIM/Camera/CameraPicture/IMG_20170221_235139.jpg");
                RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
                MultipartBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("file", null, new MultipartBody.Builder()
                                .addPart(Headers.of(
                                        "Content-Disposition", "form-data; filename=\"photo.jpg\""),
                                        RequestBody.create(MediaType.parse("image"),
                                                file))
                                .build())
                        .build();
                Request request = new Request.Builder()
                        .url("http://lemonade2017.pythonanywhere.com/file")
                        .post(requestBody)
                        .build();
                Call call = client.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d(TAG, "onFailure: " + e.toString());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            Log.d(TAG, "onResponse: ");
                        }
                    }
                });
            }

        });



    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }
}
