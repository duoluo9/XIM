package com.zhangteng.xim.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.zhangteng.swiperecyclerview.adapter.BaseAdapter;
import com.zhangteng.swiperecyclerview.widget.CircleImageView;
import com.zhangteng.xim.R;
import com.zhangteng.xim.bmob.callback.BmobCallBack;
import com.zhangteng.xim.bmob.entity.Like;
import com.zhangteng.xim.bmob.entity.Remark;
import com.zhangteng.xim.bmob.entity.Story;
import com.zhangteng.xim.bmob.entity.User;
import com.zhangteng.xim.bmob.http.DataApi;
import com.zhangteng.xim.db.DBManager;
import com.zhangteng.xim.db.bean.LocalUser;

import java.util.List;

import cn.bmob.v3.exception.BmobException;

/**
 * Created by swing on 2018/5/22.
 */
public class CircleAdapter extends BaseAdapter<Story> {
    private Context context;

    public CircleAdapter(Context context, List<Story> data) {
        super(data);
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.circle_body_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        Story story = data.get(position);
        ((ItemViewHolder) holder).expandableTextView.setText(story.getContent());
        ((ItemViewHolder) holder).recyclerView.setLayoutManager(new GridLayoutManager(context, 3));
        ((ItemViewHolder) holder).recyclerView.setAdapter(new NineImageAdapter(context, story.getIconPaths()));
        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.mipmap.app_icon).centerCrop();
        Glide.with(context)
                .load(story.getUser().getIcoPath())
                .apply(requestOptions)
                .into(((ItemViewHolder) holder).circleImageView);

        ((ItemViewHolder) holder).name.setText(story.getUser().getUsername());
        ((ItemViewHolder) holder).location.setText("");
        ((ItemViewHolder) holder).time.setText(story.getCreatedAt());
        Like like = new Like();
        like.setStory(story);

        DataApi.getInstance().queryLike(like, new BmobCallBack<List<Like>>(context, false) {
            @Override
            public void onSuccess(@Nullable List<Like> bmobObject) {
                StringBuffer stringBuffer = new StringBuffer();
                for (Like like1 : bmobObject) {
                    if (like1.getUser().getUsername() == null && like1.getUser().getObjectId() != null) {
                        LocalUser localUser = DBManager.instance().queryUser(like1.getUser().getObjectId());
                        like1.setUser(User.getUser(localUser));
                    }
                    stringBuffer.append(like1.getUser().getUsername()).append(" ");
                }
                ((ItemViewHolder) holder).like.setText(stringBuffer.toString());
            }
        });

