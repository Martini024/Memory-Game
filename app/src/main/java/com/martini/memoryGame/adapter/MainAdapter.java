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
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        int size = Math.max(parent.getMeasuredHeight(), parent.getMeasuredWidth());
        marginLayoutParams.width = (int) (size * 0.28);
        marginLayoutParams.height = (int) (size * 0.28);
        int margin = (int) (size * 0.01);
        parent.setPadding(margin * 3, margin, margin * 5, 0);
        marginLayoutParams.setMargins(margin * 2, 0, 0, margin * 2);
        view.setLayoutParams(marginLayoutParams);
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
