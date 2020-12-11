package com.MusicPlayer.MyMusicPlayer.activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import com.MusicPlayer.MyMusicPlayer.R;
import com.MusicPlayer.MyMusicPlayer.model.MusicModel;
import com.MusicPlayer.MyMusicPlayer.view.PlayMusicView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLOutput;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.BlurTransformation;
import me.wcy.lrcview.LrcView;

public class PlayMusicActivity extends AppCompatActivity {

    public static final String NAME = "name";
    public static final String POSTER = "poster";
    public static final String PATH = "path";
    public static final String AUTHOR = "author";
    public static final String List = "list";//音乐总列表
    public static final String Position = "position";//音乐位置



    private String mName;
    private String mPoster;
    private String mPath;
    private String mAuthor;
    private MusicModel mMusicModel;
    private Boolean IsPlay = false;
    private List<MusicModel> mList = new ArrayList<>();
    private int position;//播放位置
    private int endTime;//结束时间

    @BindView(R.id.iv_bg)
    ImageView mIvBg;
    @BindView(R.id.tv_name)
    TextView mTvName;
    @BindView(R.id.tv_author)
    TextView mTvAuthor;
    @BindView(R.id.local_music_bottom_iv_next)
    ImageView nextMusic;
    @BindView(R.id.local_music_bottom_iv_last)
    ImageView lastMusic;
    @BindView(R.id.local_music_bottom_iv_play)
    ImageView playMusicIv;
    @BindView(R.id.play_music_view)
    PlayMusicView mPlayMusicView;

    @BindView(R.id.tv_leftTime)
    TextView leftTime;//左边时间
    @BindView(R.id.tv_rightTime)
    TextView rightTime;//右边时间
    @BindView(R.id.seekBar)
    SeekBar seekBar;//进度条
    @BindView(R.id.music_lrc_iv)
    CheckBox music_lrc_iv;//歌词显示按钮
    @BindView(R.id.music_lrc)
    LrcView musicLrc;//歌词加载控件



    Handler handler;
    private SimpleDateFormat time=new SimpleDateFormat("mm:ss");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_music);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        initView();
    }

    private void initView() {
        ButterKnife.bind(this);
        handler=new Handler();
        mName = getIntent().getStringExtra(NAME);
        mPoster = getIntent().getStringExtra(POSTER);
        mPath = getIntent().getStringExtra(PATH);
        mAuthor = getIntent().getStringExtra(AUTHOR);
        //播放位置
        //接受音乐列表 + 音乐位置
        Bundle bundle = getIntent().getExtras();
        mList = (List<MusicModel>) bundle.getSerializable(List);
        position = bundle.getInt(Position);

        rightTime.setText(mList.get(position).getDuration());
        //设置seekBar的最大值
        endTime = (int)mList.get(position).getEndTime();
        seekBar.setMax(endTime);

        System.out.println("Hello:"+position);

        System.out.println("总长度："+mList.size());
        //歌曲对象
        mMusicModel = new MusicModel();
        mMusicModel.setName(mName);
        mMusicModel.setPath(mPath);
        mMusicModel.setPoster(mPoster);
        mMusicModel.setAuthor(mAuthor);


        Glide.with(this)
                .load(mMusicModel.getPoster())
                // 设置音乐播放器背景图片的高斯模糊度
                .apply(RequestOptions.bitmapTransform(new BlurTransformation(25, 35)))
                .into(mIvBg);
        mTvName.setText(mMusicModel.getName());
        mTvAuthor.setText(mMusicModel.getAuthor());

        //音乐播放转盘
        mPlayMusicView.setMusic(mMusicModel);

        mPlayMusicView.playMusic();
        //多线程开始，延时300ms 先延时再执行
        handler.postDelayed(runnable,300);

        //赋初值
        leftTime.setText(mPlayMusicView.leftTime);

        IsPlay = true;

        //音乐-暂停/开始
//        View playMusicIv = findViewById(R.id.local_music_bottom_iv_play);
        playMusicIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(IsPlay) {
                    mPlayMusicView.trigger();
                    playMusicIv.setImageResource(R.mipmap.icon_play);
                    IsPlay = false;
                }else
                {
                    mPlayMusicView.trigger();
                    playMusicIv.setImageResource(R.mipmap.icon_pause);
                    IsPlay = true;
                }
            }
        });

        //下一曲
//        View nextMusic = findViewById(R.id.local_music_bottom_iv_next);
        nextMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NextMusic();
            }
        });

        //上一曲