        Remark remark = new Remark();
        remark.setStory(story);
        DataApi.getInstance().queryRemark(remark, new BmobCallBack<List<Remark>>(context, false) {
            @Override
            public void onSuccess(@Nullable List<Remark> bmobObject) {
                final StringBuffer stringBuffer1 = new StringBuffer();
                for (final Remark remark1 : bmobObject) {
                    if (remark1.getUser().getUsername() == null && remark1.getUser().getObjectId() != null) {
                        LocalUser localUser = DBManager.instance().queryUser(remark1.getUser().getObjectId());
                        remark1.setUser(User.getUser(localUser));
                    }
                    if (remark1.getRemark() != null) {
                        if (remark1.getRemark().getUser() == null) {
                            DataApi.getInstance().queryRemark(remark1.getRemark().getObjectId(), new BmobCallBack<Remark>(context, false) {
                                @Override
                                public void onSuccess(@Nullable Remark bmobObject) {
                                    remark1.setRemark(bmobObject);
                                    if (null == remark1.getRemark().getUser().getUsername()
                                            && remark1.getRemark().getUser().getObjectId() != null) {
                                        LocalUser localUser = DBManager.instance().queryUser(remark1.getRemark().getUser().getObjectId());
                                        remark1.getRemark().setUser(User.getUser(localUser));
                                        stringBuffer1.append(remark1.getUser().getUsername());
                                        stringBuffer1.append(" 回复 ").append(localUser.getUsername());
                                        stringBuffer1.append(" : ").append(remark1.getContent()).append("\n");
                                        ((ItemViewHolder) holder).comment.setText(stringBuffer1.toString());
                                    } else {
                                        stringBuffer1.append(remark1.getUser().getUsername());
                                        stringBuffer1.append(" 回复 ").append(remark1.getRemark().getUser().getUsername());
                                        stringBuffer1.append(" : ").append(remark1.getContent()).append("\n");
                                        ((ItemViewHolder) holder).comment.setText(stringBuffer1.toString());
                                    }
                                }

                                @Override
                                public void onFailure(BmobException bmobException) {
                                    super.onFailure(bmobException);
                                    if (null == remark1.getRemark().getUser().getUsername()
                                            && remark1.getRemark().getUser().getObjectId() != null) {
                                        LocalUser localUser = DBManager.instance().queryUser(remark1.getRemark().getUser().getObjectId());
                                        remark1.getRemark().setUser(User.getUser(localUser));
                                        stringBuffer1.append(remark1.getUser().getUsername());
                                        stringBuffer1.append(" 回复 ").append(localUser.getUsername());
                                        stringBuffer1.append(" : ").append(remark1.getContent()).append("\n");
                                        ((ItemViewHolder) holder).comment.setText(stringBuffer1.toString());
                                    } else {
                                        stringBuffer1.append(remark1.getUser().getUsername());
                                        stringBuffer1.append(" 回复 ").append(remark1.getRemark().getUser().getUsername());
                                        stringBuffer1.append(" : ").append(remark1.getContent()).append("\n");
                                        ((ItemViewHolder) holder).comment.setText(stringBuffer1.toString());
                                    }
                                }
                            });
                        } else {
                            if (null == remark1.getRemark().getUser().getUsername()
                                    && remark1.getRemark().getUser().getObjectId() != null) {
                                LocalUser localUser = DBManager.instance().queryUser(remark1.getRemark().getUser().getObjectId());
                                remark1.getRemark().setUser(User.getUser(localUser));
                                stringBuffer1.append(remark1.getUser().getUsername());
                                stringBuffer1.append(" 回复 ").append(localUser.getUsername());
                                stringBuffer1.append(" : ").append(remark1.getContent()).append("\n");
                                ((ItemViewHolder) holder).comment.setText(stringBuffer1.toString());
                            } else {
                                stringBuffer1.append(remark1.getUser().getUsername());
                                stringBuffer1.append(" 回复 ").append(remark1.getRemark().getUser().getUsername());
                                stringBuffer1.append(" : ").append(remark1.getContent()).append("\n");
                                ((ItemViewHolder) holder).comment.setText(stringBuffer1.toString());
                            }
                        }

                    } else {
                        stringBuffer1.append(remark1.getUser().getUsername());
                        stringBuffer1.append(" : ").append(remark1.getContent()).append("\n");
                        ((ItemViewHolder) holder).comment.setText(stringBuffer1.toString());
                    }
                }
            }
        });


    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        ExpandableTextView expandableTextView;
        CircleImageView circleImageView;
        TextView name;
        RecyclerView recyclerView;
        TextView location;
        TextView time;
        TextView like;
        TextView comment;

        public ItemViewHolder(View itemView) {
            super(itemView);
            expandableTextView = itemView.findViewById(R.id.expand_text_view);
            circleImageView = itemView.findViewById(R.id.circle_body_header);
            name = itemView.findViewById(R.id.circle_body_name);
            recyclerView = itemView.findViewById(R.id.circle_body_recyclerview);
            location = itemView.findViewById(R.id.circle_body_location);
            time = itemView.findViewById(R.id.circle_body_time);
            like = itemView.findViewById(R.id.circle_body_like);
            comment = itemView.findViewById(R.id.circle_body_comment);
        }
    }
}
