package pl.tajchert.tablicarejestracyjna;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.MatrixCursor;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

/**
 * Created by michaltajchert on 29/12/14.
 */
public class SearchStorage {
    private static final String TAG = "SearchStorage";
    private static final String PREFS_KEY= "pl.tajchert.tablicarejestracyjna";
    private static final String PREFS_KEY_HISTORY= "PLATE_HISTORY";

    private Context context;
    private SharedPreferences sharedPreferences;
    private ArrayList<String> history;
    private static SearchStorage mInstance = null;

    public SearchStorage(Context context) {
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
        this.history = new ArrayList<String>(sharedPreferences.getStringSet(PREFS_KEY_HISTORY, new HashSet<String>()));
    }

    public static SearchStorage getInstance(Context context){
        if(mInstance == null){
            mInstance = new SearchStorage(context);
        }
        return mInstance;
    }

    public ArrayList<String> getHistory(){
        return history;
    }

    public void addToHistory(String search) {
        history.add(search);
        sharedPreferences.edit().putStringSet(PREFS_KEY_HISTORY, new HashSet<String>(history)).apply();
    }

    public static CursorAdapter getCursor (Context context, String match){
        String[] columnNames = {"_id","text"};
        MatrixCursor cursor = new MatrixCursor(columnNames);
        ArrayList<String> onlyMatching = new ArrayList<>();
        for(String searchHistorical : getInstance(context).history){
            if(searchHistorical.contains(match) && onlyMatching.size() < 10){
                onlyMatching.add(searchHistorical);
            }
        }
        Collections.rotate(onlyMatching, onlyMatching.size());
        String[] temp = new String[2];
        String[] array = new String[onlyMatching.size()];
        array = onlyMatching.toArray(array);
        int id = 0;
        for(String item : array){
            temp[0] = Integer.toString(id++);
            temp[1] = item;
            cursor.addRow(temp);
        }
        String[] from = {"text"};
        int[] to = {android.R.id.text1};
        CursorAdapter cursorAdapter =  new SimpleCursorAdapter(context, R.layout.search_suggestion_list, cursor, from, to);
        return cursorAdapter;
    }

}
