package com.zwh.appsinstall;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class AppDetailActivity extends Activity{
	private PackageInfo mPackageInfo = null;
	private PackageManager  mPm  = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.installed_app_details);

		Intent intent = getIntent();
		String packageName = intent.getStringExtra("PACKAGENAME");

		try {
			mPackageInfo = getPackageManager().getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
			mPm = getPackageManager();
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		TextView mAppVersion = (TextView) findViewById(R.id.app_size);

		if (mPackageInfo != null && mPackageInfo.versionName != null) {
			mAppVersion.setVisibility(View.VISIBLE);
			mAppVersion.setText(getString(R.string.version_text)+String.valueOf(mPackageInfo.versionName));
		} else {
			mAppVersion.setVisibility(View.INVISIBLE);
		}

		ImageView mAppIcon = (ImageView)findViewById(R.id.app_icon);
		Drawable drawable = mPackageInfo.applicationInfo.loadIcon(mPm);
		mAppIcon.setImageDrawable(drawable);

		TextView mAppName = (TextView)findViewById(R.id.app_name);
		CharSequence  label = mPackageInfo.applicationInfo.loadLabel(mPm);
		mAppName.setText(label);

		getAppPemission(packageName);

	}

	public void getAppPemission(String packageName) {
		PackageInfo info;
		
		try {
			info = mPm.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
			//String result = null;
			String[] packagePermissions = info.requestedPermissions;
			Log.v("name", info.packageName);
			if (packagePermissions != null) {
				for (int i = 0; i < packagePermissions.length; i++) {
					Log.v("result", packagePermissions[i]);
				}
			} else {
				Log.v("name", info.packageName + ": no permissions");
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}
}
