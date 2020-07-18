package com.martini.memoryGame.adapter;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.martini.memoryGame.R;

import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

    private List<Bitmap> cardFront;

    public MainAdapter(List<Bitmap> cardFront) {
        this.cardFront = cardFront;
    }

    @Override
    public MainAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_flip_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.imageButton.setImageBitmap(cardFront.get(position));
        holder.itemView.setClickable(false);
    }

    @Override
    public int getItemCount() {
        return cardFront.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageButton imageButton;

        public ViewHolder(View itemView) {
            super(itemView);
            imageButton = itemView.findViewById(R.id.cardFront);
        }
    }
}
