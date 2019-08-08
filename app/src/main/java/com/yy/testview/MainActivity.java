package com.yy.testview;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import notchtools.geek.com.notchtools.NotchTools;
import notchtools.geek.com.notchtools.core.NotchProperty;
import notchtools.geek.com.notchtools.core.OnNotchCallBack;

public class MainActivity extends AppCompatActivity implements OnNotchCallBack {
    private TextView leftgTV, rightTV;
    private MyRockerView mRightRocker, mLeftRocker;
    private CheckBox cbShow, cbDG, cbGravity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        NotchTools.getFullScreenTools().fullScreenUseStatusForActivityOnCreate(this, this);
        leftgTV = findViewById(R.id.leftgTV);
        rightTV = findViewById(R.id.rightTV);
        mRightRocker = findViewById(R.id.mRightRocker);
        mLeftRocker = findViewById(R.id.mLeftRocker);
        mRightRocker.setOnLocaListener(new MyRockerView.OnLocaListener() {
            @Override
            public void getLocation(int x, int y) {
                rightTV.setText("x=" + x + "--->" + "y=" + y);
            }
        });
        mLeftRocker.setOnLocaListener(new MyRockerView.OnLocaListener() {
            @Override
            public void getLocation(int x, int y) {
                leftgTV.setText("x=" + x + "--->" + "y=" + y);
            }
        });
        cbShow = findViewById(R.id.cbShow);
        cbShow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                mLeftRocker.setShow(b);
                if (b) {
                    cbShow.setText("显示");
                } else {
                    cbShow.setText("隐藏");
                }
                if (mRightRocker.isGravity()) {
                    Toast.makeText(MainActivity.this, "重力感应模式下不可隐藏", Toast.LENGTH_SHORT).show();
                    return;
                }
                mRightRocker.setShow(b);
                if (b) {
                    cbShow.setText("显示");
                } else {
                    cbShow.setText("隐藏");
                }
            }
        });
        cbDG = findViewById(R.id.cbDG);
        cbDG.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                mLeftRocker.setDG(b);

                if (b) {
                    cbDG.setText("定高");
                } else {
                    cbDG.setText("非定高");
                }
            }
        });
        cbGravity = findViewById(R.id.cbGravity);
        cbGravity.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                mRightRocker.setGravity(b);

                if (b) {
                    cbGravity.setText("重力已开启");
                } else {
                    cbGravity.setText("重力已关闭");
                }
            }
        });
    }

    @Override
    public void onNotchPropertyCallback(NotchProperty notchProperty) {

    }

    /**
     * onWindowFocusChanged最好也进行全屏适配，防止失去焦点又重回焦点时的flag不正确。
     *
     * @param hasFocus
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus) {
            NotchTools.getFullScreenTools().fullScreenUseStatusForOnWindowFocusChanged(this);
        }
        super.onWindowFocusChanged(hasFocus);
    }
}
