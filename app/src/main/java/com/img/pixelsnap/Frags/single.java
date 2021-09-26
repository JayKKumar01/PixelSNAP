package com.img.pixelsnap.Frags;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.os.Handler;
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
import com.img.pixelsnap.databinding.FragmentSingleBinding;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class single extends Fragment {
    View view;
    ActivityResultLauncher<String> getImage;
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
        view = inflater.inflate(R.layout.fragment_single, container, false);
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

        getImage = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                imgOrginal.setImageURI(imgUri = result);
                //handler.postDelayed(run,500);


            }
        });
        imgOrginal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getImage.launch("image/*");
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
    Handler handler = new Handler();
    Runnable run = new Runnable() {
        @Override
        public void run() {
            getSize();
        }
    };

    private void getSize() {
        try {
                    dialog.show();
                    Bitmap bit = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(),imgUri);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bit.compress(Bitmap.CompressFormat.JPEG,90,baos);
                    byte[] bytes = baos.toByteArray();
                    String str = "Original Image : "+ Formatter.formatFileSize(getContext(),bytes.length);
                    sizeOrignal.setText(str);
                    dialog.dismiss();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
        if(w.isEmpty() || h.isEmpty() || q.isEmpty() || !(imgUri != null)){
            Toast.makeText(getContext(), "Set Resolution and Quality both", Toast.LENGTH_SHORT).show();
        }
        else {
            width = Integer.parseInt(w);
            height = Integer.parseInt(h);
            qual = Integer.parseInt(q);

            Glide.with(getContext()).asBitmap()
                    .override(width,height)
                    .centerCrop()
                    .load(imgUri)
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