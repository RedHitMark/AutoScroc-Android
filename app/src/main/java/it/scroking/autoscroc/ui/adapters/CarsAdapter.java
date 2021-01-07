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
import it.scroking.autoscroc.models.Car;

public class CarsAdapter extends RecyclerView.Adapter<CarsAdapter.CarViewHolder> {

    private List<Car> mDataset;
    private OnCarListener mOnCarListener;

    public CarsAdapter(List<Car> mDataset, OnCarListener onCarListener) {
        this.mDataset = mDataset;
        this.mOnCarListener = onCarListener;
    }


    @NonNull
    @Override
    public CarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.car_item, parent, false);

        return new CarViewHolder(v, this.mOnCarListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CarsAdapter.CarViewHolder holder, int position) {
        Car car = mDataset.get(position);
        holder.carName.setText(car.getName());

        byte[] decodedString = Base64.decode(car.getImg(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        holder.carImage.setImageBitmap(decodedByte);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface OnCarListener {
        void onCarClick(int position);
    }

    public class CarViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        TextView carName;
        ImageView carImage;

        OnCarListener onBrandListener;

        public CarViewHolder(View v, OnCarListener onBrandListener) {
            super(v);
            this.carName = v.findViewById(R.id.car_name);
            this.carImage = v.findViewById(R.id.img);

            this.onBrandListener = onBrandListener;

            v.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            onBrandListener.onCarClick(getAdapterPosition());
        }
    }
}
