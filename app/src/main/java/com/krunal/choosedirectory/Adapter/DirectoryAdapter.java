package com.krunal.choosedirectory.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.krunal.choosedirectory.databinding.ItemDirectoryBinding;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;

public class DirectoryAdapter extends RecyclerView.Adapter<DirectoryAdapter.MyViewHolder>{

    List<File> list = new ArrayList<>();

    OnClickListener mOnClickListener;

    public DirectoryAdapter(){
    }

    public void setOnClickListener(OnClickListener mOnClickListener) {
        this.mOnClickListener = mOnClickListener;
    }


    public void AddItems(List<File> list){
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDirectoryBinding itemDirectoryBinding =ItemDirectoryBinding
                .inflate(LayoutInflater.from(parent.getContext()));
        return new MyViewHolder(itemDirectoryBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        File current = list.get(position);
        holder.binding.name.setText(current.getName());
        holder.Bind(current,mOnClickListener,position);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private ItemDirectoryBinding binding;

        public MyViewHolder(@NonNull ItemDirectoryBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;

        }
        void Bind(File file, OnClickListener onClickBackUp, int position){

            binding.mainLayout.setOnClickListener(v -> {
                onClickBackUp.OnClick(file,position);
            });



        }
    }

    public interface OnClickListener {
        void OnClick(File file, int position);
    }

}
