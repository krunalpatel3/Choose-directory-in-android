package com.krunal.choosedirectory.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.krunal.choosedirectory.databinding.ActivityMainBinding;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

import static com.krunal.choosedirectory.ClsGlobal.ClsGlobal.SELECT_DIRECTORY;
import static com.krunal.choosedirectory.ClsGlobal.ClsGlobal.requestPermission;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        requestPermission(MainActivity.this);

        binding.btnSelectDirectory.setOnClickListener(v -> {
            Intent i = new Intent(this, SelectDirectoryActivity.class);
            startActivityForResult(i, SELECT_DIRECTORY);
        });


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode,
                permissions, grantResults, this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == SELECT_DIRECTORY && data != null) {

            if (data != null){
                binding.tvDrSelected.setText(data.getStringExtra("SelectedPath"));
            }


        }

        super.onActivityResult(requestCode, resultCode, data);

    }

}
