package fi.benson.fleaapp;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.appyvet.rangebar.RangeBar;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.vstechlab.easyfonts.EasyFonts;


public class AdvancedSearch extends AppCompatActivity  {


    private RangeBar rangebar;
    TextView searchRange,searchCategory;
    private String category;
    private String minPrice,maxPrice;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_search);

        searchRange = (TextView) findViewById(R.id.search_range);
        searchCategory = (TextView) findViewById(R.id.search_category_tv);

        rangebar = (RangeBar) findViewById(R.id.rangebar);
        rangebar.setConnectingLineWeight(5);
        rangebar.setBarWeight(10);
        rangebar.setPinRadius(70);
        rangebar.setBarColor(Color.parseColor("#006767"));
        //rangebar.setTemporaryPins(true);
        rangebar.setTickStart(0);
        rangebar.setTickEnd(1000);
        rangebar.setTickInterval(10);
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

    }

}
