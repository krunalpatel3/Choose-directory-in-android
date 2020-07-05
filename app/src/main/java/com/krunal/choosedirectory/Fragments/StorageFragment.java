package com.krunal.choosedirectory.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import androidx.fragment.app.Fragment;
import com.krunal.choosedirectory.Activity.MainActivity;
import com.krunal.choosedirectory.Activity.SelectDirectoryActivity;
import com.krunal.choosedirectory.Adapter.StorageAdapter;
import com.krunal.choosedirectory.databinding.ScreenFolderBinding;
import com.krunal.choosedirectory.databinding.ScreenStorageBinding;

import java.util.Arrays;

public class StorageFragment extends Fragment {
    private static final String PARAMETER_STORAGES_PATH = "storages.path";
    private SelectDirectoryActivity mainActivity;
    private StorageAdapter adapter;
    private ScreenStorageBinding binding;

    public static StorageFragment newInstance(String[] storagesPath) {
        StorageFragment fragment = new StorageFragment();
        Bundle parameters = new Bundle();
        parameters.putStringArray(PARAMETER_STORAGES_PATH, storagesPath);
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
    public final View onCreateView(LayoutInflater inflater,
                                   ViewGroup container,
                                   Bundle savedInstanceState) {
        binding = ScreenStorageBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public final void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        adapter = new StorageAdapter(mainActivity);

        binding.list.setAdapter(adapter);

        binding.list.setOnItemClickListener((parent, view, position, id) -> {
            String storagePath = (String) parent.getItemAtPosition(position);
            openStorage(storagePath);
        });

        reload();
    }

    public void reload()
    {
        adapter.update(Arrays.asList(storages()));
    }

    private String[] storages()
    {
        Bundle extras = getArguments();

        return (extras != null) ? extras.getStringArray(PARAMETER_STORAGES_PATH) : new String[0];
    }

    private void openStorage(String storagePath)
    {
        FolderFragment folderFragment = FolderFragment.newInstance(storagePath);

        mainActivity.addFragment(folderFragment, true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        // no call for super(). Bug on API Level > 11.
    }
}