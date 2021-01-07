package it.scroking.autoscroc.ui.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.scroking.autoscroc.R;
import it.scroking.autoscroc.models.CarBrand;

public class BrandsAdapter extends RecyclerView.Adapter<BrandsAdapter.BrandViewHolder> {
    private List<CarBrand> mDataset;
    private OnBrandListener mOnBrandListener;

    public BrandsAdapter(List<CarBrand> mDataset, OnBrandListener onBrandListener) {
        this.mDataset = mDataset;
        this.mOnBrandListener = onBrandListener;
    }


    @NonNull
    @Override
    public BrandViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.brand_item, parent, false);

        return new BrandViewHolder(v, this.mOnBrandListener);
    }

    @Override
    public void onBindViewHolder(@NonNull BrandViewHolder holder, int position) {
        CarBrand carBrand = mDataset.get(position);
        holder.brandName.setText(carBrand.getName());

        byte[] decodedString = Base64.decode(carBrand.getLogo(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        holder.brandimage.setImageBitmap(decodedByte);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface OnBrandListener {
        void onBrandClick(int position);
    }

    public class BrandViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        TextView brandName;
        ImageView brandimage;

        OnBrandListener onBrandListener;

        public BrandViewHolder(View v, OnBrandListener onBrandListener) {
            super(v);
            this.brandName = v.findViewById(R.id.brand_name);
            this.brandimage = v.findViewById(R.id.brand_image);

            this.onBrandListener = onBrandListener;

            v.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            onBrandListener.onBrandClick(getAdapterPosition());
        }
    }
}
