
package com.nolanlawson.jnameconverter;
 
import java.util.Collections;
import java.util.List;

import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
 
public class SendActionChooser extends ListActivity {
	
	private static final String TAG = "SendActionChooser";
	
	private AppAdapter adapter=null;
	private String subject;
	private String body;
	
	@Override
	public void onCreate(Bundle savedInstanceState)	{
		super.onCreate(savedInstanceState);

		Intent actionSendIntent= createIntent();

		PackageManager packageManager=getPackageManager();
 
		List<ResolveInfo> launchables=packageManager.queryIntentActivities(actionSendIntent, 0);
 		
		for (int i = launchables.size() - 1; i >=0 ; i--) {
			ResolveInfo launchable = launchables.get(i);
			ActivityInfo activity=launchable.activityInfo;
			
			if (activity.name.equals("com.facebook.katana.ShareLinkActivity")) { 
				// forget facebook - it doesn't work with anything but URLs
				launchables.remove(i);
				
			} 	
			Log.d(TAG, "activity name: " + activity.name);
		} 
		Collections.sort(launchables, new ResolveInfo.DisplayNameComparator(packageManager)); 
 
		launchables.add(getDummyClipboardLaunchable());
		
		adapter = new AppAdapter(packageManager, launchables);
		setListAdapter(adapter);
		
		Bundle extras = getIntent().getExtras();
		
		body = extras.getString(Intent.EXTRA_TEXT);
		subject = extras.getString(Intent.EXTRA_SUBJECT);
	}
 
	private ResolveInfo getDummyClipboardLaunchable() {
		return new ResolveInfo(){
			
			@Override
			public Drawable loadIcon(PackageManager pm) {
				return null;
			}

			@Override
			public CharSequence loadLabel(PackageManager pm) {
				return getResources().getString(R.string.copyToClipboardText);
			}
			
		};
	}

	@Override
	protected void onListItemClick(ListView l, View v,
																 int position, long id) {
		ResolveInfo launchable=adapter.getItem(position);
		ActivityInfo activity=launchable.activityInfo;
		
		if (activity == null) {
			// dummy clipboard launchable
			ClipboardManager clipboard = 
			      (ClipboardManager) getSystemService(CLIPBOARD_SERVICE); 
			
			 clipboard.setText(body);
			 Toast t = Toast.makeText(getApplicationContext(), 
					 getResources().getString(R.string.copiedToClipboardText), 
					 Toast.LENGTH_SHORT);
			 t.show();		
			 finish();
		} else {
		
			ComponentName name=new ComponentName(activity.applicationInfo.packageName,activity.name);
			
			Intent actionSendIntent= createIntent();
			actionSendIntent.setComponent(name);
	 
			startActivity(actionSendIntent);	
		}
	}
 
	private Intent createIntent() {
		Intent actionSendIntent=new Intent(android.content.Intent.ACTION_SEND);

		actionSendIntent.setType("text/plain");
		actionSendIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
		actionSendIntent.putExtra(Intent.EXTRA_TEXT, body);
		
		return actionSendIntent;
	}

	private class AppAdapter extends ArrayAdapter<ResolveInfo> {
		private PackageManager pm=null;
 
		AppAdapter(PackageManager pm, List<ResolveInfo> apps) {
			super(SendActionChooser.this, R.layout.chooser_row, apps);
			this.pm=pm;
		}
 
		@Override
		public View getView(int position, View convertView,
													ViewGroup parent) {
			if (convertView==null) {
				convertView=newView(parent);
			}
 
			bindView(position, convertView);
 
			return(convertView);
		}
 
		private View newView(ViewGroup parent) {
			return(getLayoutInflater().inflate(R.layout.chooser_row, parent, false));
		}
 
		private void bindView(int position, View row) {
			TextView label=(TextView)row.findViewById(R.id.label);
 
			label.setText(getItem(position).loadLabel(pm));
 
			ImageView icon=(ImageView)row.findViewById(R.id.icon);
 
			icon.setImageDrawable(getItem(position).loadIcon(pm));
		}
	}

}