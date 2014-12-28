package pl.tajchert.tablicarejestracyjna;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_search);



        queue = Volley.newRequestQueue(this);

        fab = (AddFloatingActionButton) findViewById(R.id.normal_plus);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        LinearLayout voteUp = (LinearLayout) findViewById(R.id.voteUp);
        LinearLayout voteDown = (LinearLayout) findViewById(R.id.voteDown);

        voteUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick UPVOTE!");
            }
        });

        voteDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick DOWNVOTE!");
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_search, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setOnQueryTextListener(this);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_search) {
            return true;
        }*/


        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        if(s != null && s.length() > 0) {
            search(s);
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        if(s.length() >= 5){
            //We can assume it is correct plate number
        }
        return false;
    }

    private void search(String plateNumber) {
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, APIConstants.TABLICE_INFO_PLATE + plateNumber, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "onResponse response: " +response.toString());
                Gson gson = new Gson();
                Tablica tablica = gson.fromJson(response.toString(), Tablica.class);
                Log.d(TAG, "onResponse tablica: " + tablica);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse :" + error.getMessage());
            }
        });
        queue.add(jsObjRequest);
    }
}
