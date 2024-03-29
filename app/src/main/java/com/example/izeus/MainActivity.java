package com.example.izeus;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ListView listView;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.listView);
        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
//                        Toast.makeText(MainActivity.this,"Run Time Permission Given!",Toast.LENGTH_SHORT).show();
                        ArrayList<File> mySongs = fetchSongs(Environment.getExternalStorageDirectory());
                        String [] items = new String[mySongs.size()];// Brings the song names.
                        for(int i=0;i<mySongs.size();i++){
                            items[i] = mySongs.get(i).getName().replace(".mp3","");//replaces the .mp3 extension
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_expandable_list_item_1,items);
                        listView.setAdapter(adapter);

                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent intent = new Intent(MainActivity.this,PlaySong.class);
                                String currentSong = listView.getItemAtPosition(position).toString();
                                intent.putExtra("songList",mySongs);
                                intent.putExtra("currentSong",currentSong);
                                intent.putExtra("position",position);
                                startActivity(intent);
                            }
                        });

                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        // Requests permission again from the user.Asks permission during runtime.
                        permissionToken.continuePermissionRequest();
                    }
                })
                .check();
    }
    // we give an URL (directory) and all songs from that directory is fetched.
    // Below function returns all the mp3 files
    public ArrayList<File> fetchSongs(File file){
        ArrayList arrayList = new ArrayList();
        File [] songs = file.listFiles();
        // Recursively add all the songs from the files to the arraylist.
        if(songs !=null){
            for(File myFile: songs){
                if(!myFile.isHidden() && myFile.isDirectory()){
                    arrayList.addAll(fetchSongs(myFile));// all songs of the directory is added to the array list.
                }
                else{
                    if(myFile.getName().endsWith(".mp3") && !myFile.getName().startsWith(".")){ // dont inc. files starting fm (.).
                        arrayList.add(myFile);
                    }
                }
            }
        }
        return arrayList;
    }
}