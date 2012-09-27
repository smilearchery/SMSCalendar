package com.smilesword.SMSCalendar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

public class SMSDateActivity extends Activity
{
	private int thread_id;
	private ListView talkView;
	private DetailEntity d;
	private String address, body;
	private List<DetailEntity> list = null;
	private Uri SMS_SMS = Uri.parse("content://sms/");
	private String startDate = null;
	private String endDate = null;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.smsdate);
		thread_id = this.getIntent().getExtras().getInt("thread_id");
		startDate = this.getIntent().getExtras().getString("startDate");
		endDate = this.getIntent().getExtras().getString("endDate");
		Toast.makeText(this, Integer.toString(thread_id), Toast.LENGTH_SHORT).show();
		talkView = (ListView) findViewById(R.id.listSmsDate);
		talkView.setDividerHeight(30);

		// talkView.setDivider(divider)
		list = new ArrayList<DetailEntity>();
		initList(thread_id, startDate, endDate);
		talkView.setAdapter(new DetailAdapter(SMSDateActivity.this, list));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		super.onCreateOptionsMenu(menu);
		menu.add(0, 1, 0, "多选");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		return true;
	}

	private void initList(int _id, String strsDate, String endsDate)
	{
		String strDate;
		long date, strlDate = 0, endlDate = 0;
		int type;
		Date datef;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		try
		{
			strlDate = dateFormat.parse(strsDate).getTime();
			endlDate = dateFormat.parse(endsDate).getTime();
		}
		catch (ParseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String[] projection = new String[] { "address", "body", "date", "type" };
		Cursor c = getContentResolver().query(SMS_SMS, projection, "thread_id=? and date>=? and date <=?", new String[] { Integer.toString(_id),Long.toString(strlDate),Long.toString(endlDate) }, "date asc");
		//, "date between " + Long.toString(strlDate) + "and " + Long.toString(endlDate) 
		startManagingCursor(c);
		while (c.moveToNext())
		{
			address = c.getString(0);
			body = c.getString(1);
			date = c.getLong(2);
			type = c.getInt(3);
			datef = new Date(date);
			strDate = dateFormat.format(datef);
			if (type == 1)
			{
				d = new DetailEntity(address, strDate, body, R.layout.list_say_he_item);
				list.add(d);
			}
			else
			{
				d = new DetailEntity("我", strDate, body, R.layout.list_say_me_item);
				list.add(d);
			}
		}
	}

}
