package com.yy.testview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class MyRockerView extends View {
    private final static String TAG = MyRockerView.class.getSimpleName();
    private Context mContext;
    //遥感可移动区域
    private RectF rockerRectF;
    //遥感可移动区域半径
    private float rockerRadius;
    //正方形背景图半径
    private float bgRadius;
    //遥感半径
    private float centerRadius;
    private Bitmap bgBitmap, centerBitmap;
    //背景原点坐标
    private float bgX = 0.0F;
    private float bgY = 0.0F;
    //    摇杆原点坐标
    private float centerX = 0.0F;
    private float centerY = 0.0F;

    //摇杆坐标的回掉监听
    private OnLocaListener onLocaListener;

    //        摇杆初始化，位置是否位于中心或者位置是否位于Y轴
    boolean isInitCenter;

    //        手指离开界面后，是否显示摇杆
    private boolean isShow = false;
    //手指离开界面后，不显示摇杆模式下，是否开启显示
    private boolean isBegin = false;
    //整个view宽高
    private int parentWidth, parentHeight;


    //摇杆初始化，位置是否位于Y轴时,定高模式下，刚开启时和手指离开屏幕时摇杆回到中心点，
    private boolean isDG;

    //        是否重力感应模式
    private boolean isGravity;
    //传感器工具
    private SensorUtil sensorUtil;

    //Y轴代表油门，需要本次结束时记录值，下次按下的初始值不为0
    private float lastY;

    public MyRockerView(Context context) {
        super(context);
        initView(context, null, 0);
    }


    public MyRockerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs, 0);
    }

    public MyRockerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs, defStyleAttr);
    }

    private void initView(Context context, AttributeSet attrs, int defStyleAttr) {
        mContext = context;


        bgBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.play_direct_large_icon);
        bgRadius = bgBitmap.getWidth() / 2;

        centerBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.play_direct_small_icon);
        centerRadius = centerBitmap.getWidth() / 2;

        rockerRadius = bgRadius - centerRadius;

        TypedArray mTypedArray = getContext().obtainStyledAttributes(attrs, R.styleable.MyRockerView,
                defStyleAttr, 0);

//        Drawable mDrawBg = mTypedArray.getDrawable(R.styleable.MyRockerView_control_bg);
//        BitmapDrawable bd = (BitmapDrawable) mDrawBg;
//
//        bgBitmap = bd.getBitmap();
//        bgRadius = bd.getIntrinsicWidth() / 2;
//
//        Drawable mDrawRocker = mTypedArray.getDrawable(R.styleable.MyRockerView_control_rocker);
//        BitmapDrawable bd2 = (BitmapDrawable) mDrawRocker;
//        centerRadius = bd2.getIntrinsicWidth() / 2;
//        centerBitmap = bd2.getBitmap();
//
//        rockerRadius = bgRadius - centerRadius;

//        是否重力感应模式
        isGravity = mTypedArray.getBoolean(R.styleable.MyRockerView_isGravide, false);
//        手指离开界面后，是否显示摇杆
        isShow = mTypedArray.getBoolean(R.styleable.MyRockerView_isShow, false);
