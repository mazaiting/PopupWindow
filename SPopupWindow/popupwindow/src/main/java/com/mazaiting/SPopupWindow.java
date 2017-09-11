package com.mazaiting;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.PopupWindow;

/**
 * Created by mazaiting on 2017/8/30.
 * API文档
 * with(Activity activity)	初始化
 * show(Callback callback)	弹出PopupWindow
 * setView(int ResId)	设置视图的资源文件
 * setLocation(int Lacation)	设置弹出位置
 * setBackgroundAlpha(float bgAlpha)	设置黑色背景透明度
 * setAnimationStyle(int animationStyle)	设置动画的资源文件
 * setAutoPopupInput(boolean isShowInput)	设置是否自动弹出软键盘
 * use the library:
 *         SPopupWindow.with(this)
                       .setView(R.layout.popup_window) // 设置布局
                       .show(new SPopupWindow.Callback() {
                          @Override public void getView(View view, PopupWindow popupWindow) {
                          Toast.makeText(MainActivity.this, "show", Toast.LENGTH_SHORT).show();
                        }
                      });
 */
/**
 * 简化的PopupWindow
 */
public class SPopupWindow {
  /**
   * PopupWindow从底部出现
   */
  public static final int BOTTOM = 0x01;
  /**
   * PopupWindow从顶部出现
   */
  public static final int TOP = 0x02;
  /**
   * PopupWindow在任意位置出现
   */
  public static final int OTHER = 0x03;
  /**
   * 显示位置
   */
  private int mLocation = BOTTOM;
  /**
   * 当mLocation = OTHER时，必须设置Point,
   * 此值为当前PopupWindow显示的位置
   */
  private Point mLocationPoint = null;
  /**
   * 上下文
   */
  private Context mContext = null;
  /**
   * 显示的View视图
   */
  private View mView = null;
  /**
   * 是否显示输入法
   */
  private boolean mIsShowInput = false;
  /**
   * 动画ID，默认为从上到下的平移
   */
  private int mAnimationStyle = R.style.anim_translate_bottom_popup_window;
  /**
   * 背景透明度，默认值为0.4f
   */
  private float mBackgroundAlpha = 0.4f;

  /**
   * 构造方法
   * @param context 设备上下文
   */
  private SPopupWindow(Context context) {
    mContext = context;
  }

  /**
   * 传入设备上下文
   * @param context 设备上下文
   * @return
   */
  public static SPopupWindow with(Context context){
    return new SPopupWindow(context);
  }

  /**
   * 设置显示位置，默认从底部弹出
   * @param location 位置
   * @return
   */
  public SPopupWindow setLocation(int location){
    this.mLocation = location;
    switch (mLocation){
      case OTHER:
      case BOTTOM:
        mAnimationStyle = R.style.anim_translate_bottom_popup_window;
        break;
      case TOP:
        mAnimationStyle = -1;
        break;
    }
    return this;
  }

  /**
   * 设置视图资源
   * @param resId
   * @return
   */
  public SPopupWindow setView(int resId){
    mView = LayoutInflater.from(mContext).inflate(resId, null);
    return this;
  }

  /**
   * 显示软键盘
   */
  private void showInput(){
    if (mIsShowInput){
      new Handler().postDelayed(new Runnable() {
        @Override public void run() {
          InputMethodManager imm =
              (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
          imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        }
      },0);
    }
  }

  /**
   * 设置是否自动弹出软件盘，默认为否
   * @param isShowInput
   * @return
   */
  public SPopupWindow setAutoPopupInput(boolean isShowInput){
    this.mIsShowInput = isShowInput;
    return this;
  }

  /**
   * 设置进出动画
   * @param animationStyle 动画id
   * @return
   */
  public SPopupWindow setAnimationStyle(int animationStyle){
    this.mAnimationStyle = animationStyle;
    return this;
  }

  /**
   * 设置背景透明度
   */
  public void setBackgroundAlpha(){
    setBackgroundAlpha(mBackgroundAlpha);
  }

  /**
   * 设置背景透明度，默认设置为0.4f
   * @param bgAlpha 背景透明度
   * @return
   */
  public SPopupWindow setBackgroundAlpha(float bgAlpha){
    if (bgAlpha > 1.0 || bgAlpha < 0.0){
      throw new RuntimeException("Alpha set error!");
    }
    Activity activity = (Activity) mContext;
    WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
    lp.alpha = bgAlpha;
    if (bgAlpha == 1){
      // 不移除该Flag的话,在有视频的页面上的视频会出现黑屏的bug
      activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    } else {
      // 此行代码主要是解决在华为手机上半透明效果无效的bug
      activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }
    activity.getWindow().setAttributes(lp);
    return this;
  }

  /**
   * 设置PopupWindow显示的位置
   * @param point 显示的位置坐标
   */
  public SPopupWindow setLocationPoint(Point point) {
    this.mLocationPoint = point;
    return this;
  }

  /**
   * 弹出PopupWindow
   */
  public void show(Callback callback){
    if (mView == null){
      throw new RuntimeException("View is not set!");
    }
    Activity activity = (Activity) mContext;
    PopupWindow window = new PopupWindow(mView, ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT, true);
    if (mAnimationStyle == -1){
      throw new RuntimeException("Animation style is not set.");
    }
    // 设置动画
    window.setAnimationStyle(mAnimationStyle);
    // 设置背景Drawable
    window.setBackgroundDrawable(new ColorDrawable());
    // 设置背景透明度
    setBackgroundAlpha();
    // 设置不会被软键盘覆盖
    window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    // 弹窗消失时恢复背景色
    window.setOnDismissListener(new PopupWindow.OnDismissListener() {
      @Override public void onDismiss() {
        setBackgroundAlpha(1.0f);
      }
    });

    // 设置回调
    callback.getView(mView, window);

    //if (mLocation != -1){
    //  window.showAtLocation(activity.getWindow().getDecorView().findViewById(
    //      android.R.id.content), mLocation, 0, 0);
    //}
    if (mLocation == BOTTOM){
      window.showAtLocation(activity.getWindow().getDecorView().findViewById(
          android.R.id.content), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    } else if (mLocation == TOP){
      window.showAtLocation(activity.getWindow().getDecorView().findViewById(
          android.R.id.content), Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
    } else if (mLocation == OTHER){

    }
    showInput();
  }

  /**
   * 回到接口
   */
  public interface Callback{
    void getView(View view, PopupWindow popupWindow);
  }

}
