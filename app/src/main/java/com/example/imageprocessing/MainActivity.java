package com.example.imageprocessing;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import org.jetbrains.annotations.NotNull;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener
{

    private static final int REQUEST_GPS = 1;
    private ImageView img1;

    private Mat mat1;
    private Mat mat2;
    private Bitmap bitmap, mBitmap;

    private float hue = 0, saturation = 1f, lum = 1f;
    private static final int MID_VALUE = 128;
    private ActivityResultLauncher<Intent> activityResultLauncher;

    private LinearLayout actionbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StatusBarUtill.transparencyBar(this);
        int actionBarHeight = getActionBarHeight();
        setHeight(actionBarHeight);

        img1 = findViewById(R.id.imageView);
        SeekBar huesk = findViewById(R.id.SeekbarHue);
        SeekBar saturationsk = findViewById(R.id.SeekbarSaturation);
        SeekBar lumsk = findViewById(R.id.SeekbarLum);

        //mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.icon);
        //bitmap = Bitmap.createBitmap(mBitmap);
        initLoadOpenCv();
        Button bt01 = findViewById(R.id.open);//打开图片
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        String str = result.getData().getStringExtra("data");
                        Toast.makeText(MainActivity.this, str, Toast.LENGTH_SHORT).show();
                    }
                });
        bt01.setOnClickListener(v -> {
            Intent i = new Intent();
            i.setType("image/*");
            i.setAction(Intent.ACTION_GET_CONTENT);
            //startActivityForResult(i,1);
            activityResultLauncher.launch(i);
        });

        //bitmap = Bitmap.createBitmap(mBitmap);



        Button bt03 = findViewById(R.id.save);//保存图片
        bt03.setOnClickListener(v -> {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS}, REQUEST_GPS);
        });




        //bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.icon);
        //bitmap = mBitmap;

        huesk.setOnSeekBarChangeListener(this);
        saturationsk.setOnSeekBarChangeListener(this);
        lumsk.setOnSeekBarChangeListener(this);

        mat1 = new Mat();
        mat2 = new Mat();


        //灰度化
        Button bt1 = findViewById(R.id.button1);//灰度
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap tembt = ((BitmapDrawable)img1.getDrawable()).getBitmap();
                Utils.bitmapToMat(tembt, mat1);
                Imgproc.cvtColor(mat1, mat2, Imgproc.COLOR_BGR2GRAY);
                Utils.matToBitmap(mat2, tembt);

                img1.setImageBitmap(tembt);
                mat1.release();
                mat2.release();
            }
        });

        //膨胀操作
        Button bt2 = findViewById(R.id.button2);//膨胀
        bt2.setOnClickListener(v -> {
            Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(10, 10));
            Bitmap tembt = ((BitmapDrawable)img1.getDrawable()).getBitmap();
            Utils.bitmapToMat(tembt,mat1);
            Imgproc.dilate(mat1, mat2, element);
            Utils.matToBitmap(mat2, tembt);

            img1.setImageBitmap(tembt);
            mat1.release();
            mat2.release();
        });

        //腐蚀操作
        Button bt3 = findViewById(R.id.button3);
        bt3.setOnClickListener(v -> {
            Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(10,10));
            Bitmap tembt = ((BitmapDrawable)img1.getDrawable()).getBitmap();
            Utils.bitmapToMat(tembt, mat1);
            Imgproc.erode(mat1, mat2, element);
            Utils.matToBitmap(mat2, tembt);
            img1.setImageBitmap(tembt);
            mat1.release();
            mat2.release();
        });

        //中值滤波
        Button bt4 = findViewById(R.id.button4);
        bt4.setOnClickListener(v -> {
            Bitmap tembt = ((BitmapDrawable)img1.getDrawable()).getBitmap();
            Utils.bitmapToMat(tembt, mat1);
            Imgproc.medianBlur(mat1, mat2, 77);
            Utils.matToBitmap(mat2, tembt);

            img1.setImageBitmap(tembt);
            mat1.release();
            mat2.release();
        });

        //高斯模糊
        Button bt5 = findViewById(R.id.button5);
        bt5.setOnClickListener(v -> {
            Bitmap tembt = ((BitmapDrawable)img1.getDrawable()).getBitmap();
            Utils.bitmapToMat(tembt, mat1);
            Imgproc.GaussianBlur(mat1, mat2, new Size(77,77), 5, 5);
            Utils.matToBitmap(mat2, tembt);

            img1.setImageBitmap(tembt);
            mat1.release();
            mat2.release();
        });

        //边缘检测
        Button bt6 = findViewById(R.id.button6);
        bt6.setOnClickListener(v -> {
            Bitmap tembt = ((BitmapDrawable)img1.getDrawable()).getBitmap();
            Utils.bitmapToMat(tembt, mat1);
            Mat gray = new Mat();
            Imgproc.cvtColor(mat1, gray, Imgproc.COLOR_BGR2GRAY);//灰度化
            mat2 = mat1.clone();
            Imgproc.Canny(mat1, mat2, 75, 200);
            Utils.matToBitmap(mat2, tembt);

            img1.setImageBitmap(tembt);
            mat1.release();
            mat2.release();
            gray.release();
        });

        Button bt02 = findViewById(R.id.origin);//显示原图
        bt02.setOnClickListener(v -> {
            bitmap = Bitmap.createBitmap(mBitmap);
            img1.setImageBitmap(bitmap);
        });
    }

    private void setHeight(int actionBarHeight){
        actionbar = findViewById(R.id.ll_actionBar);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)actionbar.getLayoutParams();
        params.height = actionBarHeight;
        actionbar.setLayoutParams(params);
    }

    private int getActionBarHeight(){
        int result = 0;
        int identifier = getResources().getIdentifier("status_bar_height","dimen","android");
        if(identifier>0){
            result = getResources().getDimensionPixelOffset(identifier);
        }
        return result;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            assert data != null;
            Uri uri =data.getData();
            Log.e("uri", uri.toString());
            ContentResolver cr = this.getContentResolver();
            try {
                bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                mBitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                img1.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }else{
            Log.i("MainActivity", "operation error");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
            saveBitmap();
        }else{

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void saveBitmap(){
        File sd = Environment.getExternalStorageDirectory();
        boolean can_write = sd.canWrite();
        File f = new File(Environment.getExternalStorageDirectory() +"/icon.jpg");
        if (f.exists()){
            f.delete();
        }
        try{
            FileOutputStream out = new FileOutputStream(f);
            mBitmap = ((BitmapDrawable)img1.getDrawable()).getBitmap();
            if (mBitmap == null){
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            }else{
                mBitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            }
            out.flush();
            out.close();
            Uri uri = Uri.fromFile(f);
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,uri));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initLoadOpenCv(){
        boolean success= OpenCVLoader.initDebug();
        if(success){
            Toast.makeText(this.getApplicationContext(), "Loading Opencv Libraries", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this.getApplicationContext(), "WARNING: COULD NOT LOAD Opencv Library", Toast.LENGTH_SHORT).show();
        }
    }
/*
    private void showOriginal(){
        img1.setImageBitmap(bitmap);
    }
*/
    @SuppressLint("NonConstantResourceId")
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()){
            case R.id.SeekbarHue:
                hue = (progress - MID_VALUE)*1f/MID_VALUE*180;
                break;
            case R.id.SeekbarSaturation:
                saturation = progress * 1f /MID_VALUE;
                break;
            case R.id.SeekbarLum:
                lum = progress*1f/MID_VALUE;
                break;
        }

        Bitmap bp = ImageHelper.getChangedBitmap(bitmap, hue,saturation,lum);
        img1.setImageBitmap(bp);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}