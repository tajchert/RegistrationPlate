package pl.tajchert.tablicarejestracyjna;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.crashlytics.android.Crashlytics;
import com.getbase.floatingactionbutton.AddFloatingActionButton;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import pl.tajchert.tablicarejestracyjna.api.Komentarze;
import pl.tajchert.tablicarejestracyjna.api.Tablica;


public class MainSearchActivity extends ActionBarActivity implements SearchView.OnQueryTextListener {
    private static final String TAG = "MainSearchActivity";
    private AddFloatingActionButton fab;
    private RequestQueue queue;
    private boolean votedForThatPlate = false;
    private Tablica currentTablica;
    private LinearLayout buttonsVotingLayout;

    private RecyclerView commentsRecList;
    private RecyclerView.Adapter adapter;
    private SearchView searchView;
    private CardView cardViewHint;
    private CardView cardViewPlate;
    private SwipeRefreshLayout swipeLayout;

    private DialogFragment newFragment;

    private String lastSearchSuggestion = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Crashlytics.start(this);
        setContentView(R.layout.activity_main_search);
        queue = Volley.newRequestQueue(this);

        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        commentsRecList = (RecyclerView) findViewById(R.id.commentList);
        fab = (AddFloatingActionButton) findViewById(R.id.normal_plus);
        LinearLayout voteUp = (LinearLayout) findViewById(R.id.voteUp);
        LinearLayout voteDown = (LinearLayout) findViewById(R.id.voteDown);
        buttonsVotingLayout = (LinearLayout) findViewById(R.id.buttonsVoting);
        cardViewPlate = (CardView) findViewById(R.id.cardViewPlate);
        cardViewHint = (CardView) findViewById(R.id.cardViewHint);

        commentsRecList.setHasFixedSize(false);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        commentsRecList.setLayoutManager(llm);
        adapter = new AdapterComment(new ArrayList<Komentarze>());
        commentsRecList.setAdapter(adapter);

        swipeLayout.setEnabled(false);
        swipeLayout.setColorSchemeColors(getResources().getColor(R.color.theme_color_accent), getResources().getColor(R.color.theme_color), getResources().getColor(R.color.theme_color_dark));
        swipeLayout.setDistanceToTriggerSync(Integer.MAX_VALUE);//Do not allow user to pull to refresh

