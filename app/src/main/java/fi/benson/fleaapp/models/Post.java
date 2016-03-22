package fi.benson.fleaapp.models;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.persistence.BackendlessDataQuery;

public class Post {
    private java.util.Date created;
    private String description;
    private String title;
    private String url;
    private String address;
    private String ownerId;
    private java.util.Date updated;
    private int price;
    private String objectId;
    private  double latitude;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    private String category;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    private double longitude;

    public static Post findById(String id) {
        return Backendless.Data.of(Post.class).findById(id);
    }

    public static void findByIdAsync(String id, AsyncCallback<Post> callback) {
        Backendless.Data.of(Post.class).findById(id, callback);
    }

    public static Post findFirst() {
        return Backendless.Data.of(Post.class).findFirst();
    }

    public static void findFirstAsync(AsyncCallback<Post> callback) {
        Backendless.Data.of(Post.class).findFirst(callback);
    }

    public static Post findLast() {
        return Backendless.Data.of(Post.class).findLast();
    }

    public static void findLastAsync(AsyncCallback<Post> callback) {
        Backendless.Data.of(Post.class).findLast(callback);
    }

    public static BackendlessCollection<Post> find(BackendlessDataQuery query) {
        return Backendless.Data.of(Post.class).find(query);
    }

    public static void findAsync(BackendlessDataQuery query, AsyncCallback<BackendlessCollection<Post>> callback) {
        Backendless.Data.of(Post.class).find(query, callback);
    }

    public java.util.Date getCreated() {
        return created;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public java.util.Date getUpdated() {
        return updated;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getObjectId() {
        return objectId;
    }

    public Post save() {
        return Backendless.Data.of(Post.class).save(this);
    }

    public void saveAsync(AsyncCallback<Post> callback) {
        Backendless.Data.of(Post.class).save(this, callback);
    }

    public Long remove() {
        return Backendless.Data.of(Post.class).remove(this);
    }

    public void removeAsync(AsyncCallback<Long> callback) {
        Backendless.Data.of(Post.class).remove(this, callback);
    }
}