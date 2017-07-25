package com.example.narasimha.android_vsnet;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    final String INCEPTION_URL="http://download.tensorflow.org/models/image/imagenet/inception-2015-12-05.tgz";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ConnectivityManager cn=(ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo ni=cn.getActiveNetworkInfo();
        if(ni==null && !(ni.isConnectedOrConnecting()))
            Toast.makeText(this, "No internet connection, Connect to the internet to run inference", Toast.LENGTH_SHORT).show();
    }

    public void launchBrainClient(View v){
        startActivity(new Intent(this,Inference.class));
    }
    public void loadTour(View v){
        startActivity(new Intent(this,Tour.class));
    }
    //MenuBar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_favorite:
                startActivity(new Intent(this,Developers.class));
                return true;
            default: return true;
        }
    }
}
