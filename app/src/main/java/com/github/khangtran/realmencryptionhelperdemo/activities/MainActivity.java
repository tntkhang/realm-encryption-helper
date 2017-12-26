package com.github.khangtran.realmencryptionhelperdemo.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.khangtran.realmencryptionhelperdemo.R;
import com.github.khangtran.realmencryptionhelperdemo.models.User;
import com.github.khangtran.realmencryptionhelperdemo.realmdao.UserDAO;
import com.github.tntkhang.realmencryptionhelper.RealmEncryptionHelper;

import java.util.Arrays;

import io.realm.Realm;

public class MainActivity extends AppCompatActivity {
    private TextView tvValue;
    private EditText edtInput;
    private Button btnGetName;
    private Button btnSetName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvValue = findViewById(R.id.tv_value);
        btnGetName = findViewById(R.id.btn_get_user);
        btnSetName = findViewById(R.id.btn_save_user);
        edtInput = findViewById(R.id.edt_input);

        btnGetName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Realm realm = Realm.getDefaultInstance();

                User user = UserDAO.getUser(realm);
                if (user != null) {
                    tvValue.setText(user.getName());
                }
                realm.close();
            }
        });
        btnSetName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserDAO.saveUser(Realm.getDefaultInstance(), new User(edtInput.getText().toString()));
                Toast.makeText(MainActivity.this, "Save success.", Toast.LENGTH_SHORT).show();
                edtInput.setText("");
            }
        });
    }
}
