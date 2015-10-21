package citu.teknoybuyandselluser;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class SellItemDetailsActivity extends BaseActivity {
    private static final String TAG = "SellItemDetails";

    private TextView mTxtItem;
    private TextView mTxtDescription;
    private TextView mTxtPrice;
    private TextView mTxtStatus;
    private ImageView mImgPreview;

    private float mPrice;
    private String mDescription;
    private String mItemName;
    private String mPicture;
    private String mFormatPrice;
    private String mStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(0, 0);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_item_details);
        setupUI();

        Intent intent = getIntent();
        mItemName = intent.getStringExtra(Constants.ITEM_NAME);
        mDescription = intent.getStringExtra(Constants.DESCRIPTION);
        mPrice = intent.getFloatExtra(Constants.PRICE, 0);
        mPicture = intent.getStringExtra(Constants.PICTURE);
        mFormatPrice = intent.getStringExtra(Constants.FORMAT_PRICE);
        mStatus = intent.getStringExtra(Constants.STATUS);

        mTxtItem = (TextView) findViewById(R.id.txtItem);
        mTxtDescription = (TextView) findViewById(R.id.txtDescription);
        mTxtPrice = (TextView) findViewById(R.id.txtPrice);
        mTxtStatus = (TextView) findViewById(R.id.txtStatus);
        mImgPreview = (ImageView) findViewById(R.id.preview);

        mTxtItem.setText(mItemName);
        mTxtDescription.setText(mDescription);
        mTxtPrice.setText("" + mFormatPrice);
        mTxtStatus.setText(mStatus);

        Picasso.with(this)
                .load(mPicture)
                .placeholder(R.drawable.thumb_24dp)
                .into(mImgPreview);

        setTitle(mItemName);
    }

    @Override
    public boolean checkItemClicked(MenuItem menuItem) {
        return menuItem.getItemId() != R.id.nav_sell_items;
    }
}
