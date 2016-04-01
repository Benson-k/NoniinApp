package fi.benson.fleaapp;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.appyvet.rangebar.RangeBar;
import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.vstechlab.easyfonts.EasyFonts;

import java.util.ArrayList;
import java.util.List;

import fi.benson.fleaapp.adapters.PostAdapter;
import fi.benson.fleaapp.models.Post;


public class AdvancedSearch extends AppCompatActivity {


    private RangeBar rangebar;
    TextView searchRange,searchCategory;
    private String category;
    private String minPrice,maxPrice;
    EditText editQuery;
    public String query;
    private RecyclerView recycler;
    private StaggeredGridLayoutManager mStaggeredLayoutManager;
    private PostAdapter adapter;
    private List<Post> searchPosts = new ArrayList<>();
    private BackendlessCollection<Post> searchPost;
    private int totalResults;
    Button searchButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_search);

        searchButton = (Button) findViewById(R.id.avd_btn);
        searchRange = (TextView) findViewById(R.id.search_range);
        searchCategory = (TextView) findViewById(R.id.search_category_tv);
        editQuery = (EditText) findViewById(R.id.advanced_search_view);


        recycler = (RecyclerView) findViewById(R.id.advAearchRecycler);
        mStaggeredLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recycler.setItemAnimator(new DefaultItemAnimator());
        recycler.setLayoutManager(mStaggeredLayoutManager);
        recycler.setHasFixedSize(true);
        adapter = new PostAdapter(this, searchPosts);
        recycler.setAdapter(adapter);

        rangebar = (RangeBar) findViewById(R.id.rangebar);
        rangebar.setConnectingLineWeight(5);
        rangebar.setBarWeight(10);
        rangebar.setPinRadius(70);
        rangebar.setBarColor(Color.parseColor("#006767"));
        //rangebar.setTemporaryPins(true);
        rangebar.setTickStart(0);
        rangebar.setTickEnd(100);
        rangebar.setTickInterval(5);
        rangebar.setSelectorColor(Color.parseColor("#006767"));
        rangebar.setPinColor(Color.parseColor("#006767"));

        // Sets the display values of the indices
        rangebar.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex,
                                              int rightPinIndex,
                                              String leftPinValue, String rightPinValue) {
                minPrice = leftPinValue;
                maxPrice = rightPinValue;

                searchRange.setText(leftPinValue + "€" + " - " + rightPinValue + "€");
                searchRange.setTypeface(EasyFonts.droidSerifBold(AdvancedSearch.this));


            }

        });

        //category spinner
        String[] list = getResources().getStringArray(R.array.categories);
        MaterialSpinner spinner = (MaterialSpinner) findViewById(R.id.search_spinner_category);
        spinner.setItems(list);
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                category = item;
                searchCategory.setText(category);
                searchCategory.setTypeface(EasyFonts.droidSerifBold(AdvancedSearch.this));
            }
        });
        spinner.setOnNothingSelectedListener(new MaterialSpinner.OnNothingSelectedListener() {

            @Override
            public void onNothingSelected(MaterialSpinner spinner) {

            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                query = editQuery.getEditableText().toString();
                pullAdvancedSearch();
                Toast.makeText(AdvancedSearch.this, searchQuery(),Toast.LENGTH_LONG).show();
            }
        });

    }

    private void addMoreItems(BackendlessCollection<Post> nextPage) {
        searchPosts.addAll(nextPage.getCurrentPage());
        adapter.notifyDataSetChanged();

    }

    private void clearItems() {
        searchPosts.clear();
        adapter.notifyDataSetChanged();
    }



    private String searchQuery (){
        String searchQuery = "title = 'Lonkero'";

        StringBuilder str = new StringBuilder(" title LIKE ");
        if (query != null){
            str.append("'%").append(query).append("%'");
            searchQuery = str.toString();
        }
        if (minPrice != null && maxPrice != null){
            str.append(" AND price > " + minPrice + " AND price < " + maxPrice);
            searchQuery = str.toString();
        }
        if (category != null){
            str.append(" AND category = ");
            str.append("'").append(category).append("'");

            searchQuery = str.toString();
        }
        return searchQuery;
    }



    public void pullAdvancedSearch(){

        BackendlessDataQuery dataQuery = new BackendlessDataQuery();

        dataQuery.setWhereClause(searchQuery());

        Backendless.Data.of(Post.class).find(dataQuery, new AsyncCallback<BackendlessCollection<Post>>() {
            @Override
            public void handleResponse(BackendlessCollection<Post> searchResult) {
                totalResults = searchResult.getTotalObjects();
                clearItems();
                searchPost = searchResult;
                addMoreItems(searchResult);
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                //Snackbar.make(coordinatorLayout, backendlessFault.toString(), Snackbar.LENGTH_LONG).show();
            }
        });
    }


}
