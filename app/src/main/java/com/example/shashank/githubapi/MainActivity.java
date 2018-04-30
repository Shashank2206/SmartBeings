package com.example.shashank.githubapi;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.GridLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.Response;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    //The Only Activity that takes care of all the application's functionalities.
    private List<GitUser> userList = new ArrayList<>(); //The List of User details that is to be used by the recycler view
    private RecyclerView recyclerView; //A recycler view to be efficient with the display of multiple data points.
    private GridLayout gridLayout;

    //Constants required to construct the GET Request URLs.
    private String BASE_URL = "https://api.github.com/search/users?q=followers:%3E0";
    private String SEARCH_USER = "https://api.github.com/users/";
    private String PER_PAGE = "&per_page=";
    private String PAGE_NUM = "&page=";

    //Variable and Constants to maintain page traversal.
    private int currentPage = 1;
    private int MAX_RECORDS = 1000;
    private int NUM_RESULTS_PER_PAGE = 20;
    private int MAX_PAGE = (int)(MAX_RECORDS / NUM_RESULTS_PER_PAGE); // As GITHUB only allows maximum of 1000 records
    private int MIN_PAGE = 1;

    private SearchView searchView;
    private GitAdapter gitAdapter;
    private TextView pageNumber ;
    private FloatingActionButton search;
    RequestQueue requestQueue; // In order to queue requests as they pile up and execute them in FIFO.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler_view);
        gitAdapter = new GitAdapter(userList , R.layout.git_profiles , getApplicationContext());
        requestQueue = Volley.newRequestQueue(this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        search = findViewById(R.id.fab);
        recyclerView.setAdapter(gitAdapter);
        gridLayout = findViewById(R.id.page_controller);
        searchView = findViewById(R.id.search_user);
        pageNumber = findViewById(R.id.page_num);

        //Calling the function that handles top user functionality.
        getGithubUsers(1 , NUM_RESULTS_PER_PAGE);


        //Search query handling.
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                carrySearch(searchView);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });
    }



    public void modifyUserList(JSONArray allUsers) throws JSONException {
        //Handles modification of UserList based on the JSONArray returned in the form of response.
        userList.clear();
        for(int pos = 0 ; pos < allUsers.length() ; pos++){
            String gitUser = allUsers.getJSONObject(pos).get("login").toString();
            String profilePicture = allUsers.getJSONObject(pos).get("avatar_url").toString();
            userList.add(new GitUser(gitUser , profilePicture));
        }
        gitAdapter.notifyDataSetChanged();
    }

    public void getGithubUsers(int pageNum , int perPage){
        //Handles retrieval of data from github
        String requestURL = BASE_URL + PER_PAGE + Integer.toString(perPage) + PAGE_NUM + Integer.toString(pageNum);
        userList.clear();
        Log.v("getGithubUsers" , requestURL);
        JsonObjectRequest arrReq = new JsonObjectRequest(Request.Method.GET, requestURL,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        // Check the length of our response (to see if the user has any repos)
                        if(response.length()>0) {
                            try {
//                            j = response.getJSONObject("items");
                                String totalCount = response.get("total_count").toString();
                                Log.v("Shaalu" , totalCount);

                                JSONArray allItems = (JSONArray) response.get("items");
                                modifyUserList(allItems);

                            } catch (JSONException e) {
                                Log.v("JSONException" , e.getMessage());
                            }

                        } else {

                            Log.v("JSONException" , "No Data");                        }

                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // If there a HTTP error then add a note to our repo list.
                        Log.e("Volley", error.toString());
                    }
                }
        );
        requestQueue.add(arrReq);
    }

    public void searchGithubForUser(String name){
        //Handles searching github based on a user handle.
        String requestURL = SEARCH_USER + name;
        JsonObjectRequest arrReq = new JsonObjectRequest(Request.Method.GET, requestURL,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        // Check the length of our response (to see if the user has any repos)
                        if(response.length()>0) {
                            try {
//                            j = response.getJSONObject("items");
                                JSONObject allItems = (JSONObject) response;
                                userList.clear();
                                userList.add(new GitUser(allItems.get("login").toString() , allItems.get("avatar_url").toString()));
                                gitAdapter.notifyDataSetChanged();

                            } catch (JSONException e) {
                                Log.v("JSONException" , e.getMessage());
                                Toast.makeText(MainActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                            }

                        } else {

                            Log.v("JSONException" , "No Data");                        }

                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // If there a HTTP error then add a note to our repo list.
                        Log.e("Volley", error.toString());
                    }
                }
        );
        requestQueue.add(arrReq);
    }

    public void moveToNextPage(View view) {
        //Handles moving the search results to the next page.
        if(currentPage < MAX_PAGE){
            currentPage += 1;
            getGithubUsers(currentPage , NUM_RESULTS_PER_PAGE);
            Log.v("NEXT" , "Fetching data");
            pageNumber.setText(Integer.toString(currentPage));
        }
        else{
            Toast.makeText(this, "At Max Page", Toast.LENGTH_SHORT).show();
        }
    }

    public void moveToPreviousPage(View view) {
        //Handles moving the search results to the previous page.
        if(currentPage > MIN_PAGE){
            currentPage -= 1;
            getGithubUsers(currentPage , NUM_RESULTS_PER_PAGE);
            Log.v("PREV" , "Fetching data");
            pageNumber.setText(Integer.toString(currentPage));
        }
        else{
            Toast.makeText(this, "At Min Page", Toast.LENGTH_SHORT).show();
        }
    }


    public void initiateSearch(View view) {
        if(recyclerView.getVisibility() == View.VISIBLE) {
            recyclerView.setVisibility(View.INVISIBLE);
            gridLayout.setVisibility(View.INVISIBLE);
            searchView.setVisibility(View.VISIBLE);

            searchView.setActivated(true);
            searchView.setQueryHint("Type your keyword here");
            searchView.onActionViewExpanded();
            searchView.setIconified(false);
            searchView.clearFocus();
        }
        else{
            recyclerView.setVisibility(View.VISIBLE);
            gridLayout.setVisibility(View.VISIBLE);
            searchView.setVisibility(View.GONE);
        }
    }

    public void carrySearch(View view) {
        recyclerView.setVisibility(View.VISIBLE);
        gridLayout.setVisibility(View.VISIBLE);
        searchView.setVisibility(View.GONE);

        Log.v("SEARCH : " , searchView.getQuery().toString());
        searchGithubForUser(searchView.getQuery().toString());
    }
}
