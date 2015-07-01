package pl.tajchert.tablicarejestracyjna.api;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import pl.tajchert.tablicarejestracyjna.R;
import retrofit.Callback;

/**
 * Created by Michal Tajchert on 2015-05-09.
 */
public class TopCardListAdapter extends RecyclerView.Adapter<TopCardListAdapter.TopPlatesViewHolder> {
    private static final String TAG = "TopCardListAdapter";
    private ArrayList<ApiTopRegistrationPlates.Result> topPlates;
    private ArrayList<ApiTopRegistrationPlates.Result> worstDrivers;
    private ArrayList<ApiTopRegistrationPlates.Result> bestDrivers;
    private Context context;
    private Callback<String> callback;

    @InjectView(R.id.card_title)
    TextView cardTitle;

    public TopCardListAdapter(ArrayList<ApiTopRegistrationPlates.Result> topPlates, Context context, Callback<String> callback) {
        this.context = context;
        this.topPlates = topPlates;
        this.callback = callback;
        if(topPlates.size() >= 20) {
            this.bestDrivers = new ArrayList<>(topPlates.subList(10, 20));
            this.worstDrivers = new ArrayList<>(topPlates.subList(0, 10));
        }

    }

    @Override
    public TopPlatesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView =  LayoutInflater.from(parent.getContext()).inflate(R.layout.card_top, parent, false);
        ButterKnife.inject(this, itemView);
        return new TopPlatesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TopPlatesViewHolder userStatsCard, final int cardNumber) {
        WtBestPlayersAdapter bestAdapter;
        if(cardNumber == 0) {
            bestAdapter = new WtBestPlayersAdapter(context, R.layout.row_top_plate, worstDrivers, false);
            cardTitle.setText("Najgorsi kierowcy");
        } else {
            bestAdapter = new WtBestPlayersAdapter(context, R.layout.row_top_plate, bestDrivers, true);
            cardTitle.setText("Najlepsi kierowcy");
        }
        userStatsCard.listView.setAdapter(bestAdapter);
        userStatsCard.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(cardNumber == 0) {
                    callback.success(worstDrivers.get(i).worstDriver, null);
                } else {
                    callback.success(bestDrivers.get(i).bestDriver, null);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    public class TopPlatesViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.list_top)
        ListView listView;

        public TopPlatesViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }

    public class WtBestPlayersAdapter extends ArrayAdapter<ApiTopRegistrationPlates.Result> {

        @InjectView(R.id.row_plate_number)
        TextView plateNumber;

        private ArrayList<ApiTopRegistrationPlates.Result> objects;
        private boolean showBestDrivers;

        public WtBestPlayersAdapter(Context context, int textViewResourceId, ArrayList<ApiTopRegistrationPlates.Result> objects, boolean showBestDrivers) {
            super(context, textViewResourceId, objects);
            this.objects = objects;
            this.showBestDrivers = showBestDrivers;
        }

        public View getView(int position, View convertView, ViewGroup parent){
            View v = convertView;
            if (v == null) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(R.layout.row_top_plate, null);
            }

            ApiTopRegistrationPlates.Result result = objects.get(position);

            if (result != null) {
                ButterKnife.inject(this, v);
                if(showBestDrivers) {
                    plateNumber.setText(result.bestDriver);
                } else {
                    plateNumber.setText(result.worstDriver);
                }
            }
            return v;

        }

    }
}
