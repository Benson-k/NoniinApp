package fi.benson.fleaapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.internal.NavigationMenu;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.yalantis.phoenix.PullToRefreshView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import fi.benson.fleaapp.adapters.PostAdapter;
import fi.benson.fleaapp.defaults.Defaults;
import fi.benson.fleaapp.models.Post;
import io.github.yavski.fabspeeddial.FabSpeedDial;
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private static final String APP_ID = "AIzaSyCtVdHPf7j1OmH_BUhEp1tlqRNS_F6BPpQ";
    private static final int PERMISSION_ACCESS_COARSE_LOCATION = 201;
    private static final int PERMISSION_ACCESS_CAMERA = 202;
    private static final int PERMISSION_ACCESS_STORAGE = 203;
    private static ExpandableListView expandableListView;
    private static ExpandableListAdapter drawerAdapter;
    public String myReturnedAddress;
    MaterialSearchView searchView;
    CoordinatorLayout coordinatorLayout;
    NavigationView navigationView;
    private PostAdapter adapter;
    private List<Post> posts = new ArrayList<>();
    private BackendlessCollection<Post> post;
    private RecyclerView recycler;
    private Uri fileUri;
    private boolean isListView = true;
    private StaggeredGridLayoutManager mStaggeredLayoutManager;
    DrawerLayout drawer;
    private int totalResults;
    private int totalPosts;
    private PullToRefreshView mPullToRefreshView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorlayout);


        pullDataFromServer();
        materialSearch();

        recycler = (RecyclerView) findViewById(R.id.mainRecycler);
        mStaggeredLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recycler.setItemAnimator(new DefaultItemAnimator());
        recycler.setLayoutManager(mStaggeredLayoutManager);
        recycler.setHasFixedSize(true);
        adapter = new PostAdapter(this, posts);
        recycler.setAdapter(adapter);






        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

         drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);

        toggle.syncState();


        navigationView = (NavigationView) findViewById(R.id.nav_view);
      //  navigationView.inflateMenu(R.menu.activity_main_drawer);
        navigationView.setNavigationItemSelectedListener(this);
       // drawer.closeDrawer(GravityCompat.START);

        checkPermisions();


        //Pull to refresh
        mPullToRefreshView = (PullToRefreshView) findViewById(R.id.pull_to_refresh);
        mPullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {

            @Override
            public void onRefresh() {

                mPullToRefreshView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        clearItems();
                        pullDataFromServer();
                        mPullToRefreshView.setRefreshing(false);
                    }
                }, 2000);

            }
        });


        FabSpeedDial fabSpeedDial = (FabSpeedDial) findViewById(R.id.fabspeed);
        assert fabSpeedDial != null;
        fabSpeedDial.setMenuListener(new SimpleMenuListenerAdapter() {
            @Override
            public boolean onPrepareMenu(NavigationMenu navigationMenu) {
                checkPermisions();
                return true;
            }

            @Override
            public boolean onMenuItemSelected(MenuItem menuItem) {
                int id = menuItem.getItemId();


                if (id == R.id.action_manage) {
                    pickFromGallery();
                } else if (id == R.id.action_gallery) {
                    pickFromGallery();
                } else if (id == R.id.action_camera) {
                    captureImage();
                }
                return false;
            }
        });


    }




    // Toggle btwn grid n list views
    private void toggle() {

        if (isListView) {
            mStaggeredLayoutManager.setSpanCount(2);
            isListView = false;
        } else {
            mStaggeredLayoutManager.setSpanCount(1);
            isListView = true;
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent intent;

        if (id == R.id.nav_category) {
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.activity_submenu_drawer);
        } else if (id == R.id.back_to_main) {
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.activity_main_drawer);
        }else if (id == R.id.nav_profile){
            drawer.closeDrawer(GravityCompat.START);
        }else if (id == R.id.nav_settings){
          //  intent = new Intent(MainActivity.this, SettingsActivity.class);
          //  startActivity(intent);
            drawer.closeDrawer(GravityCompat.START);
        }else if (id == R.id.nav_favorite){
            drawer.closeDrawer(GravityCompat.START);
        }else if (id == R.id.nav_search){
            intent = new Intent(MainActivity.this, AdvancedSearch.class);
            startActivity(intent);
            drawer.closeDrawer(GravityCompat.START);
        }else if (id == R.id.nav_list){
            toggle();
            drawer.closeDrawer(GravityCompat.START);
        }else if (id == R.id.nav_grid){
            toggle();
            drawer.closeDrawer(GravityCompat.START);
        }else if (id == R.id.sub_electronics){
            String category = "Electronics";
            reloadDrawerAndPullCategory(category);
        }else if (id == R.id.sub_games){
            String category = "Games & Console";
            reloadDrawerAndPullCategory(category);
        }else if (id == R.id.sub_cars){
            String category = "Books & Music";
            reloadDrawerAndPullCategory(category);
        }else if (id == R.id.sub_books){
            String category = "Clothing & Fashion";
            reloadDrawerAndPullCategory(category);
        }else if (id == R.id.sub_clothing){
            String category = "Cars & Motors";
            reloadDrawerAndPullCategory(category);
        }else if (id == R.id.sub_sports){
            String category = "Sports & Leisure";
            reloadDrawerAndPullCategory(category);
        }else if (id == R.id.sub_home){
            String category = "Home & Garden";
            reloadDrawerAndPullCategory(category);
        }

        return true;
    }

    private void addMoreItems(BackendlessCollection<Post> nextPage) {
        posts.addAll(nextPage.getCurrentPage());
        adapter.notifyDataSetChanged();

    }

    private void clearItems() {
        posts.clear();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1) {
            Uri path;
            int type;
            if (requestCode == Defaults.PICK_IMAGE) {
                path = data.getData();
                type = 50;
            } else {
                path = fileUri;
                type = 51;
            }
            Intent i = new Intent(MainActivity.this, UploadActivity.class);
            i.putExtra("extras", path);
            i.putExtra("image_from", type);
            i.putExtra("address", myReturnedAddress);
            startActivity(i);
            finish();
        }
    }


    public void pullDataFromServer() {
        Backendless.Persistence.of(Post.class).find(new AsyncCallback<BackendlessCollection<Post>>() {
            @Override
            public void handleResponse(BackendlessCollection<Post> foundPosts) {
                totalPosts = foundPosts.getTotalObjects();
                post = foundPosts;
                addMoreItems(foundPosts);


            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Log.d("Post", "Error: " + fault.getMessage());
            }
        });
    }
    public void reloadDrawerAndPullCategory(String category) {
        drawer.closeDrawer(GravityCompat.START);
        navigationView.getMenu().clear();
        navigationView.inflateMenu(R.menu.activity_main_drawer);

        StringBuilder str = new StringBuilder("category = ");
        str.append("'").append(category).append("'");

        String whereClause = "" + str;
        BackendlessDataQuery dataQuery = new BackendlessDataQuery();
        dataQuery.setWhereClause(whereClause);
        Backendless.Persistence.of(Post.class).find(dataQuery, new AsyncCallback<BackendlessCollection<Post>>() {
            @Override
            public void handleResponse(BackendlessCollection<Post> response) {
                clearItems();
                post = response;
                addMoreItems(response);
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Snackbar.make(coordinatorLayout, fault.toString(), Snackbar.LENGTH_LONG).show();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        adapter.notifyDataSetChanged();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        } else {
            super.onBackPressed();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.button_menu, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);

        return true;
    }

    public void materialSearch() {

        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        assert searchView != null;
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Do some magic

                return false;
            }


            @Override
            public boolean onQueryTextChange(String newText) {
                //Do some magic
                BackendlessDataQuery dataQuery = new BackendlessDataQuery();

                StringBuilder str = new StringBuilder(" title LIKE ");
                str.append("'").append(newText).append("%'");

                dataQuery.setWhereClause("" + str);

                Backendless.Data.of(Post.class).find(dataQuery, new AsyncCallback<BackendlessCollection<Post>>() {
                    @Override
                    public void handleResponse(BackendlessCollection<Post> result) {
                        totalResults = result.getTotalObjects();
                        clearItems();
                        post = result;
                        addMoreItems(result);
                    }

                    @Override
                    public void handleFault(BackendlessFault backendlessFault) {
                        Snackbar.make(coordinatorLayout, backendlessFault.toString(), Snackbar.LENGTH_LONG).show();
                    }
                });
                return false;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic
                pullDataFromServer();
            }
        });
    }


    /***
     * Get the image below
     */

    private void pickFromGallery() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, ""), Defaults.PICK_IMAGE);
        }
    }

    private void captureImage() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            fileUri = getOutputMediaFileUri();
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            // start the image capture Intent
            startActivityForResult(intent, Defaults.CAPTURE_IMAGE);
        }

    }
    /**
     * Create a File for saving an image or video
     */
    private static File getOutputMediaFile() {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), Defaults.IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(Defaults.IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                        + Defaults.IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile = null;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + timeStamp + ".png");
        return mediaFile;
    }

    /**
     * Create a file Uri for saving an image or video
     */
    public Uri getOutputMediaFileUri() {

        return Uri.fromFile(getOutputMediaFile());
    }

    public void checkPermisions() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSION_ACCESS_COARSE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    PERMISSION_ACCESS_CAMERA);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_ACCESS_STORAGE);
        }
    }

    /**
     * get the user address location
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ACCESS_COARSE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // All good!
                } else {
                    Toast.makeText(this, "Need your location!", Toast.LENGTH_SHORT).show();
                }

                break;
            case PERMISSION_ACCESS_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // All good!
                } else {
                    Toast.makeText(this, "Can't access Camera!", Toast.LENGTH_SHORT).show();
                }
                break;
            case PERMISSION_ACCESS_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // All good!
                } else {
                    Toast.makeText(this, "Can't access Media Storage!", Toast.LENGTH_SHORT).show();
                }
                break;

        }
    }

}
