package com.zhangteng.xim.activity;

import android.content.Intent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhangteng.swiperecyclerview.widget.CircleImageView;
import com.zhangteng.xim.R;
import com.zhangteng.xim.base.BaseActivity;
import com.zhangteng.xim.db.DBManager;
import com.zhangteng.xim.db.bean.LocalUser;
import com.zhangteng.xim.utils.StringUtils;

import butterknife.BindView;

/**
 * Created by swing on 2018/5/28.
 */
public class FriendInfoActivity extends BaseActivity {
    @BindView(R.id.iv_avatar)
    CircleImageView circleImageView;
    @BindView(R.id.tv_name)
    TextView realName;
    @BindView(R.id.iv_sex)
    ImageView sex;
    @BindView(R.id.tv_accout)
    TextView username;
    @BindView(R.id.tv_region)
    TextView area;
    @BindView(R.id.tv_sign)
    TextView sign;
    @BindView(R.id.img_photo1)
    ImageView photo1;
    @BindView(R.id.img_photo2)
    ImageView photo2;
    @BindView(R.id.img_photo3)
    ImageView photo3;
    @BindView(R.id.tv_photo_temp)
    TextView photos;
    @BindView(R.id.btn_sendmsg)
    Button send;
    private String objectId;
    private LocalUser user;

    @Override
    protected int getResourceId() {
        return R.layout.activity_friend_info;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        if (intent.hasExtra("objectId")) {
            objectId = getIntent().getStringExtra("objectId");
        }
        user = DBManager.instance().queryUser(objectId);
        realName.setText(StringUtils.isNotEmpty(user.getRealName()) ? user.getRealName() : user.getUsername());
        sex.setImageResource(user.getSex() == 0 ? R.mipmap.ic_sex_male : R.mipmap.ic_sex_female);
        username.setText(String.format("XIM账号：%s", user.getUsername()));
        area.setText("");

    }
}