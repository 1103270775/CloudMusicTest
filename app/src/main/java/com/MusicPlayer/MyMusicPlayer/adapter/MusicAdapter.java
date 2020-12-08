package com.MusicPlayer.MyMusicPlayer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import com.MusicPlayer.MyMusicPlayer.R;
import com.MusicPlayer.MyMusicPlayer.model.MusicModel;

import java.io.IOException;
import java.util.List;


public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ViewHolder> {

    private Context mContext;
    private List<MusicModel> mList;
    private MusicModel data;
    private OnItemClikListener mOnItemClikListener;

    private boolean isSelect;

    public MusicAdapter(Context context, List<MusicModel> mList) {
        this.mContext = context;
        this.mList = mList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_music_view, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        data = mList.get(position);
        RequestOptions options = new RequestOptions()
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher);
        Glide.with(mContext).load(data.getPoster()).apply(options).into(holder.iv_cover);
        holder.tv_name.setText(data.getName());
        holder.tv_author.setText(String.format(" - %s", data.getAuthor()));
//        holder.tv_remark.setText(data.getRemark());


        holder.tv_time.setText(data.getDuration());


        if (mOnItemClikListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getLayoutPosition();
                    try {
                        mOnItemClikListener.onItemClik(holder.itemView, pos);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClikListener.onItemLongClik(holder.itemView, pos);
                    return true;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_time;
        private ImageView iv_cover;
        private TextView tv_name;
        private TextView tv_author;
//        private TextView tv_remark;

        public ViewHolder(View itemView) {
            super(itemView);
            iv_cover = (ImageView) itemView.findViewById(R.id.iv_cover);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_author = (TextView) itemView.findViewById(R.id.tv_author);
//            tv_remark = (TextView) itemView.findViewById(R.id.tv_remark);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
        }
    }

    public interface OnItemClikListener {
        void onItemClik(View view, int position) throws IOException;

        void onItemLongClik(View view, int position);
    }

    public void setItemClikListener(OnItemClikListener mOnItemClikListener) {
        this.mOnItemClikListener = mOnItemClikListener;
    }

}
