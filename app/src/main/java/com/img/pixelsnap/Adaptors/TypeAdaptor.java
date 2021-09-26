package com.img.pixelsnap.Adaptors;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.img.pixelsnap.Frags.batch;
import com.img.pixelsnap.Frags.single;

public class TypeAdaptor extends FragmentStateAdapter {
    private String[] titles = {"GALLERY","CAMERA"};

    public TypeAdaptor(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new single();
            case 1:
                return new batch();
        }
        return new single();
    }

    @Override
    public int getItemCount() {
        return titles.length;
    }
}