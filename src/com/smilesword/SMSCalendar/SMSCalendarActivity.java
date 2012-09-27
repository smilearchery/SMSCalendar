package com.smilesword.SMSCalendar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class SMSCalendarActivity extends Activity
{

	private ListView listView;
	private List<Map<String, Object>> items;
	private ListAdapter adapter;
	private Uri SMS_Conversation = Uri.parse("content://mms-sms/conversations").buildUpon().appendQueryParameter("simple", "true").build();
	private Uri SMS_SMS = Uri.parse("content://sms/");
	private OnItemClickListener l;

	// private SimpleDateFormat formatter = new
	// SimpleDateFormat("yyyy.MM.dd hh:mm:ss");

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		listView = (ListView) findViewById(R.id.listView);
		String[] columns = new String[] { "address", "body" };
		int[] names = new int[] { R.id.telnum, R.id.snippet };
		adapter = new SimpleAdapter(this, getList(), R.layout.sms_conversation, columns, names);
		l = new ListViewOnItemClickListener();
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(l);
	}

	public List<Map<String, Object>> getList()
	{
		items = new ArrayList<Map<String, Object>>();
		String[] projection = new String[] { "snippet", "_id" };
		Cursor c = getContentResolver().query(SMS_Conversation, projection, null, null, null);
		startManagingCursor(c);
		Map<String, Object> map;

		while (c.moveToNext())
		{
			map = new HashMap<String, Object>();
			map.put("body", c.getString(0));
			map.put("_id", c.getInt(1));
			map.put("address", getTelNum(c.getInt(1)));
			items.add(map);
		}

		return items;
	}

	public String getTelNum(int thread_id)
	{
		String telnum = null;
		String[] projection = new String[] { "address" };
		Cursor c = getContentResolver().query(SMS_SMS, projection, "thread_id=?", new String[] { Integer.toString(thread_id) }, null);
		startManagingCursor(c);
		if (c.moveToFirst())
		{
			telnum = c.getString(0);
		}
		return telnum;
	}

	private class ListViewOnItemClickListener implements OnItemClickListener
	{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id)
		{
			Log.d("_id", items.get(position).get("_id").toString());
			Intent intent = new Intent();
			intent.setClass(SMSCalendarActivity.this, SMSConversation.class);
			intent.putExtra("thread_id", (Integer)items.get(position).get("_id"));
			startActivity(intent);
		}
	}

}