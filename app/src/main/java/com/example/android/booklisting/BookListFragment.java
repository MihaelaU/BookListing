package com.example.android.booklisting;

/**
 * Created by ursum on 08/07/2017.
 */

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;


public class BookListFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private static ArrayList<Book> mValues;
    private BookListAdapter mAdapter = new BookListAdapter(mValues, getContext());
    private RecyclerView recyclerView;

    // Constructor
    public BookListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Set listeners on input fields and other setups
        final EditText searchField = (EditText) getActivity().findViewById(R.id.search_field);

        final Spinner ResultsField = (Spinner) getActivity().findViewById(R.id.results_spinner);

        // Set the listeners on the SearchField
        searchField.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                searchField.setCursorVisible(true);
            }
        });
                searchField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String ResultsString = ResultsField.getSelectedItem().toString();
                    int Results = Integer.parseInt(ResultsString);
                    searchField.setCursorVisible(false);
                    String searchCriteria = searchField.getText().toString();
                    getBooks(searchCriteria, Results);
                    View view = getActivity().getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    handled = true;
                }
                return handled;
            }
        });

    }

    /**
     * getBooks is calling the FetchTask method to query the Google Books API
     *
     * @param searchCriteria the search text entered into the SearchField EditText
     * @param Results     the number of results to be returned from the API selected
     *                       in the MaxResults Spinner
     */
    private void getBooks(String searchCriteria, int Results) {
        final LinearLayout noConnection = (LinearLayout) getActivity().findViewById(R.id.no_connection);
        final LinearLayout emptyResults = (LinearLayout) getActivity().findViewById(R.id.empty_results);

        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if (isConnected) {
            noConnection.setVisibility(View.GONE);
            FetchTask fetch = new FetchTask(new FetchTask.AsyncResponse() {
                @Override
                public void processFinish(ArrayList<Book> output) {
                    mValues = output;
                    mAdapter = new BookListAdapter(mValues, getContext());
                    recyclerView.setAdapter(mAdapter);

                    if (mValues == null) {
                        emptyResults.setVisibility(View.VISIBLE);

                    } else {
                        emptyResults.setVisibility(View.GONE);

                    }
                }
            }, searchCriteria, Results);
            fetch.execute();
        } else {
            emptyResults.setVisibility(View.GONE);
            noConnection.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_booklist_list, container, false);
        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
        }
        return view;
    }

    /**
     * results Spinner Listener*/
    private class ResultsSpinnerListener implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            String selected = parent.getItemAtPosition(pos).toString();
            int Results = Integer.parseInt(selected);
            final EditText searchField = (EditText) getActivity().findViewById(R.id.search_field);
            String searchCriteria = searchField.getText().toString();
            if (!searchCriteria.isEmpty()) {
                getBooks(searchCriteria, Results);
            }
        }

        public void onNothingSelected(AdapterView parent) {

        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnListFragmentInteractionListener {

    }
}

