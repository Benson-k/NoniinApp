package fi.benson.fleaapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;
import com.vstechlab.easyfonts.EasyFonts;

import java.util.Date;

import me.kentin.yeti.Yeti;
import me.kentin.yeti.listener.OnShareListener;

public class DetailActivity extends AppCompatActivity implements OnMapReadyCallback  {

    ImageView dImageView;
    TextView dTextView, dPriceView, dDescView, dCategoryView, dCreatedView ,dConditionView;
    private GoogleMap mMap;

    FloatingActionButton floatingActionButton;
    private static final int REQUEST_CODE_YETI = 0;
    private Yeti yeti;
    private Intent shareIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");

        shareIntent.putExtra(Intent.EXTRA_SUBJECT, getIntent().getStringExtra("title").toUpperCase());
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Hi, checkout this out at Turku flea app.I think u will like it.");

        yeti = Yeti.with(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(yeti.share(shareIntent), REQUEST_CODE_YETI);
            }
        });

        dTextView = (TextView) findViewById(R.id.detail_title);
        dDescView = (TextView) findViewById(R.id.detail_desc);
        dPriceView = (TextView) findViewById(R.id.detail_price);
        dImageView = (ImageView) findViewById(R.id.detail_image);
        dCategoryView = (TextView) findViewById(R.id.detail_category);
        dCreatedView = (TextView) findViewById(R.id.detail_created);
        dConditionView= (TextView) findViewById(R.id.detail_condition);

        Picasso.with(this).load(getIntent().getStringExtra("url")).into(dImageView);

        dTextView.setText(getIntent().getStringExtra("title"));

        dPriceView.setText(Integer.toString(getIntent().getIntExtra("price", 0)) + " €");
        dPriceView.setTypeface(EasyFonts.droidSerifBold(this));

        dDescView.setText(getIntent().getStringExtra("desc"));
        dDescView.setTypeface(EasyFonts.droidSerifItalic(this));

        dCategoryView.setText( getIntent().getStringExtra("category"));
        dCategoryView.setTypeface(EasyFonts.ostrichBlack(this));

        dConditionView.setText(getIntent().getStringExtra("condition"));
        dConditionView.setTypeface(EasyFonts.ostrichBlack(this));

        Date date = new Date();
        date.setTime(getIntent().getLongExtra("date", -1));
        dCreatedView.setText(date.toString());
        dCreatedView.setTypeface(EasyFonts.ostrichBlack(this));

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        double lat = getIntent().getDoubleExtra("latitude", 0);
        double lon = getIntent().getDoubleExtra("longitude", 0);

        LatLng thePosition = new LatLng(lat, lon);

        CameraPosition googlePlex = CameraPosition.builder()
                .target(thePosition)
                .zoom(12)
                .bearing(0)
                .tilt(45)
                .build();

        mMap.addMarker(new MarkerOptions().position(thePosition).title(getIntent().getStringExtra("address")));
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(googlePlex));

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_YETI) {
            yeti.result(data, shareListener);
        }
    }

    OnShareListener shareListener = new OnShareListener() {
        @Override
        public boolean shareWithFacebook(Intent intent) {
            return false;
        }

        @Override
        public boolean shareWithTwitter(Intent intent) {

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.removeExtra(Intent.EXTRA_TEXT);
            intent.putExtra(Intent.EXTRA_TEXT, "BOOM this is what's actually going to be shared.");

            startActivity(intent);
            return true; // true = you have changed the intent, prevent the system from firing the old intent
        }

        @Override
        public boolean shareWithGooglePlus(Intent intent) {
            return false; // false = let the system handle it the usual way
        }

        @Override
        public boolean shareWithEmail(Intent intent) {
            return false;
        }

        @Override
        public boolean shareWithSms(Intent intent) {
            return false;
        }

        @Override
        public boolean shareWithOther(Intent intent) {
            return false;
        }
    };
}