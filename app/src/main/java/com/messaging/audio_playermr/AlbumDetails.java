package com.messaging.audio_playermr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.messaging.audio_playermr.Model.AlbumDetailsAdapter;
import com.messaging.audio_playermr.Model.SongFiles;
import static com.messaging.audio_playermr.MainActivity.songFiles;

import java.util.ArrayList;

public class AlbumDetails extends AppCompatActivity {

    RecyclerView recyclerView;
    ImageView albumpic;
    String albumName;
    ArrayList<SongFiles> albumsong=new ArrayList<>();
    AlbumDetailsAdapter albumDetailsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_details);
        recyclerView=findViewById(R.id.recyclerViewdetails);
        albumpic=findViewById(R.id.album_photo);
        albumName=getIntent().getStringExtra("albumname");

        int j=0;
        for (int i = 0; i < songFiles.size() ; i++) {
            if (albumName.equals(songFiles.get(i).getAlbum())){
                albumsong.add(j,songFiles.get(i));
                j++;
            }

        }
        byte[] image=getAlbumArt(albumsong.get(0).getPath());
        if (image!=null){
            Glide.with(AlbumDetails.this).load(image).into(albumpic);
        }
        else {
            Glide.with(AlbumDetails.this).load(R.drawable.applogomr).into(albumpic);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!(albumsong.size()<1)){
            albumDetailsAdapter=new AlbumDetailsAdapter(this,albumsong);
            recyclerView.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL,false));
            recyclerView.setAdapter(albumDetailsAdapter);
        }
    }

    private byte[] getAlbumArt(String uri){
        MediaMetadataRetriever mmtr=new MediaMetadataRetriever();
        mmtr.setDataSource(uri);
        byte[] art=mmtr.getEmbeddedPicture();
        mmtr.release();
        return art;

    }
}