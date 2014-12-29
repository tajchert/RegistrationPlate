package pl.tajchert.tablicarejestracyjna;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by michaltajchert on 29/12/14.
 */
public class HolderComment extends RecyclerView.ViewHolder {
    protected TextView cDate;
    protected TextView cContent;
    protected TextView cAuthor;
    //protected ImageView cUp;
    //protected ImageView cDown;

    public HolderComment(View v) {
        super(v);
        cDate =  (TextView) v.findViewById(R.id.commentDate);
        cContent = (TextView)  v.findViewById(R.id.commentContent);
        cAuthor = (TextView)  v.findViewById(R.id.commentAuthor);
        //cUp = (ImageView) v.findViewById(R.id.commentVoteUp);
        //cDown = (ImageView) v.findViewById(R.id.commentVoteDown);
    }
}