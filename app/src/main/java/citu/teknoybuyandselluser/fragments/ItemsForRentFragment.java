package citu.teknoybuyandselluser.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import citu.teknoybuyandselluser.Ajax;
import citu.teknoybuyandselluser.Constants;
import citu.teknoybuyandselluser.ExpirationCheckerService;
import citu.teknoybuyandselluser.R;
import citu.teknoybuyandselluser.ReservedItemActivity;
import citu.teknoybuyandselluser.Server;
import citu.teknoybuyandselluser.adapters.ReservedItemsAdapter;
import citu.teknoybuyandselluser.models.Item;
import citu.teknoybuyandselluser.models.Notification;
import citu.teknoybuyandselluser.models.ReservedItem;

/**
 ** 0.01 Initial Codes                                      - J. Pedrano    - 12/24/2015
 ** 0.02 View Reserved Items for Rent from database         - J. Amaya      - 01/06/2016
 */

public class ItemsForRentFragment extends Fragment {
    private static final String TAG = "Items For Rent Fragment";
    private View view = null;

    private SharedPreferences prefs;

    private String user;

    private Gson gson = new Gson();

    public ItemsForRentFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_items_for_rent, container, false);

        prefs = getActivity().getSharedPreferences(Constants.MY_PREFS_NAME, Context.MODE_PRIVATE);
        user = prefs.getString(Constants.USERNAME, "");

        getReservedItemsForRent();
        return view;
    }

    public void getReservedItemsForRent() {

        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressGetItems);
        progressBar.setVisibility(View.GONE);

        Server.getReservedItemsForRent(user, progressBar, new Ajax.Callbacks() {
            @Override
            public void success(String responseBody) {
                ArrayList<ReservedItem> reservations = new ArrayList<ReservedItem>();
                reservations = gson.fromJson(responseBody, new TypeToken<ArrayList<ReservedItem>>(){}.getType());
                    ListView lv = (ListView) view.findViewById(R.id.listViewItemsForRent);
                    TextView txtMessage = (TextView) view.findViewById(R.id.txtMessage);

                    if (reservations.size() == 0) {
                        txtMessage.setText("No reserved items for rent");
                        txtMessage.setVisibility(View.VISIBLE);
                        lv.setVisibility(View.GONE);
                    } else {
                        txtMessage.setVisibility(View.GONE);
                        ReservedItemsAdapter listAdapter = new ReservedItemsAdapter(getActivity().getBaseContext(), R.layout.list_item, reservations);
                        lv.setVisibility(View.VISIBLE);
                        lv.setAdapter(listAdapter);
                        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                ReservedItem reservation = (ReservedItem) parent.getItemAtPosition(position);

                                Intent intent;
                                intent = new Intent(getActivity().getBaseContext(), ReservedItemActivity.class);
                                intent.putExtra(Constants.ID, reservation.getItem().getId());
                                intent.putExtra(Constants.RESERVATION_ID, reservation.getId());
                                intent.putExtra(Constants.ITEM_NAME, reservation.getItem().getName());
                                intent.putExtra(Constants.DESCRIPTION, reservation.getItem().getDescription());
                                intent.putExtra(Constants.PRICE, reservation.getItem().getPrice());
                                intent.putExtra(Constants.STARS_REQUIRED, reservation.getItem().getStars_required());
                                intent.putExtra(Constants.STARS_TO_USE, reservation.getStars_to_use());
                                //intent.putExtra(Constants.DISCOUNTED_PRICE, reservation.getDiscountedPrice());
                                intent.putExtra(Constants.PICTURE, reservation.getItem().getPicture());
                                intent.putExtra(Constants.RESERVED_DATE, reservation.getReserved_date());

                                startActivity(intent);
                            }
                        });
                    }
            }

            @Override
            public void error(int statusCode, String responseBody, String statusText) {
                Log.v(TAG, "Request error");
                Toast.makeText(getActivity().getBaseContext(), "Unable to connect to server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getReservedItemsForRent();

        Intent service = new Intent(getActivity().getBaseContext(), ExpirationCheckerService.class);
        service.putExtra("username", user);
        getActivity().startService(service);
    }
}
