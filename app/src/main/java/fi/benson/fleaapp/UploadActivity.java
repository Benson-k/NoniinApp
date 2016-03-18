package fi.benson.fleaapp;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.files.BackendlessFile;

import java.util.UUID;

import fi.benson.fleaapp.models.Post;

public class UploadActivity extends AppCompatActivity {

    CollapsingToolbarLayout collapsingToolbarLayout;
    ImageView postimage;
    EditText editText_title, editText_desc, editText_price, editText_password, editText_phone;
    Button submit;
    String theUrl;
    Bitmap thebitmap;
    String address;
    private String selectedImagePath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);


        postimage = (ImageView) findViewById(R.id.imageViewdetail);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        address = getIntent().getStringExtra("address");
        Uri extras = getIntent().getParcelableExtra("extras");
        int image_from = getIntent().getIntExtra("image_from", 0);
        extractImage(extras, image_from);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle("Flea App");
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));

        editText_desc = (EditText) findViewById(R.id.et_desc);
        editText_title = (EditText) findViewById(R.id.et_title);
        editText_price = (EditText) findViewById(R.id.et_price);
        submit = (Button) findViewById(R.id.btn_post);

        uploadit();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataObjectUpload();

            }
        });

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
                Toast.makeText(UploadActivity.this, "image success" + backendlessFile.getFileURL(), Toast.LENGTH_SHORT).show();
                theUrl = backendlessFile.getFileURL();
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                Toast.makeText(UploadActivity.this, backendlessFault.toString(), Toast.LENGTH_LONG).show();
            }
        });

    }


    // save object asynchronously
    private void dataObjectUpload() {

        final Post post = new Post();
        post.setTitle(editText_title.getText().toString());
        post.setPrice(Integer.parseInt(editText_price.getText().toString()));
        post.setDescription(editText_desc.getText().toString());
        post.setAddress(address);
        post.setUrl(theUrl);

        Backendless.Persistence.save(post, new AsyncCallback<Post>() {
            public void handleResponse(Post response) {
                Toast.makeText(UploadActivity.this, " data success", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(UploadActivity.this, MainActivity.class);
                startActivity(intent);
                finish();

            }

            public void handleFault(BackendlessFault fault) {
                Toast.makeText(UploadActivity.this, fault.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
