package com.zwh.appsinstall;
import java.util.Date;

import android.app.Activity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

/**
 * DeskClock clock view for desk docks.
 */
public class DeskClock extends Activity {

	private DigitalClock mTime;
	private TextView mDate;
	private String TAG = "DeskClock";
	private String mDateFormat;

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.desk_clock);

		mDateFormat = getString(R.string.full_wday_month_day_no_year);

		mTime = (DigitalClock) findViewById(R.id.time);
		mDate = (TextView) findViewById(R.id.date);

		mTime.setSystemUiVisibility(View.STATUS_BAR_VISIBLE);
		mTime.getRootView().requestFocus();
		refreshDate();
	}

	private void refreshDate() {
		final Date now = new Date();
		Log.d(TAG, "refreshing date..." + now);
		mDate.setText(DateFormat.format(mDateFormat, now));
	}
} 