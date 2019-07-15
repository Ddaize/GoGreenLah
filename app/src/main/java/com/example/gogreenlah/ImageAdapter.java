package com.example.gogreenlah;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private Context context;
    private List<ImageUpload> uploads;
    private OnImageClickListener onImageClickListener;


    public interface OnImageClickListener {
        void onImageClick (int position);
    }

    public void setOnImageClickListener(OnImageClickListener listener) {
        this.onImageClickListener = listener;
    }

    public ImageAdapter(Context context, List<ImageUpload> uploads) {
        this.context = context;
        this.uploads = uploads;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.upload_image_item,parent, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder imageViewHolder, int i) {
        ImageUpload uploadCurrent = uploads.get(i);
        imageViewHolder.textViewName.setText(uploadCurrent.getImageName());
        Picasso.get().load(uploadCurrent.getImageUrl())
               // .fit()
               // .centerCrop()
                .into(imageViewHolder.imageView);
    }

    @Override
    public int getItemCount() {
        return uploads.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView textViewName;
        public CircleImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewName = itemView.findViewById(R.id.textViewName);
            imageView = itemView.findViewById(R.id.imageViewUpload);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onImageClickListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    onImageClickListener.onImageClick(position);
                }
            }
        }
    }


}
