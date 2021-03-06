package com.example.mycall0.tab3;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.example.mycall0.R;
import com.example.mycall0.Info.MessageBean;
import com.example.mycall0.myadapter.MessageBoxListAdapter;

import android.app.Activity;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

/**
 * ������Ϣ�б� �շ���
 * 
 * @author Administrator
 * 
 */
public class MessageBoxList extends Activity {
	private ListView talkView;
	private List<MessageBean> messages = null;
	private AsyncQueryHandler asyncQuery;
	private String address;
	private SimpleDateFormat sdf;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.message_list_view);
		sdf = new SimpleDateFormat("MM-dd HH:mm");
		String thread = getIntent().getStringExtra("threadId");
		init(thread);
	}

	private void init(String thread) {
		asyncQuery = new MessageAsynQueryHandler(getContentResolver());
		talkView = (ListView) findViewById(R.id.message_list);
		messages = new ArrayList<MessageBean>();

		Uri uri = Uri.parse("content://sms");
		String[] projection = new String[] { "date", "address", "person",
				"body", "type" }; // ��ѯ����
		asyncQuery.startQuery(0, null, uri, projection,
				"thread_id = " + thread, null, "date asc");
	}

	private class MessageAsynQueryHandler extends AsyncQueryHandler {

		public MessageAsynQueryHandler(ContentResolver cr) {
			super(cr);
		}

		@Override
		protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
			if (cursor != null && cursor.getCount() > 0) {
				cursor.moveToFirst();
				for (int i = 0; i < cursor.getCount(); i++) {
					cursor.moveToPosition(i);
					String date = sdf.format(new Date(cursor.getLong(cursor
							.getColumnIndex("date"))));
					if (cursor.getInt(cursor.getColumnIndex("type")) == 1) {
						MessageBean d = new MessageBean(
								cursor.getString(cursor
										.getColumnIndex("address")),
								date,
								cursor.getString(cursor.getColumnIndex("body")),
								R.layout.list_say_he_item);
						messages.add(d);
					} else {
						MessageBean d = new MessageBean(
								cursor.getString(cursor
										.getColumnIndex("address")),
								date,
								cursor.getString(cursor.getColumnIndex("body")),
								R.layout.list_say_me_item);
						messages.add(d);
					}
				}
				if (messages.size() > 0) {
					talkView.setAdapter(new MessageBoxListAdapter(
							MessageBoxList.this, messages));
					talkView.setDivider(null);
					talkView.setSelection(messages.size());
				} else {
					Toast.makeText(MessageBoxList.this, "没有短信进行操作",
							Toast.LENGTH_SHORT).show();
				}
			}
			super.onQueryComplete(token, cookie, cursor);
		}
	}
}
