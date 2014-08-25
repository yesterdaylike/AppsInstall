package com.zwh.appsinstall;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.CollationKey;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;

import com.zwh.appsinstall.AppMsg.Style;

public class MainActivity extends Activity {

	//private ActionBar actionBar;
	private ViewPager mPager;  
	private ArrayList<View> mTabsView = null;  
	private ArrayList<String> mTitles = null;
	//private ListView mRecentAppListView = null;
	private ListView mInstalledAppListView = null;
	private ListView mApkFileListView = null;
	private PackageManager mPackageManager = null;

	private boolean refrest = true;

	/*private View mLayoutDeskClock = null;
	private DigitalClock mTime;
	private TextView mDate;*/

	private static final int DISPLAY_TASKS = 20;
	private static final int MAX_TASKS = DISPLAY_TASKS + 1;

	@Override  
	protected void onCreate(Bundle savedInstanceState) {

		/*int theme = getSharedPreferences("cons", MODE_PRIVATE).getInt("theme", 
				android.R.style.Theme_Holo);
		setTheme(theme);*/

		super.onCreate(savedInstanceState);  
		setContentView(R.layout.activity_main);
		//actionBar = this.getActionBar();

		/*actionBar.setTitle("ActionBar+ViewPager");   
		actionBar.setSubtitle("setSubtitle");

		//实现用户点击ActionBar 图标后返回前一个activity  
		actionBar.setDisplayHomeAsUpEnabled(true) ;*/

		//定义ActionBar模式为NAVIGATION_MODE_TABS  
		//actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);      

		//每个页面的Title数据
		mTitles = new ArrayList<String>();
		//mTitles.add(getString(R.string.recent_app));
		mTitles.add(getString(R.string.install_app));
		mTitles.add(getString(R.string.all_app));

		mPager = (ViewPager)findViewById(R.id.vPager);
		mPackageManager = getPackageManager();
		//将要分页显示的View装入数组中
		LayoutInflater layFlater = LayoutInflater.from(this);

		/*mLayoutDeskClock = layFlater.inflate(R.layout.desk_clock, null);
		mTime = (DigitalClock) mLayoutDeskClock.findViewById(R.id.time);
		mDate = (TextView) mLayoutDeskClock.findViewById(R.id.date);*/

		//View layoutRecent = layFlater.inflate(R.layout.activity_tabbar_recent, null); 
		//mRecentAppListView = (ListView) layoutRecent.findViewById(R.id.recent_listview);

		View layoutInstall = layFlater.inflate(R.layout.activity_tabbar_install, null); 
		mInstalledAppListView = (ListView) layoutInstall.findViewById(R.id.install_listview);
		mInstalledAppListView.setOnItemClickListener(mInstalledAppItemClickListener);
		mInstalledAppListView.setOnItemLongClickListener(mInstalledAppItemLongClickListener);

		View layoutAll = layFlater.inflate(R.layout.activity_tabbar_all, null);
		mApkFileListView = (ListView) layoutAll.findViewById(R.id.all_listview);
		mApkFileListView.setOnItemClickListener(mAllAppItemClickListener);
		//mApkFileListView.setOnItemLongClickListener(mAllAppItemLongClickListener);

		registerForContextMenu(mApkFileListView);

		//每个页面的Title数据
		mTabsView = new ArrayList<View>();
		//mTabsView.add(layoutRecent);
		mTabsView.add(layoutInstall);
		mTabsView.add(layoutAll);

		mPager.setAdapter(mPagerAdapter);
		mPager.setCurrentItem(0);

		//ShowClock();
	}

