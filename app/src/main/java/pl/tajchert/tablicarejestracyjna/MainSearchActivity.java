package pl.tajchert.tablicarejestracyjna;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
import com.google.android.gms.analytics.HitBuilders;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import io.fabric.sdk.android.Fabric;
import pl.tajchert.tablicarejestracyjna.api.APIConnect;
import pl.tajchert.tablicarejestracyjna.api.ApiTopRegistrationPlates;
import pl.tajchert.tablicarejestracyjna.api.EventPostCommentResult;
import pl.tajchert.tablicarejestracyjna.api.Komentarze;
import pl.tajchert.tablicarejestracyjna.api.Tablica;
import pl.tajchert.tablicarejestracyjna.api.TopCardListAdapter;
import retrofit.Callback;
import retrofit.RetrofitError;


public class MainSearchActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private static final String TAG = "MainSearchActivity";
    private AddFloatingActionButton fab;
    private RequestQueue queue;
    private boolean votedForThatPlate = false;
    private Tablica currentTablica;
    private LinearLayout buttonsVotingLayout;

    private RecyclerView commentsRecList;
    private RecyclerView.Adapter adapterCommentsResults;
    private RecyclerView.Adapter adapterTopPlates;
    private SearchView searchView;
    private CardView cardViewHint;
    private CardView cardViewPlate;
    private SwipeRefreshLayout swipeLayout;

    private DialogFragment newFragment;

    private String lastSearchSuggestion = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
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
        adapterCommentsResults = new AdapterComment(new ArrayList<Komentarze>(), MainSearchActivity.this);
        commentsRecList.setAdapter(adapterCommentsResults);

        swipeLayout.setEnabled(false);
        swipeLayout.setColorSchemeColors(getResources().getColor(R.color.theme_color_accent), getResources().getColor(R.color.theme_color), getResources().getColor(R.color.theme_color_dark));
        swipeLayout.setDistanceToTriggerSync(Integer.MAX_VALUE);//Do not allow user to pull to refresh

        setButtons(voteUp, voteDown);
        RegistrationPlateApplication.tracker.setScreenName("EntryScreen");
        RegistrationPlateApplication.tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        //Check if search is empty and there is no visible cards
        if(cardViewHint != null && cardViewHint.getVisibility() == View.VISIBLE) {
            getTopPlates();
        }
    }

    private void getTopPlates() {
        if(adapterTopPlates != null) {
            commentsRecList.setAdapter(adapterTopPlates);
            commentsRecList.setVisibility(View.VISIBLE);
            cardViewHint.setVisibility(View.GONE);
            cardViewPlate.setVisibility(View.GONE);
        } else {
            APIConnect.getTopDrivers(new Callback<ApiTopRegistrationPlates>() {
                @Override
                public void success(ApiTopRegistrationPlates apiTopRegistrationPlates, retrofit.client.Response response) {
                    //Show card with top registration plates
                    adapterTopPlates = new TopCardListAdapter((ArrayList<ApiTopRegistrationPlates.Result>) apiTopRegistrationPlates.results, MainSearchActivity.this, new Callback<String>() {
                        @Override
                        public void success(String s, retrofit.client.Response response) {
                            //click

                            RegistrationPlateApplication.tracker.setScreenName("SearchFromTop");
                            RegistrationPlateApplication.tracker.send(new HitBuilders.ScreenViewBuilder().build());
                            s = s.replace(" ", "");
                            search(s + "");
                        }

                        @Override
                        public void failure(RetrofitError error) {

                        }
                    });
                    if (apiTopRegistrationPlates.results != null && apiTopRegistrationPlates.results.size() >= 20) {
                        commentsRecList.setAdapter(adapterTopPlates);
                        commentsRecList.setVisibility(View.VISIBLE);
                        cardViewHint.setVisibility(View.GONE);
                        cardViewPlate.setVisibility(View.GONE);
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    Toast.makeText(MainSearchActivity.this, "Nieudane pobranie top tablic", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    public void onEvent(final EventPostCommentResult eventPostCommentResult) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(eventPostCommentResult.isSuccess) {
                    Toast.makeText(MainSearchActivity.this, eventPostCommentResult.message, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainSearchActivity.this, "Nieudane dodanie komentarza.", Toast.LENGTH_LONG).show();
                }
            }
        });
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

                RegistrationPlateApplication.tracker.setScreenName("SearchFromSubmitButton");
                RegistrationPlateApplication.tracker.send(new HitBuilders.ScreenViewBuilder().build());
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
        RegistrationPlateApplication.tracker.send(new HitBuilders.EventBuilder()
                .setCategory("ui_event")
                .setAction("search")
                .setLabel(plateNumber)
                .build());
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {
            Log.d(TAG, "search when displaying home button: " + e.getLocalizedMessage());
        }
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
        RegistrationPlateApplication.tracker.send(new HitBuilders.EventBuilder()
                .setCategory("ui_event")
                .setAction("vote")
                .setLabel(plateNumber)
                .setValue(value)
                .build());
        swipeLayout.setRefreshing(true);
        String voteUrl = APIConstants.TABLICE_INFO_PLATE + plateNumber + APIConstants.TABLICE_INFO_VOTE_ADD  + value;
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, voteUrl, null, new Response.Listener<JSONObject>() {

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
                        TextView voteDownText = (TextView) findViewById(R.id.voteDownText);
                        voteDownText.setText((currentTablica.getLapkiDol() + 1) + "");
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

        commentsRecList.setAdapter(adapterCommentsResults);
        ((AdapterComment) adapterCommentsResults).setCommentList(tablica.getKomentarze());
        adapterCommentsResults.notifyDataSetChanged();
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
                RegistrationPlateApplication.tracker.setScreenName("SearchSuggestion");
                RegistrationPlateApplication.tracker.send(new HitBuilders.ScreenViewBuilder().build());
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                getTopPlates();
                try {
                    RegistrationPlateApplication.tracker.send(new HitBuilders.EventBuilder()
                            .setCategory("ui_event")
                            .setAction("home_press")
                            .build());
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                } catch (Exception e) {
                    Log.d(TAG, "onOptionsItemSelected exception: " + e.getLocalizedMessage());
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
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
