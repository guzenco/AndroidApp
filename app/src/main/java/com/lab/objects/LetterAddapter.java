package com.lab.objects;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lab.R;

public class LetterAddapter extends RecyclerView.Adapter<LetterAddapter.ListItemViewHolder>{

    Letter letters[];
    OnClickListener onClickListener;

    public LetterAddapter(Letter[] letters, OnClickListener onClickListener) {
        this.letters = letters;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public ListItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_letter, parent, false);
        return new ListItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ListItemViewHolder holder, int position) {
        Letter l =  letters[position];
        holder.label.setText(l.toString());
        holder.label.setOnClickListener(view -> {
            onClickListener.onClick(view, position);
        });
    }

    @Override
    public int getItemCount() {
        return letters.length;
    }

    public static final class ListItemViewHolder extends RecyclerView.ViewHolder {
        Button label;

        public ListItemViewHolder(View itemView) {
            super(itemView);
            label = (Button) itemView.findViewById(R.id.letter);
        }
    }

    public static interface OnClickListener{
        void onClick(View v, int p);
    }
}


