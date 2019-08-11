package com.example.homework5122;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_CODE_PERMISSION_READ_STORAGE = 10; //пользовательская переменная, определяем права доступа на чтение

    private Button btnSetting;
    private LinearLayout activitySettings;
    private Button btnApply;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        //обработчик открытия настроек
        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activitySettings.setVisibility(View.VISIBLE);
                btnSetting.setVisibility(View.GONE);
            }
        });
        //применяем настройки и устанавливаем изображение в ivBackground
        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //текущий статус разрешения на чтение READ_EXTERNAL_STORAGE
                int permissionStatus = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
                if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
                    //доступ разрешен, загружаем картинку
                    LoadImg();
                } else {
                    //еше не было проверки, запрашиваем доступ
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION_READ_STORAGE);
                }
                //скрывем окно настроек
                activitySettings.setVisibility(View.GONE);
                btnSetting.setVisibility(View.VISIBLE);
            }
        });
    }

    private void initView() {
        btnSetting = findViewById(R.id.btn_setting);
        activitySettings = findViewById(R.id.activity_settings);
        btnApply = findViewById(R.id.btn_apply);
    }

    //загрузка изображения
    public void LoadImg() {
        ImageView ivBackground = findViewById(R.id.iv_background);
        EditText etFilename = findViewById(R.id.et_filename);
        String image_src = etFilename.getText().toString();

        if (isExternalStorageReadable()) {
            File file_src = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), image_src);
            Bitmap image = BitmapFactory.decodeFile(file_src.getAbsolutePath());
            ivBackground.setImageBitmap(image);
            etFilename.setText("");
        } else {
            Toast.makeText(this, getResources().getString(R.string.msg_file_error), Toast.LENGTH_LONG).show();
        }
    }

    //проверяем есть ли доступ для чтения из внешнего хранилища
    private boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION_READ_STORAGE: //пользовательская переменная, доступ на чтение
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    LoadImg();
                } else {
                    //request denied
                    Toast.makeText(this, getResources().getString(R.string.msg_request_denied), Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
}