//        摇杆初始化，位置是否位于中心
        isInitCenter = mTypedArray.getBoolean(R.styleable.MyRockerView_isInitCenter, true);

        mTypedArray.recycle();


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        parentHeight = MeasureSpec.getSize(heightMeasureSpec);
        if (isShow) {

            onChangeMeasure();

        }
    }

    private void onChangeMeasure() {
        if (isInitCenter) {
            bgX = centerX = parentWidth / 2;
            bgY = centerY = parentHeight / 2;
        } else {
            if (isDG) {
                bgX = centerX = parentWidth / 2;
                bgY = centerY = parentHeight / 2;
            } else {
                bgX = centerX = parentWidth / 2;
                bgY = parentHeight / 2;
                centerY = bgY + (bgRadius - centerRadius);
            }


        }
        rockerRectF = new RectF(bgX - rockerRadius, bgY - rockerRadius, bgX + rockerRadius, bgY + rockerRadius);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isGravity) {
            return super.onTouchEvent(event);
        }
        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "ACTION_DOWN");
                if (isShow) {

                    centerX = event.getX();
                    centerY = event.getY();
                    //手指触摸点位于可移动区域外部时，计算映射的点
                    if (!rockerRectF.contains(centerX, centerY)) {
//                            Log.d(TAG, "外部->" + pointX + "->pointY" + pointY);
                        float dx = centerX - bgX;
                        float dy = centerY - bgY;

                        double touchR = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
                        double cosAngle;
                        if (dy < 0) {
                            if (dx < 0) {
                                dy = -dy;
                                dx = -dx;
                                cosAngle = Math.acos(dx / touchR);
                                centerX = (float) (bgX - rockerRadius * Math.cos(cosAngle));
                                centerY = (float) (bgY - rockerRadius * Math.sin(cosAngle));
                            } else {
                                dy = -dy;
                                cosAngle = Math.acos(dx / touchR);
                                centerX = (float) (bgX + rockerRadius * Math.cos(cosAngle));
                                centerY = (float) (bgY - rockerRadius * Math.sin(cosAngle));
                            }

                        } else {
                            if (dx < 0) {
                                dx = -dx;
                                cosAngle = Math.acos(dx / touchR);
                                centerX = (float) (bgX - rockerRadius * Math.cos(cosAngle));
                                centerY = (float) (bgY + rockerRadius * Math.sin(cosAngle));
                            } else {
                                cosAngle = Math.acos(dx / touchR);
                                centerX = (float) (bgX + rockerRadius * Math.cos(cosAngle));
                                centerY = (float) (bgY + rockerRadius * Math.sin(cosAngle));
                            }
                        }


//                            Log.d(TAG, "改变pointX->" + pointX + "->pointY" + pointY);

                    }

                    setLinstenerData();
                } else {
                    isBegin = true;

                    if (isInitCenter) {
                        bgX = centerX = event.getX();
                        bgY = centerY = event.getY();


                    } else {

                        if (isDG) {
                            //隐藏模式，原点Y轴，定高模式
                            bgX = centerX = event.getX();
                            bgY = centerY = event.getY();
                        } else {
                            //隐藏模式，原点Y轴，非定高模式
                            bgX = centerX = event.getX();

                            centerY = event.getY();
                            bgY = centerY - (bgRadius - centerRadius);
                        }

                    }
                    rockerRectF = new RectF(bgX - rockerRadius, bgY - rockerRadius, bgX + rockerRadius, bgY + rockerRadius);


                }

                break;
            case MotionEvent.ACTION_MOVE:
                Log.d(TAG, "ACTION_MOVE");
                centerX = event.getX();
                centerY = event.getY();

                //手指触摸点位于可移动区域外部时，计算映射的点
                if (!rockerRectF.contains(centerX, centerY)) {
//                            Log.d(TAG, "外部->" + pointX + "->pointY" + pointY);
                    float dx = centerX - bgX;
                    float dy = centerY - bgY;

                    double touchR = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
                    double cosAngle;
                    if (dy < 0) {
                        if (dx < 0) {
                            dy = -dy;
                            dx = -dx;
                            cosAngle = Math.acos(dx / touchR);
                            centerX = (float) (bgX - rockerRadius * Math.cos(cosAngle));
                            centerY = (float) (bgY - rockerRadius * Math.sin(cosAngle));
                        } else {
                            dy = -dy;
                            cosAngle = Math.acos(dx / touchR);
                            centerX = (float) (bgX + rockerRadius * Math.cos(cosAngle));
                            centerY = (float) (bgY - rockerRadius * Math.sin(cosAngle));
                        }

                    } else {
                        if (dx < 0) {
                            dx = -dx;
                            cosAngle = Math.acos(dx / touchR);
                            centerX = (float) (bgX - rockerRadius * Math.cos(cosAngle));
                            centerY = (float) (bgY + rockerRadius * Math.sin(cosAngle));
                        } else {
                            cosAngle = Math.acos(dx / touchR);
                            centerX = (float) (bgX + rockerRadius * Math.cos(cosAngle));
                            centerY = (float) (bgY + rockerRadius * Math.sin(cosAngle));
                        }
                    }


//                            Log.d(TAG, "改变pointX->" + pointX + "->pointY" + pointY);

                }

                setLinstenerData();


                break;
            case MotionEvent.ACTION_UP:
                Log.d(TAG, "ACTION_UP");
                if (isShow) {

                } else {
                    isBegin = false;

                }
                moveBack();
                setLinstenerData();

                if (!isInitCenter) {
                    //Y轴油门需要记住本次离开时的值，为下次ACTION_DOWN时不为0
                    lastY = centerY;
                }
                break;
        }
        postInvalidate();
        return true;
    }

    //返回原点
    private void moveBack() {

        centerX = bgX;
        if (isInitCenter) {
            //回到中心
            centerY = bgY;
        } else {
//          回到Y轴
            if (isDG) {
                //定高模式，回到中心
                centerY = bgY;
            }
        }
    }

    private void setLinstenerData() {

        int xValue = (int) ((centerX - bgX) * 128 / rockerRadius + 0.5);
        int yValue = (int) ((bgY - centerY) * 128 / rockerRadius + 0.5);
        if (onLocaListener != null) {
            onLocaListener.getLocation(xValue, yValue);
        }



    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d(TAG, "onDraw-->isShow"+isShow);
        if (isShow) {
            //画背景
            canvas.drawBitmap(bgBitmap, null, new RectF(bgX - bgRadius, bgY - bgRadius, bgRadius + bgX, bgRadius + bgY), null);
            // 画摇杆
            canvas.drawBitmap(centerBitmap, null, new RectF(centerX - centerRadius, centerY - centerRadius, centerX + centerRadius, centerY + centerRadius), null);

        } else {
            if (isBegin) {

                //画背景
                canvas.drawBitmap(bgBitmap, null, new RectF(bgX - bgRadius, bgY - bgRadius, bgRadius + bgX, bgRadius + bgY), null);
                // 画摇杆
                canvas.drawBitmap(centerBitmap, null, new RectF(centerX - centerRadius, centerY - centerRadius, centerX + centerRadius, centerY + centerRadius), null);
            }

        }

    }


    public interface OnLocaListener {
        /**
         * @param x 方向偏移百分比
         * @param y 方向偏移百分比
         */
        void getLocation(int x, int y);
    }

    public void setOnLocaListener(OnLocaListener onLocaListener) {
        this.onLocaListener = onLocaListener;
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        if (isShow) {
            if (!show) {
                //显示————》  隐藏
                isShow = show;
                isBegin = false;
                postInvalidate();
            }
        } else {
            // 隐藏————》 显示
            if (show) {
                isShow = show;

                onChangeMeasure();
                postInvalidate();
            }

        }
    }

    public boolean isDG() {
        return isDG;
    }

    public void setDG(boolean DG) {

        if (isDG) {
            if (!DG) {
                //定高--》非定高
                isDG = DG;
//                postInvalidate();
            }

        } else {
            if (DG) {
                //非定高--》定高
                isDG = DG;
                centerY = bgY;
                postInvalidate();
                setLinstenerData();
            }
        }
    }

    public boolean isGravity() {
        return isGravity;
    }

    public void registerListener() {
        if (sensorUtil != null) {
            sensorUtil.registerListener();
        }
    }

    public void unregisterListener() {
        if (sensorUtil != null) {
            sensorUtil.unRegisterListener();
        }
    }

    public void setGravity(boolean gravity) {

        if (isGravity) {
            //重力感应 开————》关
            if (!gravity) {
                isGravity = gravity;

                if (sensorUtil != null) {
                    sensorUtil.unRegisterListener();
                    sensorUtil = null;
                }

            }
        } else {
            //重力感应 关————》开
            if (gravity) {
                isGravity = gravity;
                setShow(true);
                sensorUtil = new SensorUtil(getContext(), new SensorUtil.OnChangeListener() {
                    @Override
                    public void setXY(float dx, float dy) {
                        Log.d("sensor", dx + "--" + dy);
                        centerX = dx * rockerRadius + bgX;
                        centerY = dy * rockerRadius + bgY;
                        postInvalidate();
                        setLinstenerData();

                    }
                });
                sensorUtil.registerListener();

            }
        }
    }
}
