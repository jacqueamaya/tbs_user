package citu.teknoybuyandselluser;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import citu.teknoybuyandselluser.models.ReservedItem;

public class RentItemActivity extends BaseActivity {
    private static final String TAG = "Buy Item";
    private static final int DIVISOR = 1000;

    private int mQuantity;
    private int mStarsToUse;
    private double mDiscount;
    private double mDiscountedPrice;
    private float mPrice;
    private String mItemName;

    private TextView mLblStarsToUse;
    private EditText mTxtQuantity;
    private EditText mTxtStarsToUse;
    private RadioButton mRdWithoutDiscount;
    private RadioButton mRdWithDiscount;

    private ProgressDialog mProgressDialog;

    private SharedPreferences mPreferences;
    private Map<String, String> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rent_item);
        setupUI();

        mPreferences = getSharedPreferences(Constants.MY_PREFS_NAME, Context.MODE_PRIVATE);

        Intent intent;
        intent = getIntent();
        int mItemId = intent.getIntExtra(Constants.ID, 0);
        mItemName = intent.getStringExtra(Constants.ITEM_NAME);
        String mDescription = intent.getStringExtra(Constants.DESCRIPTION);
        mPrice = intent.getFloatExtra(Constants.PRICE, 0);
        mQuantity = intent.getIntExtra(Constants.QUANTITY, 1);
        String mPicture = intent.getStringExtra(Constants.PICTURE);
        String mFormatPrice = intent.getStringExtra(Constants.FORMAT_PRICE);

        TextView mTxtItem = (TextView) findViewById(R.id.txtItem);
        TextView mTxtDescription = (TextView) findViewById(R.id.txtDescription);
        TextView mTxtPrice = (TextView) findViewById(R.id.txtPrice);
        mLblStarsToUse = (TextView) findViewById(R.id.lblStarsToUse);
        mTxtStarsToUse = (EditText) findViewById(R.id.txtStarsToUse);
        mRdWithoutDiscount = (RadioButton) findViewById(R.id.rdWithoutDiscount);
        mRdWithDiscount = (RadioButton) findViewById(R.id.rdWithDiscount);
        mTxtQuantity = (EditText) findViewById(R.id.txtQuantity);
        ImageView mBtnRentItem = (ImageView) findViewById(R.id.btnRentItem);
        ImageView mImgItem = (ImageView) findViewById(R.id.imgItem);

        mProgressDialog = new ProgressDialog(this);

        mTxtItem.setText(mItemName);
        mTxtDescription.setText(mDescription);
        mTxtPrice.setText("Php " + mFormatPrice);

        Picasso.with(this)
                .load(mPicture)
                .placeholder(R.drawable.thumbsq_24dp)
                .into(mImgItem);

        setTitle(mItemName);

        data = new HashMap<>();

        String user = mPreferences.getString(Constants.USERNAME, "");
        data.put(Constants.RENTER, user);
        data.put(Constants.ID, "" + mItemId);

        mBtnRentItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRent(v);
            }
        });
    }

    @Override
    public boolean checkItemClicked(MenuItem menuItem) {
        return menuItem.getItemId() != R.id.nav_make_transactions;
    }

    public void onRent(View view) {
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Please wait. . .");
        if (mRdWithDiscount.isChecked()) {
            buyWithDiscountDialogBox();
        } else if (mRdWithoutDiscount.isChecked()) {
            rentItem();
        }
    }

    public void rentItem() {
        int quantity = Integer.parseInt(mTxtQuantity.getText().toString());
        if(quantity <= mQuantity) {
            data.put(Constants.QUANTITY, quantity + "");
            Server.rentItem(data, mProgressDialog, new Ajax.Callbacks() {
                @Override
                public void success(String responseBody) {
                    try {
                        JSONObject json = new JSONObject(responseBody);
                        if (json.getInt("status") == 201) {
                            Log.d(TAG, "Rent Item success");
                            Toast.makeText(RentItemActivity.this, mItemName + " is now reserved.", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(RentItemActivity.this, json.getString("statusText"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void error(int statusCode, String responseBody, String statusText) {
                    Log.d(TAG, "Server error");
                    Toast.makeText(RentItemActivity.this, "Unable to connect to server", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void buyWithDiscountDialogBox() {
        final AlertDialog.Builder buyItem = new AlertDialog.Builder(this);
        buyItem.setTitle("Buy With Discount");
        buyItem.setIcon(R.drawable.ic_star_black_24dp);

        if (mTxtStarsToUse.getText().toString().equals("")) {
            buyItem.setMessage("You cannot buy this item since you have no stars collected.")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
        } else {
            mStarsToUse = Integer.parseInt(mTxtStarsToUse.getText().toString());
            if (getStars() < mStarsToUse) {
                buyItem.setMessage("Not enough stars collected. \nRemaining stars collected: 87")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
            } else {
                if (mStarsToUse < 50) {
                    buyItem.setMessage("Stars to use should be greater than or equal to 50.")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                } else if (mStarsToUse > 150) {
                    buyItem.setMessage("Stars to use should not be greater than 150.")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                } else {
                    ReservedItem ri = new ReservedItem();
                    ri.setStarsToUse(mStarsToUse);
                    calculateDiscount();
                    calculateDiscountedPrice();
                    buyItem.setMessage("Discount:\t" + Utils.formatDouble(mDiscount * 100) + "%\n" +
                            "Original Price:\t" + Utils.formatFloat(mPrice) + "\n" +
                            "Discounted Price: \t" + Utils.formatDouble(mDiscountedPrice) + "\n" +
                            "Stars Remaining: " + getStarsRemaining())
                            .setCancelable(true)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    data.put(Constants.STARS_TO_USE, "" + mStarsToUse);
                                    rentItem();
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                }
            }
        }

        AlertDialog alert = buyItem.create();
        alert.show();
    }

    private int getStars() {
        mPreferences = getSharedPreferences(Constants.MY_PREFS_NAME, Context.MODE_PRIVATE);
        return mPreferences.getInt(Constants.STARS_COLLECTED, 0);
    }

    private int getStarsRemaining() {
        return getStars() - mStarsToUse;
    }

    private void calculateDiscount() {
        mDiscount = (double) mStarsToUse / DIVISOR;
    }

    private void calculateDiscountedPrice() {
        mDiscountedPrice = mPrice * (1 - mDiscount);
    }

    public void showInputStars(View view) {
        if (getStars() >= 50) {
            mLblStarsToUse.setText("Stars to use");
            mLblStarsToUse.setVisibility(View.VISIBLE);
            mTxtStarsToUse.setVisibility(View.VISIBLE);
        } else {
            mLblStarsToUse.setText("Insufficient stars");
            mLblStarsToUse.setVisibility(View.VISIBLE);
        }
    }

    public void hideInputStars(View view) {
        if (mLblStarsToUse.getVisibility() == View.VISIBLE) {
            mLblStarsToUse.setVisibility(View.GONE);
            mTxtStarsToUse.setVisibility(View.GONE);
        }
    }
}