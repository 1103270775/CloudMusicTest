package com.MusicPlayer.MyMusicPlayer.view;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.bumptech.glide.Glide;

import com.MusicPlayer.MyMusicPlayer.R;
import com.MusicPlayer.MyMusicPlayer.model.MusicModel;
import com.MusicPlayer.MyMusicPlayer.service.MusicService;

import me.wcy.lrcview.LrcView;

public class PlayMusicView extends FrameLayout {

    private Context mContext;
    public String leftTime="00:00";
    private MusicModel mMusicModel;
    private MusicService.MusicBind mMusicBinder;
    private Intent mServiceIntent;
    private boolean isPlaying, isBindService;
    private View mView;
    private FrameLayout mFlPlayMusic;
    private ImageView mIvIcon, mIvNeedle, mIvPlay;
    //歌词
    private LrcView musicLrc;

    //测试
    private ImageView playIV;

    private Animation mPlayMusicAnim, mPlayNeedleAnim, mStopNeedleAnim;

    public PlayMusicView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public PlayMusicView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PlayMusicView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PlayMusicView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        // MediaPlayer
        mContext = context;

        mView = LayoutInflater.from(mContext).inflate(R.layout.play_music, this, false);
//        View maView = View.inflate(mContext,R.layout.activity_play_music,null);
        mFlPlayMusic = mView.findViewById(R.id.fl_play_music);




        mFlPlayMusic.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                trigger();

            }
        });


        mIvIcon = mView.findViewById(R.id.iv_icon);
        mIvNeedle = mView.findViewById(R.id.iv_needle);
        mIvPlay = mView.findViewById(R.id.iv_play);
        musicLrc = mView.findViewById(R.id.music_lrc);

        /**
         * 1、定义所需要执行的动画
         *      1、光盘转动的动画
         *      2、指针指向光盘的动画
         *      3、指针离开光盘的动画
         * 2、startAnimation
         */
        mPlayMusicAnim = AnimationUtils.loadAnimation(mContext, R.anim.play_music_anim);
        mPlayNeedleAnim = AnimationUtils.loadAnimation(mContext, R.anim.play_needle_anim);
        mStopNeedleAnim = AnimationUtils.loadAnimation(mContext, R.anim.stop_needle_anim);

        addView(mView);
    }

    /**
     * 切换播放状态
     */
    public void trigger() {
        if (isPlaying) {
            stopMusic();
        } else {
            playMusic();
        }
    }

    /**
     * 播放音乐
     */
    public void playMusic() {
        isPlaying = true;
        mIvPlay.setVisibility(View.GONE);
        mFlPlayMusic.startAnimation(mPlayMusicAnim);
        mIvNeedle.startAnimation(mPlayNeedleAnim);
//        TextView view = findViewById(R.id.tv_leftTime);
//        view.setText(getPlayPosition());
//        启动服务
        startMusicService();


    }

    /**
     * 停止播放
     */
    public void stopMusic() {
        isPlaying = false;
        //暂停图标
        mIvPlay.setVisibility(View.VISIBLE);
//        musicLrc.setVisibility(View.VISIBLE);

        mFlPlayMusic.clearAnimation();
        mIvNeedle.startAnimation(mStopNeedleAnim);

        mMusicBinder.stopMusic();
        System.out.println("view层时间节点："+getPlayPosition());
//        leftTime = getPlayPosition();

//        Intent intent = new Intent(mContext, PlayMusicActivity.class);
//        intent.putExtra("leftTime", getPlayPosition());
//        mContext.startActivity(intent);

//        IntentFilter intentFilter = new IntentFilter(getPlayPosition());
//        MyReceiver myReceiver = new MyReceiver();
//        mContext.registerReceiver(myReceiver,intentFilter);
//        Intent intent = new Intent();
//        intent.setAction(getPlayPosition());
//        mContext.sendBroadcast(intent);
    }



    /**
     * 设置光盘中显示的音乐封面图片
     */
    private void setMusicIcon() {
        Glide.with(mContext)
                .load(mMusicModel.getPoster())
                .into(mIvIcon);
    }

    /**
     * 设置音乐播放模型
     */
    public void setMusic(MusicModel musicModel) {
        this.mMusicModel = musicModel;

        setMusicIcon();
    }

    //获取当前播放位置
    public int getPlayPosition(){
        return mMusicBinder.getPlayPosition();
    }
    //移动到当前点播放
    public void seekToPosition(int m){
        mMusicBinder.seekToPosition(m);
    }

    /**
     * 启动音乐服务
     */
    private void startMusicService() {
//        musicLrc.setVisibility(View.GONE);

        if (mServiceIntent == null) {
            mServiceIntent = new Intent(mContext, MusicService.class);
            mContext.startService(mServiceIntent);
        } else {
            mMusicBinder.playMusic();


        }

        // 当前未绑定，绑定服务，同时修改绑定状态
        if (!isBindService) {
            isBindService = true;
            mContext.bindService(mServiceIntent, conn, Context.BIND_AUTO_CREATE);
        }
    }

    /**
     * 销毁方法，需要在 activity 被销毁的时候调用
     */
    public void destroy() {
        // 如果已绑定服务，则解除绑定，同时修改绑定状态
        if (isBindService) {
            isBindService = false;
            mContext.unbindService(conn);
        }

    }

    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mMusicBinder = (MusicService.MusicBind) service;
            mMusicBinder.setMusic(mMusicModel);
            mMusicBinder.playMusic();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
}
