package fi.benson.fleaapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.vstechlab.easyfonts.EasyFonts;

import java.util.ArrayList;
import java.util.List;

import fi.benson.fleaapp.DetailActivity;
import fi.benson.fleaapp.R;
import fi.benson.fleaapp.models.Post;


/**
 * Created by benson on 3/14/16.
 */
public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    Bitmap myBitmap;
    private List<Post> list;
    private Context context;

    public PostAdapter(Context context, List<Post> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public PostAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_places, parent, false);
        return new ViewHolder(view, context, list);
    }

    @Override
    public void onBindViewHolder(final PostAdapter.ViewHolder holder, int position) {

        final Post post = list.get(position);

        // Picasso.with(context).load(post.getUrl()).into(holder.placeImage);

        holder.placeName.setText(post.getTitle());
        holder.placeName.setTypeface(EasyFonts.walkwayBlack(context));

        Picasso.with(context)
                .load(post.getUrl())
                .into(holder.placeImage, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Log.v("Picasso", "Could not fetch image");
                    }
                });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        //Setup Views
        public LinearLayout placeHolder;
        public LinearLayout placeNameHolder;
        public TextView placeName;
        public ImageView placeImage;


        List<Post> itemPosts = new ArrayList<Post>();
        Context ctx;


        public ViewHolder(final View itemView, Context ctx, List<Post> itemPosts) {

            super(itemView);
            this.itemPosts = itemPosts;
            this.ctx = ctx;

            itemView.setOnClickListener(this);
            //Assign views by ID
            placeHolder = (LinearLayout) itemView.findViewById(R.id.mainHolder);
            placeName = (TextView) itemView.findViewById(R.id.placeName);
            placeNameHolder = (LinearLayout) itemView.findViewById(R.id.placeNameHolder);
            placeImage = (ImageView) itemView.findViewById(R.id.placeImage);


        }


        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Post itemPost = this.itemPosts.get(position);

            Intent intent = new Intent(this.ctx, DetailActivity.class);
            intent.putExtra("url", itemPost.getUrl());
            intent.putExtra("title", itemPost.getTitle());
            intent.putExtra("price", itemPost.getPrice());
            intent.putExtra("desc", itemPost.getDescription());
            intent.putExtra("address", itemPost.getAddress());
            intent.putExtra("latitude", itemPost.getLatitude());
            intent.putExtra("longitude", itemPost.getLongitude());


            ctx.startActivity(intent);

        }
    }


}