	OnItemLongClickListener mInstalledAppItemLongClickListener = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {
			// TODO Auto-generated method stub
			TextView tvtag = (TextView) view.findViewById(R.id.tag);
			final String []strs = tvtag.getText().toString().split(",");
			Log.e("apk", String.valueOf(tvtag.getText()));

			PopupMenu popup = new PopupMenu(MainActivity.this, view.findViewById(R.id.tag));
			popup.setOnMenuItemClickListener(new OnMenuItemClickListener() {

				@Override
				public boolean onMenuItemClick(MenuItem item) {
					// TODO Auto-generated method stub
					switch (item.getItemId()) {
					case R.id.start:
						Intent LaunchIntent = mPackageManager.getLaunchIntentForPackage(strs[0]);
						if( null != LaunchIntent && !strs[0].equals("com.zwh.appsinstall")){
							startActivity( LaunchIntent );
						}
						/*else{
							showAppMsg(getString(R.string.can_not_open_apk), AppMsg.STYLE_INFO);
							Log.e("Intent", "Intent is null!");
						}*/
						return true;
					case R.id.uninstall:
						Uri packageUri = Uri.parse("package:" + strs[0]);
						Intent deleteIntent = new Intent();
						deleteIntent.setAction(Intent.ACTION_DELETE);
						deleteIntent.setData(packageUri);
						startActivity(deleteIntent);
						return true;
					case R.id.pull:
						File src = new File(strs[2]);
						String root = "/mnt/sdcard";
						String dstPath = root + strs[2].substring(strs[2].lastIndexOf("/"));
						File dst = new File(dstPath);
						try {
							copyFile(src, dst);
							showAppMsg(dstPath, AppMsg.STYLE_INFO);
							new LoadApkFilesTask().execute();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						return true;
					case R.id.detail:
						Intent intent = new Intent(MainActivity.this, AppDetailActivity.class);
						intent.putExtra("PACKAGENAME", strs[0]);
						startActivity(intent);
						return true;
					default:
						return false;
					}
				}
			});
			MenuInflater inflater = popup.getMenuInflater();
			inflater.inflate(R.menu.main_activity_actions, popup.getMenu());
			popup.show();
			return false;
		}
	};

	public void copyFile(File src, File dst) throws IOException {
		InputStream in = new FileInputStream(src);
		OutputStream out = new FileOutputStream(dst);

		// Transfer bytes from in to out
		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		in.close();
		out.close();
	}

