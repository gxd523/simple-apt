package com.gxd.apt;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.gxd.apt.annotation.BindView;
import com.gxd.apt.annotation.DIActivity;

@DIActivity
public class MainActivity extends Activity {
    @BindView(R.id.text_view)
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MainActivity_ViewBinding.bind(this);

        textView.setText("成功使用注解处理器!");
    }
}
