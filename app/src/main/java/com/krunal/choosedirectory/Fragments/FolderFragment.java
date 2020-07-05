package com.krunal.choosedirectory.Fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.krunal.choosedirectory.Activity.MainActivity;
import com.krunal.choosedirectory.Activity.SelectDirectoryActivity;
import com.krunal.choosedirectory.Adapter.FolderAdapter;
import com.krunal.choosedirectory.R;
import com.krunal.choosedirectory.databinding.ScreenFolderBinding;
import com.krunal.choosedirectory.models.FileInfo;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class FolderFragment extends Fragment {

    private static final String PARAMETER_FOLDER_PATH = "folder.path";
    private SelectDirectoryActivity mainActivity;

//    private ListView listView;
//    private TextView labelNoItems;
    private FolderAdapter adapter;
    private ScreenFolderBinding binding;

    public static FolderFragment newInstance(String folderPath) {
        FolderFragment fragment = new FolderFragment();
        Bundle parameters = new Bundle();
        parameters.putSerializable(PARAMETER_FOLDER_PATH, folderPath);
        fragment.setArguments(parameters);

        return fragment;
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);

        mainActivity = (SelectDirectoryActivity) context;
    }

    @Override
    public final View onCreateView(LayoutInflater inflater, ViewGroup container,
                                   Bundle savedInstanceState)
    {

        binding = ScreenFolderBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public final void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);


        binding.fab.setOnClickListener(v -> {


            Intent intent = new Intent();
            intent.putExtra("SelectedPath", folderName());
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            getActivity().setResult(RESULT_OK,intent);
            getActivity().finish();


        });

        binding.swipeContainer.setColorSchemeResources(R.color.colorPrimary);
        binding.swipeContainer.setOnRefreshListener(() -> {
            refreshFolder();
            binding.swipeContainer.setRefreshing(false);
        });

        adapter = new FolderAdapter(mainActivity);

        binding.list.setAdapter(adapter);
        binding.list.setOnItemClickListener((parent, view, position, id) -> {
            FileInfo fileInfo = (FileInfo) parent.getItemAtPosition(position);

            if (adapter.isSelectionMode())
            {
                adapter.updateSelection(fileInfo.toggleSelection());
                updateButtonBar();
            }
            else
            {
                if (fileInfo.isDirectory())
                {
                    openFolder(fileInfo);
                }

            }
        });

        binding.list.setOnItemLongClickListener((parent, view, position, id) -> {
            FileInfo fileInfo = (FileInfo) parent.getItemAtPosition(position);
            adapter.updateSelection(fileInfo.toggleSelection());
            updateButtonBar();

            return true;
        });

        binding.list.setOnTouchListener((v, event) -> {
            if ((event.getAction() == MotionEvent.ACTION_DOWN)
                    && binding.list.pointToPosition((int)
                    (event.getX() * event.getXPrecision()),
                    (int) (event.getY() * event.getYPrecision())) == -1) {

                onBackPressed();
                return true;
            }

            return false;
        });

        refreshFolder();
    }

    public synchronized boolean onBackPressed()
    {
        if ((adapter != null) && adapter.isSelectionMode())
        {
            unselectAll();

            return false;
        }
        else
        {
            return true;
        }
    }

    private void unselectAll()
    {
        adapter.unselectAll();
        updateButtonBar();
    }

    private void updateButtonBar()
    {
//        Clipboard clipboard = mainActivity.clipboard();
//
//        mainActivity.buttonBar().displayButtons(adapter.itemsSelected(),
//                !adapter.allItemsSelected(), !clipboard.isEmpty() && clipboard.someExist()
//                && !clipboard.hasParent(folder()), adapter.hasFiles(), true);
    }

    public String folderName()
    {
        return folder().getAbsolutePath();
    }

    private File folder()
    {
        String folderPath = parameter(PARAMETER_FOLDER_PATH, "/");

        Log.e("Check","folderPath: " + folderPath);

        return new File(folderPath);
    }

    private List<FileInfo> fileList()
    {
        File root = folder();
        File[] fileArray = root.listFiles();

        if (fileArray != null)
        {
            List<File> files = Arrays.asList(fileArray);

            Collections.sort(files, (lhs, rhs) -> {
                if (lhs.isDirectory() && !rhs.isDirectory())
                {
                    return -1;
                }
                else if (!lhs.isDirectory() && rhs.isDirectory())
                {
                    return 1;
                }
                else
                {
                    return lhs.getName().toLowerCase().compareTo(rhs.getName().toLowerCase());
                }
            });

            List<FileInfo> result = new ArrayList<>();

            for (File file : files)
            {
                if (file != null)
                {
                    result.add(new FileInfo(file));
                }
            }

            return result;
        }
        else
        {
            return new ArrayList<>();
        }
    }

    @SuppressWarnings({"unchecked", "SameParameterValue"})
    private <Type> Type parameter(String key, Type defaultValue)
    {
        Bundle extras = getArguments();

        if ((extras != null) && extras.containsKey(key))
        {
            return (Type) extras.get(key);
        }
        else
        {
            return defaultValue;
        }
    }

    private void openFolder(FileInfo fileInfo)
    {
        FolderFragment folderFragment = FolderFragment.newInstance(fileInfo.path());

        mainActivity.addFragment(folderFragment, true);
    }



    public void onSelectAll()
    {
        adapter.selectAll();
        updateButtonBar();
    }


    private void showMessage(@StringRes int text)
    {
        Toast.makeText(context(), text, Toast.LENGTH_SHORT).show();
    }

    public void refreshFolder()
    {
        List<FileInfo> files = fileList();
        adapter.setData(files);
        updateButtonBar();

        if (files.isEmpty()) {
            binding.list.setVisibility(View.GONE);
            binding.labelNoItems.setVisibility(View.VISIBLE);
        } else {
            binding.list.setVisibility(View.VISIBLE);
            binding.labelNoItems.setVisibility(View.GONE);
        }
    }

    private void startActivity(Intent intent, @StringRes int resId)
    {
        try
        {
            startActivity(intent);
        }
        catch (Exception e)
        {
//            CrashUtils.report(e);

            showMessage(resId);
        }
    }

    private boolean isResolvable(Intent intent)
    {
        PackageManager manager = mainActivity.getPackageManager();
        List<ResolveInfo> resolveInfo = manager.queryIntentActivities(intent, 0);

        return !resolveInfo.isEmpty();
    }

    private Context context()
    {
        Context context = getContext();

        if (context != null)
        {
            return context;
        }
        else
        {
            Context fragmentActivity = getActivity();

            if (fragmentActivity != null)
            {
                return fragmentActivity;
            }
            else
            {
                return mainActivity;
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        // no call for super(). Bug on API Level > 11.
    }
}