	OnItemLongClickListener mAllAppItemLongClickListener = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {
			// TODO Auto-generated method stub
			PopupMenu popup = new PopupMenu(MainActivity.this, view.findViewById(R.id.tag));
			popup.setOnMenuItemClickListener(new OnMenuItemClickListener() {

				@Override
				public boolean onMenuItemClick(MenuItem item) {
					// TODO Auto-generated method stub
					switch (item.getItemId()) {
					case R.id.start:

						return true;
					case R.id.uninstall:
						showAppMsg("uninstall!", AppMsg.STYLE_INFO);
						return true;
					case R.id.pull:
						showAppMsg("pull!", AppMsg.STYLE_INFO);
						return true;
					case R.id.detail:
						showAppMsg("detail!", AppMsg.STYLE_INFO);
						return true;
					default:
						return false;
					}
				}
			});
			MenuInflater inflater = popup.getMenuInflater();
			inflater.inflate(R.menu.main_activity_actions, popup.getMenu());
			popup.show();
			return false;
		}
	};

	/*@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_activity_actions, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
		case R.id.DefaultWorkspace:
			//editNote(info.id);
			return true;
		case R.id.Appwidget:
			//deleteNote(info.id);
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}*/

	OnItemClickListener mInstalledAppItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view,
				int position, long id) {
			// TODO Auto-generated method stub
			TextView tvtag = (TextView) view.findViewById(R.id.tag);
			String []strs = tvtag.getText().toString().split(",");

			Log.e("apk", String.valueOf(tvtag.getText()));
			Intent LaunchIntent = mPackageManager.getLaunchIntentForPackage(strs[0]);
			if( null != LaunchIntent && !strs[0].equals("com.zwh.appsinstall")){
				startActivity( LaunchIntent );
			}

			/*else{
				showAppMsg("Can't open apk!", AppMsg.STYLE_INFO);
				Log.e("Intent", "Intent is null!");
			}*/
		}
	};

	OnItemClickListener mAllAppItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view,
				int position, long id) {
			// TODO Auto-generated method stub
			TextView tvtag = (TextView) view.findViewById(R.id.option);
			if(tvtag == null){
				return;
			}
			Log.e("apk", String.valueOf(tvtag.getText()));

			try {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setDataAndType(Uri.fromFile(new File(tvtag.getText().toString())), "application/vnd.android.package-archive");
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			} catch (ActivityNotFoundException e) {
				// TODO: handle exception
			}
		}
	};

	/*public boolean onCreateOptionsMenu(Menu menu) {  
		// Inflate the menu; this adds items to the action bar if it is present.  
		//添加菜单项
		MenuItem findItem = menu.add(0,1,0,"查找");    
		MenuItem exitItem = menu.add(0,0,0,"退出");   

		//绑定到ActionBar      
		findItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		exitItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		return true;
	}*/

	/** 
	 * 填充ViewPager的数据适配器
	 */  
	private final PagerAdapter mPagerAdapter = new PagerAdapter() {  
		//int mCurPos = 0;

		public int getCount() {
			return mTabsView.size();
		}  

		public void destroyItem(View container, int position, Object object) {  
			((ViewPager)container).removeView(mTabsView.get(position));  
		}  

		public CharSequence getPageTitle(int position) {
			//重要，用于让PagerTitleStrip显示相关标题
			return mTitles.get(position);   
		}

		@Override  
		public Object instantiateItem(View container, int position) {  
			((ViewPager)container).addView(mTabsView.get(position));

			if (refrest) {
				new LoadInstalledApkTask().execute();
				new LoadApkFilesTask().execute();
				refrest = false;
			}

			//mRecentAppListView.addHeaderView(mLayoutDeskClock);
			/*mTime.setSystemUiVisibility(View.STATUS_BAR_VISIBLE);
			mTime.getRootView().requestFocus();*/
			//getRecentApps();

			return mTabsView.get(position);
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View)object);
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}
	};  

	public void setMyAllApps() {
		// 查找所有首先显示的activity  
		Intent intent = new Intent(Intent.ACTION_MAIN, null);  
		intent.addCategory(Intent.CATEGORY_LAUNCHER);  
		List<ResolveInfo> mAllApps = mPackageManager.queryIntentActivities(intent, 0);  
		//mAllPackages=pm.getInstalledPackages(0);  
		// 按照名字排序  
		Collections.sort(mAllApps, new ResolveInfo.DisplayNameComparator(  
				mPackageManager));  
	}

	private String[] getRecentApps() {
		final ActivityManager am = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
		final List<ActivityManager.RecentTaskInfo> recentTasks =
				am.getRecentTasks(MAX_TASKS, ActivityManager.RECENT_IGNORE_UNAVAILABLE);
		ActivityManager.RecentTaskInfo recentInfo;

		int numTasks = recentTasks.size();
		String[] recentPackageName = new String[numTasks];

		for (int i = 0; i < numTasks; i++) {
			recentInfo = recentTasks.get(i);
			recentPackageName[i] = recentInfo.baseIntent.getComponent().getPackageName();
		}
		return recentPackageName;
	}

	private void pushRecentAppFornt(List<PackageInfo> packageInfoList){
		//将recent app 提到前面
		String[] recentApps = getRecentApps();
		int length = recentApps.length;
		PackageInfo pkInfo, temp;

		for (int i = 0; i < length; i++) {
			for (int j = 0; j < packageInfoList.size(); j++ ) {
				pkInfo = packageInfoList.get(j);
				if( pkInfo.packageName.equals(recentApps[i]) ){
					temp = packageInfoList.get(i);
					packageInfoList.set(i, pkInfo);
					packageInfoList.set(j, temp);
					break;
				}
			}
		}
	}

	private List<Map<String, Object>> getInstalledApps(boolean getSysPackages) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map;

		List<PackageInfo> packageInfoList = mPackageManager.getInstalledPackages(PackageManager.GET_ACTIVITIES);

		Collections.sort(packageInfoList, new PackageInfoSort()); //按字典排序

		//将recent app 提到前面
		pushRecentAppFornt(packageInfoList);

		for (PackageInfo packageInfo : packageInfoList) {
			Intent LaunchIntent = mPackageManager.getLaunchIntentForPackage(packageInfo.packageName);
			if ( null == LaunchIntent || ( !getSysPackages && packageInfo.versionName == null ) ) {
				continue ;
			}
			map = new HashMap<String, Object>();
			map.put("icon", packageInfo.applicationInfo.loadIcon(mPackageManager));
			//map.put("icon", p.applicationInfo.packageName+','+p.applicationInfo.className);
			map.put("title", packageInfo.applicationInfo.loadLabel(mPackageManager));
			map.put("summary", packageInfo.versionName);
			map.put("tag", packageInfo.packageName+','+packageInfo.applicationInfo.className+','+packageInfo.applicationInfo.sourceDir);

			list.add(map);

			/*if(packageInfo.applicationInfo.flags == ApplicationInfo.FLAG_SYSTEM){
				//系统应用
			}
			else{
			}*/
		}
		return list;
	}

	private class LoadInstalledApkTask extends AsyncTask<Void, Integer, Void> {
		List<Map<String, Object>> list;
		protected Void doInBackground(Void... urls) {
			list = getInstalledApps(true);
			return null;
		}

		protected void onPostExecute(Void result) {

			if( null == list || list.size() < 1 ){
				return;
			}

			mApkFileListView.setBackgroundColor(Color.WHITE);
			SimpleAdapter installedAdapter = new SimpleAdapter(MainActivity.this,
					list,
					R.layout.installed_listitem,
					new String[] {"icon","title","summary","tag"},
					new int[] {R.id.icon, R.id.title, R.id.summary, R.id.tag});
			mInstalledAppListView.setAdapter(installedAdapter);

			installedAdapter.setViewBinder(new ViewBinder(){
				public boolean setViewValue(View view,Object data,String textRepresentation){
					if(view instanceof ImageView && data instanceof Drawable){
						ImageView imageView=(ImageView)view;
						imageView.setImageDrawable((Drawable)data);
						return true;
					}
					else return false;
				}
			});
		}
	}


	private class LoadApkFilesTask extends AsyncTask<Void, Integer, Void> {

		private List<String> mAllFileArray;
		private List<Map<String, Object>> list = null;

		protected Void doInBackground(Void... urls) {
			File IntMemory_Path = new File("/mnt/sdcard");
			File SDCard_Path = new File("/mnt/external_sd");
			File USB_Path = new File("/mnt/usb_storage");
			String extension = ".apk";

			mAllFileArray = new ArrayList<String>();
			getFiles(mAllFileArray, IntMemory_Path, extension, true);
			getFiles(mAllFileArray, SDCard_Path, extension, true);
			getFiles(mAllFileArray, USB_Path, extension, true);

			if( mAllFileArray.size() > 0 ){ 
				Collections.sort(mAllFileArray, new ApkFileSort());
				list = getAllAppData(mAllFileArray);
			}
			return null;
		}

		protected void onPostExecute(Void result) {
			if( null == list || list.size() < 1 && mApkFileListView.getHeaderViewsCount()<1 ){
				//mApkFileListView.addHeaderView(mNullHeader);
				
				List<String> data = new ArrayList<String>();
				            data.add(getString(R.string.empty_apk_file));
				mApkFileListView.setAdapter(
						new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_expandable_list_item_1,data));
				return;
			}

			SimpleAdapter mApkFileAdapter = new SimpleAdapter(MainActivity.this,
					list,
					R.layout.apkfile_listitem,
					new String[] { "icon", "title", "summary", "option"},
					new int[] {R.id.icon, R.id.title, R.id.summary, R.id.option});
			mApkFileListView.setAdapter(mApkFileAdapter);

			mApkFileAdapter.setViewBinder(new ViewBinder(){
				public boolean setViewValue(View view,Object data,String textRepresentation){
					if(view instanceof ImageView && data instanceof Drawable){
						ImageView iv=(ImageView)view;
						iv.setImageDrawable((Drawable)data);
						return true;
					}
					else return false;
				}
			});
		}
	}

	private List<Map<String, Object>> getAllAppData(List<String> appFiles) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map;

		PackageInfo pkgInfo;

		for (String appfile : appFiles) {
			pkgInfo = mPackageManager.getPackageArchiveInfo(appfile,PackageManager.GET_ACTIVITIES);  
			if (pkgInfo != null) {
				ApplicationInfo appInfo = pkgInfo.applicationInfo;
				/* 必须加这两句，不然下面icon获取是default icon而不是应用包的icon */
				appInfo.sourceDir = appfile;
				appInfo.publicSourceDir = appfile;

				long lengthInBytes = new File(appfile).length();
				String length = humanReadableByteCount(lengthInBytes, true);

				map = new HashMap<String, Object>();
				map.put("icon", appInfo.loadIcon(mPackageManager));
				map.put("title", appInfo.loadLabel(mPackageManager));
				map.put("summary", pkgInfo.versionName+"     "+length);
				map.put("option", appfile);
				list.add(map);
			}
		}
		return list;
	}

	public static String humanReadableByteCount(long bytes, boolean si) {
		int unit = si ? 1000 : 1024;
		if (bytes < unit) return bytes + " B";
		int exp = (int) (Math.log(bytes) / Math.log(unit));
		String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
		return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}

	public void getFiles(List<String> list, File Path, String Extension, boolean IsIterative){
		File[] files = Path.listFiles();
		if( null == files || files.length < 1 ){
			return;
		}
		for (File file : files) {
			if (file.isFile()){
				String filePath = file.getPath();

				if (filePath.toLowerCase().endsWith(Extension))  //判断扩展名
					list.add(file.getPath());

				if (!IsIterative)
					break;
			}
			else if (file.isDirectory() && file.getPath().indexOf("/.") == -1)  //忽略点文件（隐藏文件/文件夹）
				getFiles(list, file, Extension, IsIterative);
		}
	}

	/** 
	 * 菜单事件响应
	 */  
	/*public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub    
		switch (item.getItemId()) {           
		case 0:    
			//displayToast("退出菜单");  
			break;  
		case 1:    
			//displayToast("查找菜单");  
			break;
		default:
			break;    
		}
		return super.onOptionsItemSelected(item);    
	}*/

	public void showAppMsg(String msg, Style type) {
		AppMsg appMsg = AppMsg.makeText(this, msg, type);
		appMsg.setLayoutGravity(Gravity.BOTTOM);
		appMsg.show();
	}

	public class PackageInfoSort implements Comparator<PackageInfo>{
		private Collator collator = Collator.getInstance(); //调入这个是解决中文排序问题 
		private Map<PackageInfo, CollationKey> map = new HashMap<PackageInfo, CollationKey>();
		private CollationKey lkey; 
		private CollationKey rkey; 
		private String label;
		private Object object;
		@Override
		public int compare(PackageInfo lhs, PackageInfo rhs) {
			// TODO Auto-generated method stub

			object = map.get(lhs);
			if( null != object ){
				lkey = (CollationKey) object;
			}
			else{
				label = (String) lhs.applicationInfo.loadLabel(mPackageManager);
				lkey = collator.getCollationKey(label.toLowerCase());
				map.put(lhs, lkey);
			}

			object = map.get(rhs);
			if( null != object ){
				rkey = (CollationKey) object;
			}
			else{
				label = (String) rhs.applicationInfo.loadLabel(mPackageManager);
				rkey = collator.getCollationKey(label.toLowerCase());
				map.put(rhs, rkey);
			}
			return lkey.compareTo(rkey);
		}
	}

	public class ApkFileSort implements Comparator<String>{
		private Collator collator = Collator.getInstance(); //调入这个是解决中文排序问题 
		private Map<String, CollationKey> map = new HashMap<String, CollationKey>();
		private CollationKey lkey;
		private CollationKey rkey;
		private PackageInfo pkgInfo;
		private String label;
		private Object object;

		@Override
		public int compare(String lhs, String rhs) {
			// TODO Auto-generated method stub
			object = map.get(lhs);
			if( null != object ){
				lkey = (CollationKey) object;
			}
			else{
				pkgInfo = mPackageManager.getPackageArchiveInfo(lhs,PackageManager.GET_ACTIVITIES);
				if( null == pkgInfo ){
					Log.i("zhengwenhui", "null == pkgInfo "+lhs);
					lkey = collator.getCollationKey(lhs.toLowerCase());
					map.put(lhs, lkey);
				}
				else{
					label = (String) pkgInfo.applicationInfo.loadLabel(mPackageManager);
					lkey = collator.getCollationKey(label.toLowerCase());
					map.put(lhs, lkey);
				}
			}

			object = map.get(rhs);
			if( null != object ){
				rkey = (CollationKey) object;
			}
			else{
				pkgInfo = mPackageManager.getPackageArchiveInfo(rhs,PackageManager.GET_ACTIVITIES);
				if( null == pkgInfo ){
					Log.i("zhengwenhui", "null == pkgInfo "+rhs);
					rkey = collator.getCollationKey(rhs.toLowerCase());
					map.put(rhs, rkey);
				}
				else{
					label = (String) pkgInfo.applicationInfo.loadLabel(mPackageManager);
					rkey = collator.getCollationKey(label.toLowerCase());
					map.put(rhs, rkey);
				}
			}
			return lkey.compareTo(rkey);
		}
	}  

	@SuppressLint("DefaultLocale")
	public void ShowClock(){
		LayoutInflater layoutInflater = LayoutInflater.from(this); 
		View clockView = layoutInflater.inflate(R.layout.desk_clock, null); 

		String mDateFormat = getString(R.string.full_wday_month_day_no_year);
		Date now = new Date();
		TextView mDate = (TextView) clockView.findViewById(R.id.date);
		mDate.setText(DateFormat.format(mDateFormat, now));

		AlertDialog.Builder builder = new Builder(this);
		builder.setView(clockView);

		builder.show();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
	}
}
