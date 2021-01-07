package it.scroking.autoscroc.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.scroking.autoscroc.R;
import it.scroking.autoscroc.models.CarModel;

public class ModelsAdapter extends RecyclerView.Adapter<ModelsAdapter.ModelViewHolder> {
    private List<CarModel> mDataset;
    private OnModelListener mOnModelListener;

    public ModelsAdapter(List<CarModel> mDataset, OnModelListener mOnModelListener) {
        this.mDataset = mDataset;

        this.mOnModelListener = mOnModelListener;
    }

    @NonNull
    @Override
    public ModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_item, parent, false);

        return new ModelViewHolder(v, this.mOnModelListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ModelViewHolder holder, int position) {
        CarModel carModel = mDataset.get(position);
        holder.modelName.setText(carModel.getName());
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }


    public interface OnModelListener {
        void onModelClick(int position);
    }

    public static class ModelViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        TextView modelName;
        OnModelListener onModelListener;

        public ModelViewHolder(View v, OnModelListener onModelListener) {
            super(v);
            this.modelName = v.findViewById(R.id.model_name);

            this.onModelListener = onModelListener;

            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            this.onModelListener.onModelClick(getAdapterPosition());
        }
    }
}
