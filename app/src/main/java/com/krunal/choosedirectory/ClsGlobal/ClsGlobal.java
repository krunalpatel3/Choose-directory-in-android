package com.krunal.choosedirectory.ClsGlobal;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.os.storage.StorageManager;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class ClsGlobal {

    public static final int REQUEST_PERMISSION = 123;
    public static final int SELECT_DIRECTORY = 345;

    public static String[] permissions_perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};

    @AfterPermissionGranted(REQUEST_PERMISSION)
    public static void requestPermission(Activity ActivityContext) {

        if (!EasyPermissions.hasPermissions(ActivityContext, permissions_perms)) {
            EasyPermissions.requestPermissions(ActivityContext, "Please grant the permissions",
                    REQUEST_PERMISSION, permissions_perms);
        }

    }

    /**
     * Get external sd card path using reflection
     * @param mContext
     * @param is_removable is external storage removable
     * @return
     */
    public static String getExternalStoragePath(Context mContext, boolean is_removable) {

        StorageManager mStorageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
        Class<?> storageVolumeClazz = null;
        try {
            storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
            Method getVolumeList = mStorageManager.getClass().getMethod("getVolumeList");
            Method getPath = storageVolumeClazz.getMethod("getPath");
            Method isRemovable = storageVolumeClazz.getMethod("isRemovable");
            Object result = getVolumeList.invoke(mStorageManager);
            final int length = Array.getLength(result);
            for (int i = 0; i < length; i++) {
                Object storageVolumeElement = Array.get(result, i);
                String path = (String) getPath.invoke(storageVolumeElement);
                boolean removable = (Boolean) isRemovable.invoke(storageVolumeElement);
                if (is_removable == removable) {
                    return path;
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
