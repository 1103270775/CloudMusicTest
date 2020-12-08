package com.MusicPlayer.MyMusicPlayer.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import com.MusicPlayer.MyMusicPlayer.R;
import com.MusicPlayer.MyMusicPlayer.adapter.MusicAdapter;
import com.MusicPlayer.MyMusicPlayer.model.MusicModel;
import com.MusicPlayer.MyMusicPlayer.utils.CheckNetwork;
import com.MusicPlayer.MyMusicPlayer.utils.ToastUtil;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE" };

    @BindView(R.id.rv_music)
    RecyclerView rvMusic;

    private MusicAdapter adapter;
    private List<MusicModel> mList = new ArrayList<>();
    MediaPlayer mediaPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        verifyStoragePermissions(this);
        initView();
    }

    private void initView() {
        ButterKnife.bind(this);

//        // 模拟从网络获取音乐数据列表
//        for (int i = 0; i < 10; i++) {
//
//            MusicModel musicModel = new MusicModel();
//            musicModel.setName("理想三旬");
//            musicModel.setAuthor("陈鸿宇");
//            musicModel.setMusicId("123456");
//            musicModel.setPoster("https://img1.doubanio.com/view/subject/m/public/s29719507.jpg");
////            musicModel.setRemark("旅途中的追逐");
//            musicModel.setPath("http://res.lgdsunday.club/Nostalgic%20Piano.mp3");
//            mList.add(musicModel);
//
//            MusicModel musicModel1 = new MusicModel();
//            musicModel1.setName("一如年少模样");
//            musicModel1.setAuthor("陈鸿宇");
//            musicModel1.setMusicId("654321");
//            musicModel1.setPoster("https://img1.doubanio.com/view/subject/m/public/s29361389.jpg");
//            musicModel1.setRemark("三旬尚远浓烟散 一如年少迟夏归");
//            musicModel1.setPath("http://res.lgdsunday.club/Nostalgic%20Piano.mp3");

        //  1.获取ContentResolver对象
        ContentResolver resolver = getContentResolver();
        //  2.获取本地音乐存储的Uri地址
        //        Uri uri = MediaStore.Audio.Media.INTERNAL_CONTENT_URI;//内部存储的
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;//额外存储的---指SD卡中的歌曲
        //  3.开始查询地址
        Cursor cursor = resolver.query(uri, null, null, null, null);
        //  4.遍历Cursor
        int id=0;//歌曲编号
        assert cursor != null;
        while (cursor.moveToNext()){
            //获取音乐信息
            String song = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
            String singer = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
            String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
            id++;
            String sid = String.valueOf(id);
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
            SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
            String time = sdf.format(new Date(duration));

            MusicModel bean = new MusicModel();
            bean.setMusicId(sid);
            bean.setName(song);
            bean.setAuthor(singer);
            bean.setPath(path);
            bean.setPoster("https://img1.doubanio.com/view/subject/m/public/s29361389.jpg");
            bean.setDuration(time);
            bean.setEndTime(duration);//endTime
            mList.add(bean);//封装数据
            System.out.println(bean);
        }

        rvMusic.setLayoutManager(new GridLayoutManager(this, 1));
        adapter = new MusicAdapter(this, mList);
        rvMusic.setAdapter(adapter);

        adapter.setItemClikListener(new MusicAdapter.OnItemClikListener() {
            @Override
            public void onItemClik(View view, int position) throws IOException {
                if (!CheckNetwork.isNetworkConnected(MainActivity.this)) {
                    ToastUtil.showToastLong("当前网络不可用，请检查您的网络设置");
                    return;
                }
                Intent intent = new Intent(MainActivity.this, PlayMusicActivity.class);
                intent.putExtra(PlayMusicActivity.NAME, mList.get(position).getName());
                intent.putExtra(PlayMusicActivity.POSTER, mList.get(position).getPoster());
                intent.putExtra(PlayMusicActivity.PATH, mList.get(position).getPath());
                intent.putExtra(PlayMusicActivity.AUTHOR, mList.get(position).getAuthor());
                System.out.println("当前位置："+position);
//                intent.putExtra(PlayMusicActivity.Position, position);
                //传递音乐存储列表+音乐位置
//                intent.putExtras("list", (Serializable)mList);
                Bundle bundle = new Bundle();
                bundle.putSerializable(PlayMusicActivity.List, (Serializable) mList);
                bundle.putInt(PlayMusicActivity.Position,position);
                //bundle传递数据
                intent.putExtras(bundle);
                startActivity(intent);
            }

            @Override
            public void onItemLongClik(View view, int position) {
//                Intent intent = new Intent(MainActivity.this, PlayMusicActivity.class);
//                intent.putExtra(PlayMusicActivity.NAME, mList.get(position).getName());
//                intent.putExtra(PlayMusicActivity.POSTER, mList.get(position).getPoster());
//                intent.putExtra(PlayMusicActivity.PATH, mList.get(position).getPath());
//                intent.putExtra(PlayMusicActivity.AUTHOR, mList.get(position).getAuthor());
//                startActivity(intent);
            }
        });

    }

    //动态申请权限
    private void verifyStoragePermissions(Activity activity) {

        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
