package com.example.android.booklisting;
/**
 * Created by ursum on 08/07/2017.
 */
import android.os.AsyncTask;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;


/**
 * FetchTask is fetching the Google Books API, getting the results, parsing it and building the structure
 * an ArrayList of Book Objects and returning the result
 */
class FetchTask extends AsyncTask<String, Void, ArrayList<Book>> {
    private final AsyncResponse delegate;
    private final String queryURI;
    private final int Results;

    // Constructor
    public FetchTask(AsyncResponse delegate, String queryURI, int maxResults) {
        this.delegate = delegate;
        this.queryURI = queryURI;
        this.Results = maxResults;
    }

    /**
     * doInBackground
     *
     * @param params optional parameters
     * @return an ArrayList of Book Objects
     */
    @Override
    protected ArrayList<Book> doInBackground(String... params) {
        // Create a new API Object
        API mApi = new API();
        // Call the API and get the results in a String variable
        String jsonResults = mApi.callAPI(queryURI);
        // If the results are not null proceed to parsing and creating Book Objects
        if (jsonResults != null) {
            try {

                JSONObject jsonObject = new JSONObject(jsonResults);
                if (jsonObject.has("items")) {
                    JSONArray resultsArray = jsonObject.getJSONArray("items");

                    int countResults = resultsArray.length();

                    ArrayList<Book> parsedResults = new ArrayList<>();

                    for (int i = 0; i < countResults; i++) {

                        JSONObject bookRecord = resultsArray.getJSONObject(i);

                        JSONObject bookVolumeInfo = bookRecord.getJSONObject("volumeInfo");

                        String bookTitle = bookVolumeInfo.getString("title");

                        JSONArray bookAuthor = null;

                        try {
                            bookAuthor = bookVolumeInfo.getJSONArray("authors");
                        } catch (JSONException ignored) {

                        }

                        String bookAuthorString = "";

                        if (bookAuthor == null) {
                            bookAuthorString = "Unknown";
                        } else {

                            int countAuthor = bookAuthor.length();
                            for (int e = 0; e < countAuthor; e++) {
                                String author = bookAuthor.getString(e);
                                if (bookAuthorString.isEmpty()) {
                                    bookAuthorString = author;
                                } else if (e == countAuthor - 1) {
                                    bookAuthorString = bookAuthorString + " and " + author;
                                } else {
                                    bookAuthorString = bookAuthorString + ", " + author;
                                }
                            }
                        }
                        //------------------------------------------------------------------------------
                        // IMAGE LINKS
                        //------------------------------------------------------------------------------
                        JSONObject bookImageLinks = null;
                        try {
                            bookImageLinks = bookVolumeInfo.getJSONObject("imageLinks");
                        } catch (JSONException ignored) {
                        }
                        // Convert the image link to a string
                        String bookSmallThumbnail = "";
                        if (bookImageLinks == null) {
                            bookSmallThumbnail = "null";
                        } else {
                            bookSmallThumbnail = bookImageLinks.getString("smallThumbnail");
                        }
                        // Create a Book object

                        Book mBook = new Book(bookTitle, bookAuthorString, bookSmallThumbnail);
                        // Add it to the array
                        parsedResults.add(i, mBook);
                    }
                    // Return the results
                    return parsedResults;
                } else {
                    return null;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * onPostExecute
     * Once the background operation is completed, pass the results through the delegate method
     *
     * @param parsedResults an ArrayList of Book Objects
     */
    @Override
    protected void onPostExecute(ArrayList<Book> parsedResults) {
        delegate.processFinish(parsedResults);
    }

    // Interface to delegate the onPostExecute actions
    public interface AsyncResponse {
        void processFinish(ArrayList<Book> output);
    }
}



