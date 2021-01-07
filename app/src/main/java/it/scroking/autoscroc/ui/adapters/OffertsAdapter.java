package it.scroking.autoscroc.ui.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import it.scroking.autoscroc.R;
import it.scroking.autoscroc.models.Offer;

public class OffertsAdapter extends BaseAdapter {
    private List<Offer> carsList;
    private LayoutInflater layoutInflater;
    private Context context;

    public OffertsAdapter(Context aContext, List<Offer> carItems) {
        this.context = aContext;
        this.carsList = carItems;
        layoutInflater = LayoutInflater.from(aContext);
    }

    public int getCount() {
        return carsList.size();
    }

    public Object getItem(int position) {
        return carsList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View covertView, ViewGroup parent) {
        ViewHolder holder;
        if (covertView == null) {
            covertView = layoutInflater.inflate(R.layout.offer_list_item, null);
            holder = new ViewHolder();
            holder.carImage = covertView.findViewById(R.id.imageCar);
            holder.carName = covertView.findViewById(R.id.textViewCarName);

            holder.carKM = covertView.findViewById(R.id.textViewCarKm);
            holder.carPrice = covertView.findViewById(R.id.textViewPriceResp);
            covertView.setTag(holder);
        } else {
            holder = (ViewHolder) covertView.getTag();
        }
        Offer rentitem = this.carsList.get(position);
        byte[] decodedString = Base64.decode(carsList.get(position).getImg(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        holder.carImage.setImageBitmap(decodedByte);
        holder.carName.setText(rentitem.getName());
        holder.carKM.setText("KM: " + rentitem.getKm());
        holder.carPrice.setText(String.valueOf(rentitem.getPrice()));
        if (rentitem.getPrice() >= 8000) {

            holder.carPrice.setTextColor(Color.RED);
        } else {
            holder.carPrice.setTextColor(Color.rgb(0, 100, 0));
        }


        return covertView;
    }

    static class ViewHolder {
        ImageView carImage;
        TextView carName;
        TextView carKM;
        TextView carPrice;
    }

}
