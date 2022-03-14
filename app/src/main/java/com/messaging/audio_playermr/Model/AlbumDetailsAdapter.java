package com.messaging.audio_playermr.Model;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.messaging.audio_playermr.AlbumDetails;
import com.messaging.audio_playermr.PlayerActivity;
import com.messaging.audio_playermr.R;

import java.io.File;
import java.util.ArrayList;

public class AlbumDetailsAdapter extends RecyclerView.Adapter<AlbumDetailsAdapter.MyViewHolder> {
    private Context context;
   static public ArrayList<SongFiles> albumsong;

    public AlbumDetailsAdapter(Context context, ArrayList<SongFiles> albumsong) {
        this.context = context;
        this.albumsong = albumsong;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // LayoutInflater layoutInflater=new
        View view=LayoutInflater.from(context).inflate(R.layout.music_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        SongFiles so=albumsong.get(position);
        holder.textView.setText(so.getTitle());

        final byte[] image=getAlbumArt(so.getPath());
        if (image!=null){
            Glide.with(context).asBitmap()
                    .load(image).into(holder.imageView);
        }
        else {
            Glide.with(context)
                    .load(R.drawable.applogomr).into(holder.imageView);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, PlayerActivity.class);
                intent.putExtra("sender","albumdetails");
                intent.putExtra("position",position);
                context.startActivity(intent);

            }
        });

    }


    @Override
    public int getItemCount() {
        return albumsong.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView,menuMore;
        TextView textView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.music_img);
            textView=itemView.findViewById(R.id.music_file_name);

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
