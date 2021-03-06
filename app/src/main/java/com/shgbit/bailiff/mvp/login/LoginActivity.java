package com.shgbit.bailiff.mvp.login;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.shgbit.bailiff.R;
import com.shgbit.bailiff.base.baseImpl.BaseActivity;
import com.shgbit.bailiff.common.ErrorMessage;
import com.shgbit.bailiff.config.Constants;
import com.shgbit.bailiff.mvp.BailiffActivity;
import com.shgbit.bailiff.mvp.court.SelectCourtActivity;
import com.shgbit.bailiff.util.PLog;
import com.shgbit.bailiff.util.PermissionsUtils;

import java.util.WeakHashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * @author:xushun on 2018/7/8
 * description :登录
 */

public class LoginActivity extends BaseActivity<LoginPresenter> implements LoginContact.OnLoginView {
    @BindView(R.id.imageView3)
    ImageView imageView3;
    @BindView(R.id.iv_tag)
    ImageView ivTag;
    @BindView(R.id.get_fayuan)
    TextView getFayuan;
    @BindView(R.id.username)
    EditText username;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.login_btn1)
    TextView loginBtn1;
    @BindView(R.id.tv_video)
    TextView tvVideo;
    @BindView(R.id.tv_devices)
    TextView tvDevices;
    @BindView(R.id.cb_remember_pwd)
    CheckBox cbRememberPwd;
    private String userCode, crop, device, upw;
    private final static String TAG = LoginActivity.class.getSimpleName();
    @BindView(R.id.login_btn)
    TextView loginBtn;
    private Unbinder bind;
//    private Observer<String> observer = new Observer<String>() {
//        @Override
//        public void onChanged(String s) {
//
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity_layout);
        bind = ButterKnife.bind(this);
        username.setText("zhouxiannong");
        password.setText("1");
        PLog.i("onCreate");
        initPermissons();
    }

    /**
     * 动态获取权限
     */
    private void initPermissons() {
        String[] permissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE, Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CALL_PHONE, Manifest.permission.CAMERA, Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.ACCESS_NETWORK_STATE
        };
//        PermissionsUtils.showSystemSetting = false;//是否支持显示系统设置权限设置窗口跳转
        //创建监听权限的接口对象
        PermissionsUtils.IPermissionsResult permissionsResult = new PermissionsUtils.IPermissionsResult() {
            @Override
            public void passPermissons() {

            }

            @Override
            public void forbitPermissons() {
//                Intent intent = new Intent(mContext, LoginActivity.class);
//                startActivity(intent);
//                finish();

            }
        };
        //这里的this不是上下文，是Activity对象！
        PermissionsUtils.getInstance().chekPermissions(this, permissions, permissionsResult);
    }

    @Override
    public LoginPresenter initPresenter() {
        return new LoginPresenter(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionsUtils.getInstance().onRequestPermissionsResult(this, requestCode, permissions, grantResults);

    }


    /**
     * 点击登录
     */
    @OnClick(R.id.login_btn)
    public void onLogin() {
        final WeakHashMap<String, Object> params = new WeakHashMap<>();
        userCode = "zhouxiannong";
        crop = "3267";
        device = "861243043273741";
        upw = password.getText().toString();
        params.put("userCode", userCode);
        params.put("crop", crop);
        params.put("device", device);
        params.put("upw", upw);
        mvpPresenter.getData(params);

        Intent intent = new Intent(this, BailiffActivity.class);
        startActivity(intent);
    }

    @Override
    public void onSucess(String data) {
        //TODO: save passWord； start  BailiffActivity
        showMessage(data);
//        Toast.makeText(context, "登录成功", Toast.LENGTH_SHORT).show();
//        LawUtils.getPreference().saveString(Constants.USER_CODE, userCode);
//        LawUtils.getPreference().saveString(Constants.CROP, crop);
//        LawUtils.getPreference().saveString(Constants.DEVICE, device);
//        LawUtils.getPreference().saveString(Constants.UPW, upw);
       
    }

    @Override
    public void onPasswordError(ErrorMessage error) {
        showMessage(error.errorMessage);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bind != null) {
            bind.unbind();
        }
    }

    @OnClick(R.id.get_fayuan)
    public void getFayuan() {
        Intent intent = new Intent(this, SelectCourtActivity.class);
        startActivityForResult(intent, Constants.COURT_SELSET_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == Constants.COURT_SELSET_CODE) {
                if (data.hasExtra("courtName")) {
                    String courtName = data.getStringExtra("courtName");
                    String courtId = data.getStringExtra("courtId");
                    getFayuan.setText(courtName);
                }
            }
        }
    }
}
