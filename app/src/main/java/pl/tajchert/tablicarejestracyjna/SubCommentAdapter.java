package pl.tajchert.tablicarejestracyjna;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import pl.tajchert.tablicarejestracyjna.api.Odpowiedzi;

/**
 * Created by Michal Tajchert on 2015-05-02.
 */
public class SubCommentAdapter  extends ArrayAdapter<Odpowiedzi> {
    private static final String TAG = "SubCommentAdapter";

    Context mContext;
    int layoutResourceId;
    ArrayList<Odpowiedzi> data;

    public SubCommentAdapter(Context mContext, int layoutResourceId, ArrayList<Odpowiedzi> data) {
        super(mContext, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            // inflate the layout
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(layoutResourceId, parent, false);
        }

        Odpowiedzi objectItem = data.get(position);
        TextView name = (TextView) convertView.findViewById(R.id.subcommentAuthor);
        TextView content = (TextView) convertView.findViewById(R.id.subcommentContent);
        TextView data = (TextView) convertView.findViewById(R.id.subcommentDate);

        name.setText(objectItem.getPodpis());
        content.setText(Html.fromHtml(objectItem.getTresc()));
        try {
            String smallDate = objectItem.getData().substring(0, objectItem.getData().length()-9);
            data.setText(smallDate);
        } catch (Exception e) {
            Log.d(TAG, "onBindViewHolder error:" + e.getMessage());
        }

        return convertView;

    }

}