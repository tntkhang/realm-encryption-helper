package com.github.khangtran.realmencryptionhelperdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.tntkhang.realmencryptionhelper.RealmEncryptionHelper;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private TextView tvValue;
    private Button btnGetKey;
    private RealmEncryptionHelper realmEncryptionHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvValue = findViewById(R.id.tv_value);
        btnGetKey = findViewById(R.id.btn_get_key);

        realmEncryptionHelper = RealmEncryptionHelper.getInstance(this, getString(R.string.app_name));


        btnGetKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvValue.setText(Arrays.toString(realmEncryptionHelper.getEncryptKey()));
            }
        });
    }
}
