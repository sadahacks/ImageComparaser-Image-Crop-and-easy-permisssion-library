package com.example.myapp.image_picker;

import android.content.Intent;

import java.io.File;

public interface ImageAttachentCallback {
    void onSuccess(File file);
    void onFailure(String error);
    void onClick(Intent intent, int id);
}
