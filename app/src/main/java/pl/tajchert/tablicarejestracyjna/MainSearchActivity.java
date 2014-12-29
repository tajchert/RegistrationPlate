package pl.tajchert.tablicarejestracyjna;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
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
import com.getbase.floatingactionbutton.AddFloatingActionButton;
import com.google.gson.Gson;

import org.json.JSONObject;

import pl.tajchert.tablicarejestracyjna.api.Tablica;


public class MainSearchActivity extends ActionBarActivity implements SearchView.OnQueryTextListener {
    private static final String TAG = "MainSearchActivity";
    private AddFloatingActionButton fab;
    private RequestQueue queue;
    private boolean votedForThatPlate = false;
    private Tablica currentTablica;
    private LinearLayout buttonsVotingLayout;

    private RecyclerView commentsRecList;
    private SearchView searchView;
    private CardView cardViewHint;
    private CardView cardViewPlate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_search);
        queue = Volley.newRequestQueue(this);


        commentsRecList = (RecyclerView) findViewById(R.id.commentList);
        commentsRecList.setHasFixedSize(false);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        commentsRecList.setLayoutManager(llm);

        fab = (AddFloatingActionButton) findViewById(R.id.normal_plus);
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

        LinearLayout voteUp = (LinearLayout) findViewById(R.id.voteUp);
        LinearLayout voteDown = (LinearLayout) findViewById(R.id.voteDown);
        buttonsVotingLayout = (LinearLayout) findViewById(R.id.buttonsVoting);
        cardViewPlate = (CardView) findViewById(R.id.cardViewPlate);
        cardViewHint = (CardView) findViewById(R.id.cardViewHint);

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
    public boolean onQueryTextSubmit(String s) {
        if(s != null && s.length() > 0) {
            String searchPlate = s;
            searchPlate = s.toUpperCase();
            searchPlate = searchPlate.replace(" " , "");
            if(searchPlate.equals("WX37125")){
                setCustomCard();
            } else {
                search(searchPlate);
            }

        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        if(!s.equals(s.toUpperCase())){
            searchView.setQuery(s.toUpperCase(), false);
        }
        if(s.length() >= 5){
            //We can assume it is correct plate number
        }
        return false;
    }

    private void search(final String plateNumber) {
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, APIConstants.TABLICE_INFO_PLATE + plateNumber, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "onResponse response: " +response.toString());
                Gson gson = new Gson();
                Tablica tablica = gson.fromJson(response.toString(), Tablica.class);
                tablica.setId(plateNumber);
                if(tablica.getLapkiDol()== 0 && tablica.getLapkiGora()==0 && (tablica.getKomentarze()== null || tablica.getKomentarze().size()==0)){
                    setNoPlateView();
                } else {
                    currentTablica = tablica;
                    setPlateView(tablica);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse :" + error.getMessage());
            }
        });
        queue.add(jsObjRequest);
    }

    private void vote(String plateNumber, final int value) {
        // 1 - upvote, (-1) - downvote
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
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //NOT OK
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.vote_error), Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(jsObjRequest);
    }

    private void setPlateView(Tablica tablica) {
        cardViewPlate.setVisibility(View.VISIBLE);
        commentsRecList.setVisibility(View.VISIBLE);
        cardViewHint.setVisibility(View.GONE);

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

        RecyclerView.Adapter adapter = new AdapterComment(tablica.getKomentarze());
        commentsRecList.setAdapter(adapter);
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
    private void showCommentDialog(String plateID){
        DialogFragment newFragment = FragmentDialogComment.newInstance(plateID);
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
        return super.onCreateOptionsMenu(menu);
    }
}
