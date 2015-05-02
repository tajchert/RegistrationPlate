package pl.tajchert.tablicarejestracyjna;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import pl.tajchert.tablicarejestracyjna.api.Komentarze;

/**
 * Created by michaltajchert on 29/12/14.
 */
public class AdapterComment extends RecyclerView.Adapter<HolderComment> {
    private static final String TAG = "AdapterComment";
    private List<Komentarze> commentList;
    private Context context;

    public AdapterComment(List<Komentarze> commentList, Context context) {
        this.commentList = commentList;
        this.context = context;
    }

    public void setCommentList(List<Komentarze> commentList) {
        this.commentList = commentList;
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    @Override
    public void onBindViewHolder(HolderComment contactViewHolder, int i) {
        Komentarze ci = commentList.get(i);
        contactViewHolder.cAuthor.setText(ci.getPodpis());
        contactViewHolder.cContent.setText(Html.fromHtml(ci.getTresc()));
        contactViewHolder.cContent.setMovementMethod(LinkMovementMethod.getInstance());
        if(ci.getTresc() != null && ci.getTresc().contains("<img src=")) {
            String imageUrl = ci.getTresc();
            contactViewHolder.cContent.setText(Html.fromHtml(imageUrl.substring(0, imageUrl.indexOf("<img src=\""))));
            imageUrl = imageUrl.substring(imageUrl.indexOf("<img src=\"") + 10);
            imageUrl = imageUrl.substring(0, imageUrl.indexOf("\"/></a>") );
            Glide.with(context).load(imageUrl).into(contactViewHolder.cImage);
        } else {
            contactViewHolder.cImage.setVisibility(View.GONE);
        }

        if(ci.getOdpowiedzi() != null && ci.getOdpowiedzi().size() > 0) {
            SubCommentAdapter adapter = new SubCommentAdapter(context, R.layout.subcomment_card, (ArrayList) ci.getOdpowiedzi());
            contactViewHolder.cSubCommentsList.setAdapter(adapter);
        } else {
            contactViewHolder.cSubCommentsList.setVisibility(View.GONE);
        }

        if(ci.getTresc() != null && ci.getTresc().contains("<iframe class=\"youtube-player\" type=\"text/html\" ")){
            String videoUrl =ci.getTresc();
            videoUrl = videoUrl.substring(videoUrl.indexOf("<iframe class=\"youtube-player\" type=\"text/html\" src=\"") + 53);
            videoUrl = videoUrl.substring(0, videoUrl.indexOf("\" allowfullscreen frameborder=\"0\"></iframe>"));
            Log.d(TAG, "onBindViewHolder :" + videoUrl);
            final String movieUrl = videoUrl;
            contactViewHolder.cYoutubeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(movieUrl));
                    context.startActivity(i);
                }
            });
        } else {
            contactViewHolder.cYoutubeButton.setVisibility(View.GONE);
        }

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
