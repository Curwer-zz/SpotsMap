package net.eray.ParkourPlayground;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseImageView;

import java.util.List;

/**
 * Created by Niclas on 2014-12-06.
 */
public class SpotAdapter extends RecyclerView.Adapter<SpotAdapter.SpotViewHolder> {

    private List<UserSpots> spots;
    private Context context;
    private int lastPosition = -1;

    public SpotAdapter(List<UserSpots> spots, Context context) {
        this.spots = spots;
        this.context = context;
    }

    @Override
    public SpotViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.spot_cards_layout, viewGroup, false);
        return new SpotViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SpotViewHolder holder, int i) {
        UserSpots userSpots = spots.get(i);
        holder.mTitle.setText(userSpots.title);
        holder.mDate.setText(userSpots.date);
        //holder.mLocation.setText(userSpots.location.getLatitude()+" ");
        holder.mImage.setParseFile(userSpots.image);
        holder.mImage.loadInBackground(new GetDataCallback() {
            @Override
            public void done(byte[] bytes, ParseException e) {

            }
        });

        setAnimation(holder.mCard, i);

    }

    private void setAnimation(View v, int pos){
        if (pos < lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            v.startAnimation(animation);
            lastPosition = pos;
        }
    }

    @Override
    public int getItemCount() {
        if (spots != null) {
            return spots.size();
        } else {
            return 0;
        }

    }

    public static class SpotViewHolder extends RecyclerView.ViewHolder {

        protected TextView mTitle;
        protected TextView mDate;
        protected TextView mLocation;
        protected ParseImageView mImage;
        protected CardView mCard;

        public SpotViewHolder(View itemView) {
            super(itemView);
            mCard = (CardView) itemView.findViewById(R.id.card_view);
            mTitle = (TextView) itemView.findViewById(R.id.card_title);
            mDate = (TextView) itemView.findViewById(R.id.card_date);
            mLocation = (TextView) itemView.findViewById(R.id.card_location);
            mImage = (ParseImageView) itemView.findViewById(R.id.card_image);
        }
    }
}
