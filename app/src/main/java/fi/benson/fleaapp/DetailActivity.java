package fi.benson.fleaapp;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.vstechlab.easyfonts.EasyFonts;

public class DetailActivity extends AppCompatActivity {

    private ImageView mImageView;
    private TextView mTitle;
    private LinearLayout mTitleHolder;


    ImageView dImageView;
    LinearLayout placeHolder;
    LinearLayout placeNameHolder;
    TextView dTextView,dPriceView, dDescView, addressView;
    Bitmap loadedBitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);


         placeHolder = (LinearLayout) findViewById(R.id.mainHolder);
         placeNameHolder = (LinearLayout) findViewById(R.id.placeNameHolder);

         dTextView = (TextView) findViewById(R.id.detailTextView);
        addressView = (TextView) findViewById(R.id.addressTextView);
        dDescView = (TextView) findViewById(R.id.descTextview);
         dPriceView = (TextView) findViewById(R.id.priceView);
         dImageView = (ImageView) findViewById(R.id.placeImage2);

        Picasso.with(this).load(getIntent().getStringExtra("url")).into(dImageView);

        dTextView.setText(getIntent().getStringExtra("title"));

        dPriceView.setText(Integer.toString(getIntent().getIntExtra("price", 0)) + " €");
        dPriceView.setTypeface(EasyFonts.droidSerifBold(this));

        dDescView.setText(getIntent().getStringExtra("desc"));
        dDescView.setTypeface(EasyFonts.droidSerifItalic(this));

        addressView.setText(getIntent().getStringExtra("address"));
        addressView.setTypeface(EasyFonts.caviarDreams(this));

    }
}
