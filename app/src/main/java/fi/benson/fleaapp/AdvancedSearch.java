package fi.benson.fleaapp;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.appyvet.rangebar.RangeBar;


public class AdvancedSearch extends AppCompatActivity {


    private RangeBar rangebar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_search);

        rangebar = (RangeBar) findViewById(R.id.rangebar);
        rangebar.setConnectingLineWeight(5);
        rangebar.setBarWeight(10);
        rangebar.setPinRadius(50);
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

            }

        });




    }
}
