package com.krunal.choosedirectory.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import com.google.gson.Gson;
import com.krunal.choosedirectory.Adapter.DirectoryAdapter;
import com.krunal.choosedirectory.Fragments.FolderFragment;
import com.krunal.choosedirectory.Fragments.StorageFragment;
import com.krunal.choosedirectory.R;
import com.krunal.choosedirectory.databinding.ActivitySelectDirectoryBinding;
import com.krunal.choosedirectory.databinding.ToolbarBinding;
import com.krunal.choosedirectory.models.ToolBar;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class SelectDirectoryActivity extends AppCompatActivity {

    private ActivitySelectDirectoryBinding main_binding;
    private ToolbarBinding toolbarBinding;
    private ToolBar toolBar;
    //    private DirectoryAdapter adapter;
    private String rootPath;
    StorageFragment storageFragment;
    private static final String PARAMETER_FOLDER_PATH = "folder.path";
    private final Stack<FolderFragment> fragments = new Stack<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        main_binding = ActivitySelectDirectoryBinding.inflate(getLayoutInflater());
        toolbarBinding = ToolbarBinding.inflate(getLayoutInflater());
        setContentView(main_binding.getRoot());

        setSupportActionBar(main_binding.toolbar);

        toolBar = new ToolBar(main_binding.folderName);
//        toolBar = new ToolBar(toolbarBinding.folderName);


        String[] storages = storages();


        if (storages.length > 1) {
            storageFragment = StorageFragment.newInstance(storages);

            FragmentManager fragmentManager = getSupportFragmentManager();

            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(R.id.fragmentContainer, storageFragment);
            transaction.addToBackStack(null);
            transaction.commitAllowingStateLoss();

            toolBar.update(getString(R.string.file_explorer));
        } else {
            String root = Environment.getExternalStorageDirectory().getAbsolutePath();
            FolderFragment folderFragment = FolderFragment.newInstance(root);

            addFragment(folderFragment, false);
        }

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_WRITE_EXTERNAL_STORAGE);
//            }
//        }


//        adapter = new DirectoryAdapter();
//        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        binding.recyclerView.setAdapter(adapter);


//        Gson gson = new Gson();
//        String jsonInString = gson.toJson(main_directory);
//        Log.e("ListTags:-- ", jsonInString);

//        adapter.setOnClickListener(new DirectoryAdapter.OnClickListener() {
//            @Override
//            public void OnClick(File file, int position) {
//                Log.e("Check", "file " + file.getAbsolutePath());
////                getFilesList(file.getAbsolutePath());
//            }
//        });


    }


//    private void getFilesList(String filePath) {
//        rootPath = filePath;
//        setTitle(filePath);
//
////        lblFilePath.setText(filePath);
//        try {
//            File root = new File(filePath);
//
//            File[] fileArray = root.listFiles();
//            List<File> files = new ArrayList<>();
//            if (fileArray != null) {
//                files = Arrays.asList(fileArray);
//
//                Collections.sort(files, (lhs, rhs) -> {
//                    if (lhs.isDirectory() && !rhs.isDirectory()) {
//                        return -1;
//                    } else if (!lhs.isDirectory() && rhs.isDirectory()) {
//                        return 1;
//                    } else {
//                        return lhs.getName().toLowerCase().compareTo(rhs.getName().toLowerCase());
//                    }
//                });
//
//            }
//
//            adapter.AddItems(files);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//
//        }
//    }
//
//
//
//
//    private boolean validPath(String path) {
//        try {
//            StatFs stat = new StatFs(path);
//            stat.getBlockCount();
//
//            return true;
//        } catch (Exception e) {
////            CrashUtils.report(e);
//
//            return false;
//        }
//    }


//    @Override
//    public void onBackPressed() {
//        try {
//            getFilesList(new File(rootPath).getAbsoluteFile().getParent());
//
//        }catch (Exception e){
//
//        }
//
////        super.onBackPressed();
//    }


    public void addFragment(FolderFragment fragment, boolean addToBackStack) {
        fragments.push(fragment);

        FragmentManager fragmentManager = getSupportFragmentManager();

        FragmentTransaction transaction = fragmentManager.beginTransaction();

        if (addToBackStack) {
            transaction.setCustomAnimations(R.anim.enter_from_right,
                    R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
        }

        transaction.add(R.id.fragmentContainer, fragment);

        if (addToBackStack) {
            transaction.addToBackStack(null);
        }

        transaction.commitAllowingStateLoss();

        toolBar.update(fragment);
    }

    private String[] storages() {
        List<String> storages = new ArrayList<>();

        try {
            File[] externalStorageFiles = ContextCompat.getExternalFilesDirs(this, null);

            String base = String.format("/Android/data/%s/files", getPackageName());

            for (File file : externalStorageFiles) {
                try {
                    if (file != null) {
                        String path = file.getAbsolutePath();

                        if (path.contains(base)) {
                            String finalPath = path.replace(base, "");

                            if (validPath(finalPath)) {
                                storages.add(finalPath);
                            }
                        }
                    }
                } catch (Exception e) {

                }
            }
        } catch (Exception e) {

        }

        String[] result = new String[storages.size()];
        storages.toArray(result);

        return result;
    }

    private boolean validPath(String path) {
        try {
            StatFs stat = new StatFs(path);
            stat.getBlockCount();

            return true;
        } catch (Exception e) {


            return false;
        }
    }

    private void removeFragment(FolderFragment fragment) {
        fragments.pop();

        FragmentManager fragmentManager = getSupportFragmentManager();

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right,
                R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
        transaction.remove(fragment);
        transaction.commitAllowingStateLoss();

        if (!fragments.isEmpty()) {
            FolderFragment topFragment = fragments.peek();
            topFragment.refreshFolder();

            toolBar.update(topFragment);
        }
    }


    @Override
    public void onBackPressed() {
        if (fragments.size() > 0) {
            FolderFragment fragment = fragments.peek();

            if (fragment.onBackPressed()) {
                if (storageFragment == null) {
                    if (fragments.size() > 1) {
                        removeFragment(fragment);
                    } else {
                        finish();
                    }
                } else {
                    removeFragment(fragment);

                    if (fragments.isEmpty()) {
                        toolBar.update(getString(R.string.file_explorer));
//                        buttonBar.displayButtons(0, false, false, false, false);
                    }
                }
            }
        } else {
            finish();
        }
    }
}
