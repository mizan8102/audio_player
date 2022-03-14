package com.messaging.audio_playermr;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.palette.graphics.Palette;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.messaging.audio_playermr.Model.SongFiles;

import java.util.ArrayList;
import java.util.Random;
import static android.media.MediaPlayer.create;
import static com.messaging.audio_playermr.MainActivity.repeatBoolean;
import static com.messaging.audio_playermr.MainActivity.shuffleBoolean;
import static com.messaging.audio_playermr.MainActivity.songFiles;
import static com.messaging.audio_playermr.Model.AlbumDetailsAdapter.albumsong;
import static com.messaging.audio_playermr.Model.MusicAdapter.mFiles;

public class PlayerActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener {

    TextView song_name,song_artist,played_duration,played_total;
    ImageView cover_art,next_btn,pre_btn,shuffil,repeatbtn;
    FloatingActionButton pauseplyabtn;
    SeekBar seekBar;
    int position=-1;
    static ArrayList<SongFiles> listsong=new ArrayList<>();
    static Uri uri;
    static MediaPlayer mediaPlayer;
    private Handler handler=new Handler();
    Thread playthread,prethread,nextthread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        initview();
        getIntentMethod();
        song_name.setText(listsong.get(position).getTitle());
        song_artist.setText(listsong.get(position).getArtist());
        mediaPlayer.setOnCompletionListener(this);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (mediaPlayer!=null && b){
                    mediaPlayer.seekTo(i*1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        PlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer!=null){

                    int nCurrentPosition=mediaPlayer.getCurrentPosition()/1000;
                    seekBar.setProgress(nCurrentPosition);
                    played_duration.setText(formattedTime(nCurrentPosition));
                }
                handler.postDelayed(this,1000);
            }
        });
        shuffil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (shuffleBoolean){
                    shuffleBoolean=false;
                    shuffil.setImageResource(R.drawable.ic_baseline_shuffle_on);
                }
                else {
                    shuffleBoolean=true;
                    shuffil.setImageResource(R.drawable.ic_baseline_shuffle_of);
                }
            }
        });

        repeatbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (repeatBoolean){
                    repeatBoolean=false;
                    repeatbtn.setImageResource(R.drawable.ic_baseline_repeat_24);
                }else {
                    repeatBoolean=true;
                    repeatbtn.setImageResource(R.drawable.ic_baseline_repeat_on);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        playThread();
        nextThread();
        preThread();
        super.onResume();
    }

    private void preThread() {
        prethread=new Thread(){
            @Override
            public void run() {
                super.run();
                pre_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                            prebtnClicked();
                    }
                });
            }
        };
        prethread.start();
    }

    private void prebtnClicked() {

        if (mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            mediaPlayer.release();

            if (shuffleBoolean && !repeatBoolean){

                position =getRandom(listsong.size()-1);
            }
            else if (!shuffleBoolean && !repeatBoolean){
                position=((position-1)<0 ? (listsong.size()-1):(position-1));
            }

            uri=Uri.parse(listsong.get(position).getPath());
            mediaPlayer= create(getApplicationContext(),uri);
            metaData(uri);
            song_name.setText(listsong.get(position).getTitle());
            song_artist.setText(listsong.get(position).getArtist());
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer!=null){

                        int nCurrentPosition=mediaPlayer.getCurrentPosition()/1000;
                        seekBar.setProgress(nCurrentPosition);

                    }
                    handler.postDelayed(this,1000);
                }
            });
            mediaPlayer.setOnCompletionListener(this);
            pauseplyabtn.setBackgroundResource(R.drawable.ic_baseline_pause);
            mediaPlayer.start();

        }
        else {
            mediaPlayer.stop();
            mediaPlayer.release();
            if (shuffleBoolean && !repeatBoolean){

                position =getRandom(listsong.size()-1);
            }
            else if (!shuffleBoolean && !repeatBoolean){
                position=((position-1)<0 ? (listsong.size()-1):(position-1));
            }
            uri=Uri.parse(listsong.get(position).getPath());
            mediaPlayer= create(getApplicationContext(),uri);
            metaData(uri);
            song_name.setText(listsong.get(position).getTitle());
            song_artist.setText(listsong.get(position).getArtist());
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer!=null){

                        int nCurrentPosition=mediaPlayer.getCurrentPosition()/1000;
                        seekBar.setProgress(nCurrentPosition);

                    }
                    handler.postDelayed(this,1000);
                }
            });
            mediaPlayer.setOnCompletionListener(this);
            pauseplyabtn.setBackgroundResource(R.drawable.ic_baseline_play);
        }
    }

    private void nextThread() {
        nextthread=new Thread(){
            @Override
            public void run() {
                super.run();
                next_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        nextbtnClicked();
                    }
                });
            }
        };
        nextthread.start();
    }

    private void nextbtnClicked() {
        if (mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            mediaPlayer.release();

            if (shuffleBoolean && !repeatBoolean){

                position =getRandom(listsong.size()-1);
            }
            else if (!shuffleBoolean && !repeatBoolean){
                position=(position+1)% listsong.size();
            }

            uri=Uri.parse(listsong.get(position).getPath());
            mediaPlayer= create(getApplicationContext(),uri);
            metaData(uri);
            song_name.setText(listsong.get(position).getTitle());
            song_artist.setText(listsong.get(position).getArtist());
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer!=null){

                        int nCurrentPosition=mediaPlayer.getCurrentPosition()/1000;
                        seekBar.setProgress(nCurrentPosition);

                    }
                    handler.postDelayed(this,1000);
                }
            });
            mediaPlayer.setOnCompletionListener(this);
            pauseplyabtn.setBackgroundResource(R.drawable.ic_baseline_pause);
            mediaPlayer.start();

        }
        else {
            mediaPlayer.stop();
            mediaPlayer.release();
            if (shuffleBoolean && !repeatBoolean){

                position =getRandom(listsong.size()-1);
            }
            else if (!shuffleBoolean && !repeatBoolean){
                position=(position+1)% listsong.size();
            }

            uri=Uri.parse(listsong.get(position).getPath());
            mediaPlayer= create(getApplicationContext(),uri);
            metaData(uri);
            song_name.setText(listsong.get(position).getTitle());
            song_artist.setText(listsong.get(position).getArtist());
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer!=null){

                        int nCurrentPosition=mediaPlayer.getCurrentPosition()/1000;
                        seekBar.setProgress(nCurrentPosition);

                    }
                    handler.postDelayed(this,1000);
                }
            });
            mediaPlayer.setOnCompletionListener(this);
            pauseplyabtn.setBackgroundResource(R.drawable.ic_baseline_play);
        }

    }

    private int getRandom(int i) {
        Random random=new Random();

        return random.nextInt(i+1);
    }

    private void playThread() {
        playthread=new Thread(){
            @Override
            public void run() {
                super.run();
                pauseplyabtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        pauseplyabtnClicked();
                    }
                });
            }
        };
        playthread.start();
        
    }

    private void pauseplyabtnClicked() {

        if (mediaPlayer.isPlaying()){
            pauseplyabtn.setImageResource(R.drawable.ic_baseline_play);
            mediaPlayer.pause();
            seekBar.setMax(mediaPlayer.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer!=null){

                        int nCurrentPosition=mediaPlayer.getCurrentPosition()/1000;
                        seekBar.setProgress(nCurrentPosition);

                    }
                    handler.postDelayed(this,1000);
                }
            });
        }
        else {
            pauseplyabtn.setImageResource(R.drawable.ic_baseline_pause);
            mediaPlayer.start();
            seekBar.setMax(mediaPlayer.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer!=null){

                        int nCurrentPosition=mediaPlayer.getCurrentPosition()/1000;
                        seekBar.setProgress(nCurrentPosition);

                    }
                    handler.postDelayed(this,1000);
                }
            });
        }

    }

    private String formattedTime(int nCurrentPosition) {
        String totalOut="";
        String totalNew="";
        String seconds=String.valueOf(nCurrentPosition % 60);
        String miniutes=String.valueOf(nCurrentPosition /60);
        totalOut=miniutes+":"+seconds;
        totalNew=miniutes+":"+seconds;
        if (seconds.length()==1){
            return totalNew;
        }
        else {
            return totalOut;
        }
    }

    private void getIntentMethod() {

        position=getIntent().getIntExtra("position",-1);
        String sender=getIntent().getStringExtra("sender");
        if (sender!=null && sender.equals("albumdetails")){

           listsong=albumsong;
        }
        else {
            listsong=mFiles;
        }

        if (listsong!=null){

            pauseplyabtn.setImageResource(R.drawable.ic_baseline_pause);
            uri=Uri.parse(listsong.get(position).getPath());
        }
        if (mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer= create(getApplicationContext(),uri);
            mediaPlayer.start();
        }
        else {
            mediaPlayer= create(getApplicationContext(),uri);
            mediaPlayer.start();
        }
        seekBar.setMax(mediaPlayer.getDuration()/1000);
        metaData(uri);

    }

    private void initview() {
        song_name=findViewById(R.id.song_name);
        song_artist=findViewById(R.id.song_artist);
        played_duration=findViewById(R.id.durationpalyed);
        played_total=findViewById(R.id.durationtotal);
        cover_art=findViewById(R.id.coverart);
        next_btn=findViewById(R.id.next);
        pre_btn=findViewById(R.id.previous);
        shuffil=findViewById(R.id.shuffle);
        repeatbtn=findViewById(R.id.repeat);
        pauseplyabtn=findViewById(R.id.pause_play);
        seekBar=findViewById(R.id.seekbartxt);

    }

    @SuppressLint("ResourceType")
    private void metaData(Uri uri){

        MediaMetadataRetriever metadataRetriever=new MediaMetadataRetriever();
        metadataRetriever.setDataSource(uri.toString());
        int durationtotal=Integer.parseInt(listsong.get(position).getDuration())/1000;
        played_total.setText(formattedTime(durationtotal));
        byte[] art=metadataRetriever.getEmbeddedPicture();
        Bitmap bitmap;
        if (art!=null){

            bitmap= BitmapFactory.decodeByteArray(art,0,art.length);
            imageAnimation(this,cover_art,bitmap);
            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(@Nullable Palette palette) {
                    Palette.Swatch swatch=palette.getDominantSwatch();
                    if (swatch!=null){
                        ImageView gradient=findViewById(R.id.imageviewgradient);
                        RelativeLayout mcontainer=findViewById(R.id.mcontainer);
                        gradient.setBackgroundResource(R.drawable.gradient_bg);
                        mcontainer.setBackgroundResource(R.drawable.main_bg);
                        GradientDrawable gradientDrawable=new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP
                                ,new int[]{swatch.getRgb() , 0x00000000});
                        gradient.setBackground(gradientDrawable);
                        GradientDrawable gradientDrawableBg=new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP
                                ,new int[]{swatch.getRgb(),swatch.getRgb()});
                       mcontainer.setBackground(gradientDrawableBg);
                       song_name.setTextColor(swatch.getTitleTextColor());
                       song_artist.setTextColor(swatch.getBodyTextColor());

                    }else {


                        ImageView gradient=findViewById(R.id.imageviewgradient);
                        RelativeLayout mcontainer=findViewById(R.id.mcontainer);
                        gradient.setBackgroundResource(R.drawable.gradient_bg);
                        mcontainer.setBackgroundResource(R.drawable.main_bg);
                        GradientDrawable gradientDrawable=new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP
                                ,new int[]{0xff000000,0x00000000});
                        gradient.setBackground(gradientDrawable);
                        GradientDrawable gradientDrawableBg=new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP
                                ,new int[]{0xff000000,0xff000000});
                        mcontainer.setBackground(gradientDrawableBg);
                        song_name.setTextColor(Color.WHITE);
                        song_artist.setTextColor(Color.DKGRAY);
                    }
                }
            });
        }
        else {
            /////////////////////Charnge this after///////////////////////
            Glide.with(this)
                    .load(R.drawable.blackapp).into(cover_art);
            ImageView gradient=findViewById(R.id.imageviewgradient);
            RelativeLayout mcontainer=findViewById(R.id.mcontainer);
            gradient.setBackgroundResource(Color.TRANSPARENT);
            mcontainer.setBackgroundResource(R.drawable.main_bg);
            song_name.setTextColor(Color.WHITE);
            song_artist.setTextColor(Color.GRAY);
        }
    }
    public void imageAnimation(final Context context, final ImageView imageView, final Bitmap bitmap){

        final Animation animationIn= AnimationUtils.loadAnimation(context,android.R.anim.fade_in);
        Animation animationOut= AnimationUtils.loadAnimation(context,android.R.anim.fade_out);
        animationOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Glide.with(context).load(bitmap).into(imageView);
                animationIn.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                imageView.startAnimation(animationIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        imageView.startAnimation(animationOut);

    }

    @Override
    public void onCompletion(MediaPlayer mm) {
        nextbtnClicked();
        if (mediaPlayer!=null){

            mediaPlayer= MediaPlayer.create(getApplicationContext(),uri);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(this);
        }
    }
}