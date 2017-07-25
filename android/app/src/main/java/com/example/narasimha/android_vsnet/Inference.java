package com.example.narasimha.android_vsnet;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOError;
import java.io.IOException;

import cz.msebera.android.httpclient.Header;

public class Inference extends AppCompatActivity {
    TabLayout tl;
    final String Camera_Heading="Camera";
    final String Gallery_Heading="Gallery";
    final String Camera_Body="Uses device native camera to capture images and run inference.";
    final String Gallery_Body="Uses device native gallery to pick images. ";
    final String button_cmera="Capture";
    final String button_gallery="Select";
    final String TENSORFLOW_REQ_URL="http://192.168.2.8:5000/VSNET_domain";
    ImageView img;
    Button b;
    boolean launch_flag=false;
    TextView view_description,view_header;
    ProgressBar inference_bar;
    final int CAMERA_REQ_ID=1;
    final int GALLERY_REQ_ID=2;
    Uri camera_file;
    final String IMAGE_DIR=Environment.DIRECTORY_DCIM;
    ImageClass imageInfo;
    TextView result;
    boolean isCamera=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inference);
        tl=(TabLayout)findViewById(R.id.tabLayout);
        tl.addOnTabSelectedListener(tabSelect);
        b=(Button)findViewById(R.id.button_launcher);
        img=(ImageView)findViewById(R.id.view_icon);
        view_description=(TextView)findViewById(R.id.body);
        view_header=(TextView)findViewById(R.id.head1);
        inference_bar=(ProgressBar)findViewById(R.id.progress);
        inference_bar.setVisibility(View.INVISIBLE);
        createDirectoryOnStart();
        imageInfo=new ImageClass();
        result=(TextView)findViewById(R.id.results);

    }
    private void createDirectoryOnStart(){
        File dir=new File(Environment.getExternalStoragePublicDirectory(IMAGE_DIR),"VSNET_2");
        if(dir.exists())
            return;
        try{
            if(dir.mkdir())
                Toast.makeText(this, "Init success", Toast.LENGTH_SHORT).show();
            else Toast.makeText(this, "Failed to initialise", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(this, "Initialisation failed, you cannout use camera", Toast.LENGTH_SHORT).show();
        }

    }

    TabLayout.OnTabSelectedListener tabSelect=new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            if (tab.getText().equals("Camera")) {
                imageInfo = null;
                imageInfo = new ImageClass();
                loadCameraView();
            } else {
                imageInfo = null;
                imageInfo=new ImageClass();
                loadGalleryView();
            }
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {
            if(tab.getText().equals("Camera")) {
                clearImageInfoAndReset();
                loadCameraView();
            }
            else{
                clearImageInfoAndReset();
                loadGalleryView();
            }
        }
    };
    public void loadCameraView(){
        b.setText(button_cmera);
        view_header.setText(Camera_Heading);
        view_description.setText(Camera_Body);
        img.setImageResource(R.drawable.cam);
        launch_flag=false;
    }
    public void loadGalleryView(){
        b.setText(button_gallery);
        view_header.setText(Gallery_Heading);
        view_description.setText(Gallery_Body);
        img.setImageResource(R.drawable.photos);
        launch_flag=true;
    }
    public void launchAsRequired(View v){
        if(!b.getText().equals("Send>")){
            if(launch_flag){
                gallery_manager();
            }else{
                camera_manager();
            }
        }else
            initialiseAndBeginUplaod(imageInfo);
    }
    public void camera_manager(){
        Intent camera_intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(camera_intent,CAMERA_REQ_ID);
    }
    public void gallery_manager(){
        Intent activity_pick=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(activity_pick,GALLERY_REQ_ID);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if((requestCode==CAMERA_REQ_ID)&&(resultCode==RESULT_OK)){
            Bundle extra=data.getExtras();
            Bitmap thumb=(Bitmap)extra.get("data");
            img.setImageBitmap(thumb);
            b.setText("Send>");
            Uri file=Uri.parse(getUriFromThumb(getApplicationContext(),thumb));
            String path_to_file=getPathFromUri(file);
            imageInfo.setImage_file_type_camera(path_to_file);
        }
        if(requestCode==GALLERY_REQ_ID&&resultCode==RESULT_OK){
            String path_to_image=getPathFromUri(data.getData());
            Bitmap bm= BitmapFactory.decodeFile(path_to_image);
            img.setImageBitmap(bm);
            b.setText("Send>");
            imageInfo.setImage_file_type_gallery(path_to_image);
        }
    }
    private String getPathFromUri(Uri selectedImage){
        String[] filePathColumn = {MediaStore.Images.Media.DATA};

        Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String filePath = cursor.getString(columnIndex);
        cursor.close();
        return filePath;
    }

    public void initialiseAndBeginUplaod(ImageClass imageInfo){
        //Check the type of file:
        String path;
        if(imageInfo.is_type_camera)
            path=imageInfo.getImage_file_type_camera();
        else
            path=imageInfo.getImage_file_type_gallery();
        uploadAsyncTask(path);
    }
    private void clearImageInfoAndReset(){
        imageInfo=null;
        imageInfo=new ImageClass();
    }
    private void uploadAsyncTask(String filename){
        inference_bar.setVisibility(View.VISIBLE);
        RequestParams parms=new RequestParams();
        try{
            parms.put("file", new File(filename));
            AsyncHttpClient http_client=new AsyncHttpClient();
            http_client.post(TENSORFLOW_REQ_URL, parms, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    try {
                        inference_bar.setVisibility(View.INVISIBLE);
                        String content=new String(responseBody,"UTF-8");
                        result.setText(content);
                    }catch (Exception e){
                        Toast.makeText(Inference.this, "No JSON found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Toast.makeText(Inference.this, "Error getting JSONObject", Toast.LENGTH_SHORT).show();
                    inference_bar.setVisibility(View.INVISIBLE);
                }
            });
        }catch (Exception e){
            Toast.makeText(this, "Error starting HttpTask", Toast.LENGTH_SHORT).show();
        }
        clearImageInfoAndReset();
    }

    private String getUriFromThumb(Context context, Bitmap image_data){
        ByteArrayOutputStream bytes=new ByteArrayOutputStream();
        image_data.compress(Bitmap.CompressFormat.JPEG,100,bytes);
        String path= MediaStore.Images.Media.insertImage(context.getContentResolver(), image_data, "image",null);
        return path;
    }
}
