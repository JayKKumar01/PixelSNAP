package com.img.pixelsnap.Activties;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.img.pixelsnap.Adaptors.TypeAdaptor;
import com.img.pixelsnap.R;
import com.img.pixelsnap.databinding.ActivityMainBinding;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private String[] titles = {"GALLERY","CAMERA"};
    TypeAdaptor adaptor;
    ActivityMainBinding binding;

    ActivityResultLauncher<Intent> getPermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getPermission = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == MainActivity.RESULT_OK){

                }
            }
        });
        takePerm();
        makeFolder();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        adaptor = new TypeAdaptor(this);
        binding.viewpager.setAdapter(adaptor);
        new TabLayoutMediator(binding.tablayout,binding.viewpager,((tab, position) -> tab.setText(titles[position]))).attach();



    }









    public void create(View view){
         File sdcard = Environment.getExternalStorageDirectory();
         sdcard = new File(sdcard.getAbsolutePath()+"/PixelSnap/");
         File file = new File(sdcard,"1.txt");
         String txt = "Hi";
        makeFolder();
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(file);
            os.write(txt.getBytes());
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Toast.makeText(this, readFile("1.txt"), Toast.LENGTH_SHORT).show();
    }
    public void makeFolder(){
        File sdcard = Environment.getExternalStorageDirectory();
        String folders[] = {"/PixelSnap/"};
        for (int i=0; i<folders.length; i++){
            sdcard = new File(sdcard.getAbsolutePath() + folders[i]);
            if (!sdcard.exists()){
                sdcard.mkdir();
            }
        }
    }

    public String readFile(String rF){
        File sdcard = Environment.getExternalStorageDirectory();
        sdcard = new File(sdcard.getAbsolutePath()+"/PixelSnap/");

        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(sdcard,rF)));
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line);
            }
            br.close();
        } catch (IOException e) { }
        String result = text.toString();

        return result;
    }

    public void takePerm(){
        if (!isPermission()){
            Permission();
        }
    }


    private void Permission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse(String.format("package:%s", getApplicationContext().getPackageName())));
                getPermission.launch(intent);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},101);
        }
    }
    public boolean isPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            return Environment.isExternalStorageManager();
        }
        else {
            int perm = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE);
            return perm == PackageManager.PERMISSION_GRANTED;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length >0){
            if (requestCode == 101){
                boolean r = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (r){

                }
                else {
                    takePerm();
                }
            }
        }
    }
}