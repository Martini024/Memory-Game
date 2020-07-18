package com.martini.memoryGame.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.martini.memoryGame.R;

public class LoadAdapter extends RecyclerView.Adapter<LoadAdapter.ViewHolder> {
    private int size;

    public LoadAdapter(int size) {
        this.size = size;
    }

    @Override
    public LoadAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading_image, parent, false);
        return new LoadAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.imageButton.setEnabled(false);
        holder.imageButton.setAlpha(0.75f);
        holder.progressBar.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return size;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageButton imageButton;
        private ProgressBar progressBar;

        public ViewHolder(View itemView) {
            super(itemView);
            imageButton = itemView.findViewById(R.id.imageButton);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }
}
