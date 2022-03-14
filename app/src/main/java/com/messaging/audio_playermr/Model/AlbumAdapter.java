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

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<SongFiles> songFiles;

    public AlbumAdapter(Context context, ArrayList<SongFiles> songFiles) {
        this.context = context;
        this.songFiles = songFiles;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // LayoutInflater layoutInflater=new
        View view=LayoutInflater.from(context).inflate(R.layout.albumitem,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        SongFiles so=songFiles.get(position);
        holder.textView.setText(so.getAlbum());

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
                Intent intent=new Intent(context, AlbumDetails.class);
                intent.putExtra("albumname",songFiles.get(position).getAlbum());
                context.startActivity(intent);

            }
        });

    }


    @Override
    public int getItemCount() {
        return songFiles.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView,menuMore;
        TextView textView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.album_image);
            textView=itemView.findViewById(R.id.album_name);

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
