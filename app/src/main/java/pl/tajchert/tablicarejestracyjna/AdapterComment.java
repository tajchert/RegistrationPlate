package pl.tajchert.tablicarejestracyjna;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import pl.tajchert.tablicarejestracyjna.api.Komentarze;

/**
 * Created by michaltajchert on 29/12/14.
 */
public class AdapterComment extends RecyclerView.Adapter<HolderComment> {
    private static final String TAG = "AdapterComment";
    private List<Komentarze> contactList;

    public AdapterComment(List<Komentarze> contactList) {
        this.contactList = contactList;
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    @Override
    public void onBindViewHolder(HolderComment contactViewHolder, int i) {
        Komentarze ci = contactList.get(i);
        contactViewHolder.cAuthor.setText(ci.getPodpis());
        contactViewHolder.cContent.setText(Html.fromHtml(ci.getTresc()));
        try {
            String smallDate = ci.getData().substring(0, ci.getData().length()-9);
            contactViewHolder.cDate.setText(smallDate);
        } catch (Exception e) {
            Log.d(TAG, "onBindViewHolder error:" + e.getMessage());
        }
    }

    @Override
    public HolderComment onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.comment_card, viewGroup, false);
        return new HolderComment(itemView);
    }
}
