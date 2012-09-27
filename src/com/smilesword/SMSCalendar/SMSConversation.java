package com.smilesword.SMSCalendar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class SMSConversation extends Activity
{
	private int thread_id;
	private ListView talkView;
	private Button bt_sendSms;
	private DetailEntity d;
	private EditText et_sendSms;
	// private Menu myMenu;
	private String address, body;
	private List<DetailEntity> list = null;
	private Uri SMS_SMS = Uri.parse("content://sms/");
	private String startDate = null;
	private String endDate = null;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.conversation);
		thread_id = this.getIntent().getExtras().getInt("thread_id");
		Toast.makeText(this, Integer.toString(thread_id), Toast.LENGTH_SHORT).show();
		talkView = (ListView) findViewById(R.id.list);
		et_sendSms = (EditText) findViewById(R.id.et_sendSMS);
		bt_sendSms = (Button) findViewById(R.id.bt_sendSMS);
		bt_sendSms.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				sendSms();
			}
		});
		talkView.setDividerHeight(30);

		// talkView.setDivider(divider)
		list = new ArrayList<DetailEntity>();
		initList(thread_id);
		talkView.setAdapter(new DetailAdapter(SMSConversation.this, list));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		super.onCreateOptionsMenu(menu);
		menu.add(0, 1, 0, "选择日期");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		showCalendar();
		return true;
	}

	private void showCalendar()
	{
		LayoutInflater li = LayoutInflater.from(this);
		View view = li.inflate(R.layout.calendar_layout, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("选择时间段");
		builder.setView(view);
		PromptListener pl = new PromptListener(view);
		builder.setPositiveButton("确定", pl);
		builder.setNegativeButton("取消", pl);
		AlertDialog ad = builder.create();
		ad.show();
	}

	private void initList(int _id)
	{
		String strDate;
		long date;
		int type;
		Date datef;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String[] projection = new String[] { "address", "body", "date", "type" };
		Cursor c = getContentResolver().query(SMS_SMS, projection, "thread_id=?", new String[] { Integer.toString(_id) }, "date asc");
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

	private void sendSms()
	{
		String strDate;
		body = et_sendSms.getText().toString();
		SmsManager smsMgr = SmsManager.getDefault();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Date datef = new Date();
		strDate = dateFormat.format(datef);
		Intent i = new Intent("com.smilesword.SMSCalender.SMSConversation");
		PendingIntent dummyEvent = PendingIntent.getBroadcast(SMSConversation.this, 0, i, 0);
		try
		{
			smsMgr.sendTextMessage(address, null, body, dummyEvent, dummyEvent);
			ContentValues values = new ContentValues();
			values.put("address", address);
			values.put("body", body);
			getContentResolver().insert(Uri.parse("content://sms/sent"), values);
		}
		catch (Exception e)
		{
			Log.e("SmsSending", "SendException", e);
		}
		finally
		{
			d = new DetailEntity(address, strDate, body, R.layout.list_say_he_item);
			list.add(d);
		}

	}

	public class PromptListener implements android.content.DialogInterface.OnClickListener
	{

		View promptDialogView = null;

		public PromptListener(View inDialogView)
		{
			promptDialogView = inDialogView;
		}

		public void onClick(DialogInterface v, int buttonId)
		{
			if (buttonId == DialogInterface.BUTTON_POSITIVE)
			{
				Context ctx = getApplicationContext();
				startDate = getStartDate();
				endDate = getEndDate();
				Toast.makeText(ctx, startDate + "  " + endDate, Toast.LENGTH_SHORT).show();
				Intent intent = new Intent();
				intent.putExtra("startDate", startDate + " 00:00:00");
				intent.putExtra("endDate", endDate + " 00:00:00");
				intent.putExtra("thread_id", thread_id);
				// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setClass(SMSConversation.this, SMSDateActivity.class);
				startActivity(intent);
			}
		}

		private String getStartDate()
		{
			DatePicker dps = (DatePicker) promptDialogView.findViewById(R.id.dp_start);
			return Integer.toString(dps.getYear()) + "-" + Integer.toString(dps.getMonth() + 1) + "-" + Integer.toString(dps.getDayOfMonth());
		}

		private String getEndDate()
		{
			DatePicker dpe = (DatePicker) promptDialogView.findViewById(R.id.dp_end);
			return Integer.toString(dpe.getYear()) + "-" + Integer.toString(dpe.getMonth() + 1) + "-" + Integer.toString(dpe.getDayOfMonth());
		}
	}

}
