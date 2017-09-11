package com.mazaiting.popupwindowtest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.Toast;
import com.mazaiting.SPopupWindow;

public class MainActivity extends AppCompatActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
  }

  public void show(View view){
    SPopupWindow.with(this)
        .setView(R.layout.popup_window) // 设置布局
        .show(new SPopupWindow.Callback() {
          @Override public void getView(View view, PopupWindow popupWindow) {
            Toast.makeText(MainActivity.this, "show", Toast.LENGTH_SHORT).show();
          }
        });
  }
}
