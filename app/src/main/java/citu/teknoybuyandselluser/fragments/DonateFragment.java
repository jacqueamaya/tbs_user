package citu.teknoybuyandselluser.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import citu.teknoybuyandselluser.DonateItemActivity;
import citu.teknoybuyandselluser.DonateItemDetailsActivity;
import citu.teknoybuyandselluser.ExpirationCheckerService;
import citu.teknoybuyandselluser.R;
import citu.teknoybuyandselluser.Server;
import citu.teknoybuyandselluser.adapters.ItemsListAdapter;
import citu.teknoybuyandselluser.models.Item;

/**
 ** 0.01 Initial Codes                      - J. Pedrano    - 12/24/2015
 ** 0.02 View Donate Items from database    - J. Amaya      - 12/31/2015
 ** 0.03 Working Floating Action Button     - J. Amaya      - 01/01/2016
 ** 0.04 Add Quantity to intent             - J. Amaya      - 01/06/2016
 */
public class DonateFragment extends Fragment {
    private static final String TAG = "Donate Fragment";

    private SharedPreferences prefs;
    private View view = null;

    private String user;

    private ItemsListAdapter listAdapter;
    private ArrayList<Item> donatedItems;

    private Gson gson = new Gson();

    public DonateFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_donate, container, false);
        prefs = getActivity().getSharedPreferences(Constants.MY_PREFS_NAME, Context.MODE_PRIVATE);
        user = prefs.getString(Constants.USERNAME, "");

        getDonateItems();

        FloatingActionButton fab= (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(getActivity().getBaseContext(), DonateItemActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }

    public void getDonateItems(){

        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressGetItems);
        progressBar.setVisibility(View.GONE);
        Server.getItemsToDonate(user, progressBar, new Ajax.Callbacks() {
            @Override
            public void success(String responseBody) {
                donatedItems = new ArrayList<Item>();
                donatedItems = gson.fromJson(responseBody, new TypeToken<ArrayList<Item>>(){}.getType());

                TextView txtMessage = (TextView) view.findViewById(R.id.txtMessage);
                ListView lv = (ListView) view.findViewById(R.id.listViewDonateItems);
                if (donatedItems.size() == 0) {
                    txtMessage.setText("No available items to donate");
                    txtMessage.setVisibility(View.VISIBLE);
                    lv.setVisibility(View.GONE);
                } else {
                    txtMessage.setVisibility(View.GONE);
                    listAdapter = new ItemsListAdapter(getActivity().getBaseContext(), R.layout.list_item, donatedItems);
                    lv.setVisibility(View.VISIBLE);
                    lv.setAdapter(listAdapter);
                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Item item = listAdapter.getDisplayView().get(position);

                            Intent intent;
                            intent = new Intent(getActivity().getBaseContext(), DonateItemDetailsActivity.class);
                            intent.putExtra(Constants.ITEM_NAME, item.getName());
                            intent.putExtra(Constants.DESCRIPTION, item.getDescription());
                            intent.putExtra(Constants.QUANTITY, item.getQuantity());
                            intent.putExtra(Constants.PICTURE, item.getPicture());
                            intent.putExtra(Constants.STARS_REQUIRED, item.getStars_required());
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
        getDonateItems();

        Intent service = new Intent(getActivity().getBaseContext(), ExpirationCheckerService.class);
        service.putExtra("username", user);
        getActivity().startService(service);
    }
}
