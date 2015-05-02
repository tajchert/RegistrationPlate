package pl.tajchert.tablicarejestracyjna;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import pl.tajchert.tablicarejestracyjna.api.Odpowiedzi;

/**
 * Created by Michal Tajchert on 2015-05-02.
 */
public class SubCommentsListView extends ListView {
    private List<Odpowiedzi> odpowiedzi = new ArrayList<Odpowiedzi>();

    public SubCommentsListView(Context context) {
        super(context);
    }

    public SubCommentsListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SubCommentsListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev){
        if(ev.getAction()==MotionEvent.ACTION_MOVE)
            return true;
        return super.dispatchTouchEvent(ev);
    }
}