        setButtons(voteUp, voteDown);
    }

    private void setButtons(LinearLayout voteUp, LinearLayout voteDown) {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentTablica != null && currentTablica.getId() != null &&currentTablica.getId().length() > 0) {
                    showCommentDialog(currentTablica.getId());
                } else {
                    showCommentDialog(null);
                }
            }
        });

        voteUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentTablica != null && currentTablica.getId() != null && votedForThatPlate == false){
                    vote(currentTablica.getId(), 1);
                }
            }
        });

        voteDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentTablica != null && currentTablica.getId() != null && votedForThatPlate == false){
                    vote(currentTablica.getId(), (-1));
                }
            }
        });
    }

    @Override
    public boolean onQueryTextSubmit(String searchPlate) {
        if(searchPlate != null && searchPlate.length() > 0) {
            searchPlate = searchPlate.toUpperCase();
            searchPlate = searchPlate.replace(" " , "");
            if(searchPlate.equals("WX37125")){
                setCustomCard();
            } else {
                search(searchPlate);
            }
            Log.d(TAG, "onQueryTextSubmit history: " + SearchStorage.getInstance(MainSearchActivity.this).getHistory());
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        if(!s.equals(s.toUpperCase())){
            searchView.setQuery(s.toUpperCase(), false);
        } else {
            searchView.setSuggestionsAdapter(SearchStorage.getCursor(MainSearchActivity.this, s));
            lastSearchSuggestion = s;
        }
        return false;
    }

    private void search(final String plateNumber) {
        swipeLayout.setRefreshing(true);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, APIConstants.TABLICE_INFO_PLATE + plateNumber, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "onResponse response: " +response.toString());
                Gson gson = new Gson();
                Tablica tablica = gson.fromJson(response.toString(), Tablica.class);
                tablica.setId(plateNumber);
                if(tablica.getLapkiDol()== 0 && tablica.getLapkiGora()==0 && (tablica.getKomentarze()== null || tablica.getKomentarze().size()==0)){
                    setNoPlateView();
                    Toast.makeText(getApplicationContext(), "Tej tablicy nie ma jeszcze w systemie, dodaj kometarz.", Toast.LENGTH_LONG).show();
                } else {
                    currentTablica = tablica;
                    setPlateView(tablica);
                }
                if(swipeLayout.isRefreshing()){
                    swipeLayout.setRefreshing(false);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Bład z wyszukiwaniem.", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onErrorResponse :" + error.getMessage());
                if(swipeLayout.isRefreshing()){
                    swipeLayout.setRefreshing(false);
                }
            }
        });
        queue.add(jsObjRequest);
    }

    private void vote(String plateNumber, final int value) {
        // 1 - upvote, (-1) - downvote
        swipeLayout.setRefreshing(true);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, APIConstants.TABLICE_INFO_PLATE + plateNumber + APIConstants.TABLICE_INFO_VOTE_ADD  + value, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "onResponse response: " +response.toString());
                String status = response.optString("status");
                if(status != null && status.equals("ok") && votedForThatPlate == false){
                    //OK!
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.vote_success), Toast.LENGTH_SHORT).show();
                    if(value == 1) {
                        TextView voteUpText = (TextView) findViewById(R.id.voteUpText);
                        voteUpText.setText((currentTablica.getLapkiGora() + 1) + "");
                    } else if(value == (-1)) {
                        TextView voteUpText = (TextView) findViewById(R.id.voteUpText);
                        voteUpText.setText((currentTablica.getLapkiGora() - 1) + "");
                    }
                } else {
                    //NOT OK
                    //Voted?
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.vote_repeat), Toast.LENGTH_SHORT).show();
                }
                //Set button off
                if(buttonsVotingLayout != null) {
                    buttonsVotingLayout.setAlpha(0.4f);
                }
                votedForThatPlate = true;
                if(swipeLayout.isRefreshing()){
                    swipeLayout.setRefreshing(false);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //NOT OK
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.vote_error), Toast.LENGTH_SHORT).show();
                if(swipeLayout.isRefreshing()){
                    swipeLayout.setRefreshing(false);
                }
            }
        });
        queue.add(jsObjRequest);
    }

    private void setPlateView(Tablica tablica) {
        cardViewPlate.setVisibility(View.VISIBLE);
        commentsRecList.setVisibility(View.VISIBLE);
        cardViewHint.setVisibility(View.GONE);
        SearchStorage.getInstance(MainSearchActivity.this).addToHistory(tablica.getId());

        TextView textViewPlateId = (TextView) findViewById(R.id.textCardPlate);
        textViewPlateId.setText(tablica.getId()+"");

        TextView voteUpText = (TextView) findViewById(R.id.voteUpText);
        voteUpText.setText(tablica.getLapkiGora()+"");
        TextView voteDownText = (TextView) findViewById(R.id.voteDownText);
        voteDownText.setText(tablica.getLapkiDol()+"");

        if(buttonsVotingLayout != null) {
            buttonsVotingLayout.setAlpha(1.0f);
        }
        votedForThatPlate = false;

        ((AdapterComment) adapter).setCommentList(tablica.getKomentarze());
        adapter.notifyDataSetChanged();
    }

    /**
     * When there is no match
     */
    private void setNoPlateView(){
        cardViewPlate.setVisibility(View.INVISIBLE);
        commentsRecList.setVisibility(View.INVISIBLE);
        cardViewHint.setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.textHint)).setText(getResources().getText(R.string.card_info_no_result));
    }
    /**
     * Default (startin/clean) card
     */
    private void setCleanCard(){
        cardViewPlate.setVisibility(View.INVISIBLE);
        commentsRecList.setVisibility(View.INVISIBLE);
        cardViewHint.setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.textHint)).setText(getResources().getText(R.string.card_hint_start));
    }

    /**
     * Easter egg, nothing fancy
     */
    private void setCustomCard(){
        cardViewPlate.setVisibility(View.INVISIBLE);
        commentsRecList.setVisibility(View.INVISIBLE);
        cardViewHint.setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.textHint)).setText(getResources().getText(R.string.card_info_author));
    }

    /**
     * Show input for new comment, fill String if user should have suggested some plate id
     * @param plateID
     */
    private void showCommentDialog(String plateID) {
        newFragment = FragmentDialogComment.newInstance(plateID);
        newFragment.setRetainInstance(true);
        newFragment.show(getSupportFragmentManager(), "dialogTag");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_search, menu);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setOnQueryTextListener(this);
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                setCleanCard();
                return false;
            }
        });
        searchView.setSuggestionsAdapter(SearchStorage.getCursor(MainSearchActivity.this, ""));
        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int i) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int i) {
                String selectedSearch = SearchStorage.getMatchedSearches(MainSearchActivity.this, lastSearchSuggestion).get(i);
                searchView.setQuery(selectedSearch, false);
                search(selectedSearch);
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == ToolConstants.ACTION_GALLERY_PICTURE && resultCode == RESULT_OK) {
            if(newFragment != null){
                ((FragmentDialogComment) newFragment).setImage(null);
            }
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                if(newFragment!=null && newFragment.isVisible()){
                    ((FragmentDialogComment) newFragment).setImage(selectedImage);
                    Bitmap bitmap;
                    InputStream is;
                    is = getContentResolver().openInputStream(Uri.parse(imageUri.toString()));
                    bitmap = BitmapFactory.decodeStream(is);
                    try {
                        File f = ToolConstants.createImageFile();
                        ToolConstants.saveBitmap(bitmap,f);
                        Log.d(TAG, "onActivityResult f:" + f.getAbsolutePath());
                        ((FragmentDialogComment) newFragment).lastPicLocation = f.getAbsolutePath();
                    } catch (IOException e) {
                        Log.d(TAG, "onActivityResult error: " + e.getMessage());
                    }
                }
            } catch (FileNotFoundException e) {
                Toast.makeText(getApplicationContext(), "Bład z wyborem zdjecia.", Toast.LENGTH_SHORT).show();
            }
        } else if(requestCode == ToolConstants.ACTION_TAKE_PICTURE && resultCode == RESULT_OK) {
            if(newFragment != null){
                ((FragmentDialogComment) newFragment).setImage(null);
            }
            if(newFragment!=null && newFragment.isVisible()){
                File imgFile = new  File(((FragmentDialogComment) newFragment).lastPicLocation);
                if(imgFile.exists()){
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    if(myBitmap != null) {
                        ((FragmentDialogComment) newFragment).setImage(myBitmap);
                    }
                } else{
                    Toast.makeText(getApplicationContext(), "Bład z wyborem zdjecia.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
