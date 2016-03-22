package fi.benson.fleaapp;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.files.BackendlessFile;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

import fi.benson.fleaapp.location.LocationTracker;
import fi.benson.fleaapp.location.UserLocation;
import fi.benson.fleaapp.models.Post;

public class UploadActivity extends AppCompatActivity {

    CollapsingToolbarLayout collapsingToolbarLayout;
    ImageView postimage;
    EditText editText_title, editText_desc, editText_price;
    Button submit;
    String theUrl;
    Bitmap thebitmap;


    LocationTracker tracker;
    double latitude, longitude;
    String address;



    private String selectedImagePath = "";
    String category;
    private CoordinatorLayout coordinatorLayout;
    TextView tv;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

    ;
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayoutUpload);
        postimage = (ImageView) findViewById(R.id.imageViewdetail);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        address = getIntent().getStringExtra("address");
        Uri extras = getIntent().getParcelableExtra("extras");
        int image_from = getIntent().getIntExtra("image_from", 0);
        extractImage(extras, image_from);

        String[] list = getResources().getStringArray(R.array.categories);
        MaterialSpinner spinner = (MaterialSpinner) findViewById(R.id.spinner1);
        spinner.setItems(list);
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                category = item;

            }
        });
        spinner.setOnNothingSelectedListener(new MaterialSpinner.OnNothingSelectedListener() {

            @Override
            public void onNothingSelected(MaterialSpinner spinner) {
                Snackbar.make(spinner, "Nothing selected", Snackbar.LENGTH_LONG).show();
            }
        });


        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        assert collapsingToolbarLayout != null;
        collapsingToolbarLayout.setTitle("Flea App");
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));

        editText_desc = (EditText) findViewById(R.id.et_desc);
        editText_title = (EditText) findViewById(R.id.et_title);
        editText_price = (EditText) findViewById(R.id.et_price);
        submit = (Button) findViewById(R.id.btn_post);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (category.equals("Pick category")){
                    tv = (TextView) findViewById(R.id.textView8);
                    tv.setTextColor(Color.parseColor("#FF0000"));
                    Snackbar.make(coordinatorLayout, "Please pick a category", Snackbar.LENGTH_LONG).show();
                }else {
                    dataObjectUpload();
                }


            }
        });
        getLocation();
        uploadit();



    }

    private void extractImage(Uri data, int image_from) {
        if (image_from == 50)
            selectedImagePath = getAbsolutePath(data);
        else if (image_from == 51)
            selectedImagePath = data.getPath();

        Bitmap bitmap = decodeFile(selectedImagePath);
        postimage.setImageBitmap(bitmap);
        thebitmap = bitmap;
    }

    public Bitmap decodeFile(String path) {
        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, o);
            // The new size we want to scale to
            final int REQUIRED_SIZE = 480;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE
                    && o.outHeight / scale / 2 >= REQUIRED_SIZE)
                scale *= 2;

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeFile(path, o2);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;

    }

    public String getAbsolutePath(Uri uri) {
        String[] projection = {MediaStore.MediaColumns.DATA};
        @SuppressWarnings("deprecation")
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }

    //SAVE IMAGE ASYNC
    private void uploadit() {
        String picName = UUID.randomUUID().toString();
        Backendless.Files.Android.upload(thebitmap, Bitmap.CompressFormat.PNG, 100, picName + ".png", "postImages", new AsyncCallback<BackendlessFile>() {

            @Override
            public void handleResponse(final BackendlessFile backendlessFile) {
                //Toast.makeText(UploadActivity.this, "image success" + backendlessFile.getFileURL(), Toast.LENGTH_SHORT).show();
                theUrl = backendlessFile.getFileURL();
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                Snackbar.make(coordinatorLayout, backendlessFault.toString(), Snackbar.LENGTH_LONG).show();
            }
        });

    }

    public void getLocation(){
        tracker=new LocationTracker(UploadActivity.this);

        // check if location is available
        if(tracker.isLocationEnabled)
        {
            latitude=tracker.getLatitude();
            longitude=tracker.getLongitude();
            address = getCompleteAddressString(latitude, longitude);

            UserLocation userLocation = new UserLocation();
            userLocation.setLatitude(latitude);
            userLocation.setLongitude(longitude);
            userLocation.setAddress(address);

        }
        else
        {
            // show dialog box to user to enable location
            tracker.askToOnLocation();
        }
    }

    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder
                    .getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                android.location.Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress
                            .append(returnedAddress.getAddressLine(i)).append(
                            "\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.w(" location address", "" + strReturnedAddress.toString());
            } else {
                Log.w(" location address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w(" location address", "Cannot get Address!");
        }
        return strAdd;
    }


    // save object asynchronously
    private void dataObjectUpload() {
        UserLocation userLocation = new UserLocation();


        final Post post = new Post();
        post.setTitle(editText_title.getText().toString());
        post.setPrice(Integer.parseInt(editText_price.getText().toString()));
        post.setDescription(editText_desc.getText().toString());
        post.setAddress(address);
        post.setUrl(theUrl);
        post.setLatitude(latitude);
        post.setLongitude(longitude);
        post.setCategory(category);


        Backendless.Persistence.save(post, new AsyncCallback<Post>() {
            public void handleResponse(Post response) {
                Intent intent = new Intent(UploadActivity.this, MainActivity.class);
                startActivity(intent);
                finish();

            }

            public void handleFault(BackendlessFault fault) {
                Snackbar.make(coordinatorLayout, fault.toString(), Snackbar.LENGTH_LONG).show();
            }
        });
    }







}