//        View lastMusic = findViewById(R.id.local_music_bottom_iv_last);
        lastMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LastMusic();
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    mPlayMusicView.seekToPosition(seekBar.getProgress());
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //歌词显示监听器
        music_lrc_iv.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                getLrcText(mMusicModel);
                PlayMusicView view = findViewById(R.id.play_music_view);

                if(isChecked){
                    musicLrc.setVisibility(View.VISIBLE);
//                    PlayMusicView view = findViewById(R.id.play_music_view);
                    view.setVisibility(View.GONE);
                }else{
                    musicLrc.setVisibility(View.GONE);
                    view.setVisibility(View.VISIBLE);
                }
            }
        });
    }
    private void getLrcText(MusicModel musicBean) {
        //获取歌词路径
        String musicPath = musicBean.getPath();
        //字符串替换
        String lrcPath = musicPath.replace(".mp3",".lrc");
        //查找歌词
        String lrcText = null;
        try {
            InputStream is = new FileInputStream(new File(lrcPath));
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            lrcText = new String(buffer,"gbk");//需要gbk格式
        } catch (IOException e) {
            e.printStackTrace();
        }
        //装载歌词
        musicLrc.loadLrc(lrcText);

    }
    //上一首歌
    private void LastMusic(){
        //停止上一首歌
        mPlayMusicView.stopMusic();
        mPlayMusicView.destroy();

        if(position<=0){
            Toast.makeText(this,"已经是第一首了，没有上一曲!",Toast.LENGTH_SHORT).show();
            playMusicIv.setImageResource(R.mipmap.icon_play);
        }else{
            position--;//下一曲
            rightTime.setText(mList.get(position).getDuration());
            //设置seekBar的最大值
            seekBar.setMax((int)mList.get(position).getEndTime());
            //歌曲对象
            mMusicModel = new MusicModel();
            mMusicModel.setName(mList.get(position).getAuthor());
            mMusicModel.setPath(mList.get(position).getPath());
            mMusicModel.setPoster(mList.get(position).getPoster());
            mMusicModel.setAuthor(mList.get(position).getName());
            mTvName.setText(mMusicModel.getName());
            mTvAuthor.setText(mMusicModel.getAuthor());
            //音乐播放转盘
            mPlayMusicView.setMusic(mMusicModel);
            mPlayMusicView.playMusic();

            IsPlay = true;
            playMusicIv.setImageResource(R.mipmap.icon_pause);
        }



    }

    private void NextMusic() {
        //停止上一首歌
        mPlayMusicView.stopMusic();
        mPlayMusicView.destroy();
        position++;//下一曲
        if(position>mList.size()-1){
            Toast.makeText(this,"已经是最后一首了，没有下一曲!",Toast.LENGTH_SHORT).show();
            playMusicIv.setImageResource(R.mipmap.icon_play);

//            position = 0;//回到开始
        }else {
//            position++;//下一曲
            rightTime.setText(mList.get(position).getDuration());
            //设置seekBar的最大值
            seekBar.setMax((int)mList.get(position).getEndTime());

            //歌曲对象
            mMusicModel = new MusicModel();
            mMusicModel.setName(mList.get(position).getAuthor());
            mMusicModel.setPath(mList.get(position).getPath());
            mMusicModel.setPoster(mList.get(position).getPoster());
            mMusicModel.setAuthor(mList.get(position).getName());
            mTvName.setText(mMusicModel.getName());
            mTvAuthor.setText(mMusicModel.getAuthor());
            //音乐播放转盘
            mPlayMusicView.setMusic(mMusicModel);
            mPlayMusicView.playMusic();
//            getLrcText(mMusicModel);
            IsPlay = true;
            playMusicIv.setImageResource(R.mipmap.icon_pause);
        }

    }

    /**
     * 后退按钮点击事件
     */
    public void onBackClick(View view) {
        onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPlayMusicView.destroy();
//        handler.removeCallbacks(runnable);

    }
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            //给当前播放的音乐设置 播放时间
            leftTime.setText(time.format(mPlayMusicView.getPlayPosition()));
            //进度条更新
            seekBar.setProgress(mPlayMusicView.getPlayPosition());
//            System.out.println("HELLO");
            //更新歌词
            musicLrc.updateTime(mPlayMusicView.getPlayPosition());
//            System.out.println("Size:"+mList.size());
//            System.out.println("position:"+position);
            if(position<mList.size()-1){//存在下一曲
                //多线程中的endTime需要适配更新，不然会出现歌曲和时间错配
                endTime = (int)mList.get(position).getEndTime();
                if(endTime - mPlayMusicView.getPlayPosition() < 500){//剩余时间不够，自动触发下一首歌
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //ui线程触发按钮按下 如果不使用UI线程按钮，可能无法跳转到下一曲
                            nextMusic.performClick();
                        }
                    });
                }
            }else{//不存在下一曲
                //多线程中的endTime需要适配更新，不然会出现歌曲和时间错配
                try {
                    endTime = (int)mList.get(position).getEndTime();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(endTime-mPlayMusicView.getPlayPosition()<=0){//到终点了
                    runOnUiThread(new Runnable() {//回到列表的第一首曲子00
                        @Override
                        public void run() {
                            position = -1;
                            nextMusic.performClick();

                        }
                    });

                }

            }
            handler.postDelayed(runnable,300);

        }


    };
}
