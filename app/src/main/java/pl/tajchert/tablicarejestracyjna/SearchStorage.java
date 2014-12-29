package pl.tajchert.tablicarejestracyjna;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by michaltajchert on 29/12/14.
 */
public class SearchStorage {
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
        if(mInstance == null)
        {
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

}
