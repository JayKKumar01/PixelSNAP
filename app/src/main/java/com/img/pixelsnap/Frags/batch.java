package com.img.pixelsnap.Frags;

import static android.app.Activity.RESULT_OK;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.img.pixelsnap.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class batch extends Fragment {
    View view;
    ActivityResultLauncher<Intent> getImage;
    Uri imgUri;
    ImageView imgOrginal, imgNew;
    int width, height, qual;
    TextView compress, sizeOrignal, sizeNew,save;
    EditText res1, res2, quality,rename;
    Bitmap bitmap = null;
    byte[] finalByte = null;
    ProgressDialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_batch, container, false);
        imgOrginal = view.findViewById(R.id.orinalImage);
        imgNew = view.findViewById(R.id.newImage);
        compress = view.findViewById(R.id.Compress);
        res1 = view.findViewById(R.id.res1);
        res2 = view.findViewById(R.id.res2);
        quality = view.findViewById(R.id.quality);
        sizeOrignal = view.findViewById(R.id.txtOriginalImage);
        sizeNew = view.findViewById(R.id.txtNewImage);
        rename = view.findViewById(R.id.rename);
        save = view.findViewById(R.id.save);
        dialog = new ProgressDialog(getContext());
        dialog.setCancelable(false);
        dialog.setMessage("loading image...");


        getImage = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getData() != null){
                    Bundle bundle = result.getData().getExtras();
                    bitmap = (Bitmap) bundle.get("data");
                    imgOrginal.setImageBitmap(bitmap);
//                    try {
//                    dialog.show();
//                    Bitmap bit = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(),imgUri);
//                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                    bit.compress(Bitmap.CompressFormat.PNG,0,baos);
//                    byte[] bytes = baos.toByteArray();
//                    String str = "Original Image : "+ Formatter.formatFileSize(getContext(),bytes.length);
//                    sizeOrignal.setText(str);
//                    dialog.dismiss();
//
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                }

            }
        });

        imgOrginal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getContext().getPackageManager()) != null){
                    getImage.launch(intent);
                }
                else {
                    Toast.makeText(getContext(), "Can't access Camera", Toast.LENGTH_SHORT).show();
                }
            }
        });

        compress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                compressImage();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveImage();
            }
        });





        return view;
    }

    private void saveImage() {
        String str = rename.getText().toString();
        if (str.isEmpty()){
            str = System.currentTimeMillis()+".jpg";
        }
        else {
            str += ".jpg";
        }

        File sdcard = Environment.getExternalStorageDirectory();
        sdcard = new File(sdcard.getAbsolutePath() + "/PixelSnap/");
        File file = new File(sdcard,str);

        try {
            FileOutputStream fileOutput = new FileOutputStream(file);
            fileOutput.write(finalByte);
            fileOutput.close();
            Toast.makeText(getContext(), "Image Saved !", Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void compressImage() {
        String w = res1.getText().toString();
        String h = res2.getText().toString();
        String q = quality.getText().toString();
        if(w.isEmpty() || h.isEmpty() || q.isEmpty() || !(bitmap != null)){
            Toast.makeText(getContext(), "Set Resolution and Quality both", Toast.LENGTH_SHORT).show();
        }
        else {
            width = Integer.parseInt(w);
            height = Integer.parseInt(h);
            qual = Integer.parseInt(q);

            Glide.with(getContext()).asBitmap()
                    .override(width,height)
                    .centerCrop()
                    .load(bitmap)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            resource.compress(Bitmap.CompressFormat.JPEG,qual,baos);
                            byte[] bytes = baos.toByteArray();
                            finalByte = bytes;
                            bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                            imgNew.setImageBitmap(bitmap);

                            String str = "New Image : "+ Formatter.formatFileSize(getContext(),bytes.length);
                            sizeNew.setText(str);
                            save.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {

                        }
                    });
        }
    }


}