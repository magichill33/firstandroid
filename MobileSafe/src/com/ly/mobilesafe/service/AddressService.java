package com.ly.mobilesafe.service;

import com.ly.mobilesafe.R;
import com.ly.mobilesafe.dao.NumberAddressQueryUtils;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.os.SystemClock;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class AddressService extends Service {

	/**
	 * ���������
	 */
	private WindowManager wm;
	private View view;
	
	private SharedPreferences sp;
	//�绰����
	private TelephonyManager tm;
	private MyListenerPhone listenerPhone;
	
	private OutCallReceiver receiver;
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		//��������
		listenerPhone = new MyListenerPhone();
		tm.listen(listenerPhone, PhoneStateListener.LISTEN_CALL_STATE);
		//�ô���ȥע��㲥������
		receiver = new OutCallReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.intent.action.NEW_OUTGOING_CALL");
		registerReceiver(receiver, filter);
		
		//ʵ��������
		wm = (WindowManager) getSystemService(WINDOW_SERVICE);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		//ȡ����������
		tm.listen(listenerPhone, PhoneStateListener.LISTEN_NONE);
		tm = null;
		//�ô���ȡ��ע��㲥������
		unregisterReceiver(receiver);
		receiver = null;
	}
	
	long[] mHits = new long[2];
	/**
	 * �Զ�����˾
	 * @param address
	 */
	public void myToast(String address)
	{
		 //���ô������
	    final WindowManager.LayoutParams params = new WindowManager.LayoutParams();
		
		view = View.inflate(this,R.layout.address_show, null);
		TextView textView = (TextView) view.findViewById(R.id.tv_address);
		//˫���ؼ�������ʾ
		view.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				System.arraycopy(mHits, 1, mHits, 0, mHits.length-1);
				mHits[mHits.length-1] = SystemClock.uptimeMillis();
				if(mHits[0]>=(SystemClock.uptimeMillis()-500))
				{
					params.x = wm.getDefaultDisplay().getWidth()/2 - view.getWidth()/2;
					wm.updateViewLayout(view, params);
					Editor editor = sp.edit();
					editor.putInt("lastx", params.x);
					//editor.putInt("lasty", params.y);
					editor.commit();
				}
			}
		});
		
		//��view��������һ�������ļ�����
		view.setOnTouchListener(new OnTouchListener() {
			//������ָ�ĳ�ʼ��λ��
			int startX;
			int startY;
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_MOVE:
					int newX = (int) event.getRawX();
					int newY = (int) event.getRawY();
					int dx = newX - startX;
					int dy = newY - startY;
					params.x += dx;
					params.y += dy;
					//���Ǳ߽�����
					if(params.x<0)
					{
						params.x = 0;
					}
					if(params.y<0)
					{
						params.y = 0;
					}
					if(params.x > (wm.getDefaultDisplay().getWidth()-view.getWidth()))
					{
						params.x = wm.getDefaultDisplay().getWidth() - view.getWidth();
					}
					if(params.y > (wm.getDefaultDisplay().getHeight()-view.getHeight()))
					{
						params.y = wm.getDefaultDisplay().getHeight() - view.getHeight();
					}
					wm.updateViewLayout(view, params);
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_UP:
					Editor editor = sp.edit();
					editor.putInt("lastx", params.x);
					editor.putInt("lasty", params.y);
					editor.commit();
					break;
				}
				return false;
			}
		});
	    //"��͸��","������","��ʿ��","������","ƻ����"
	    int [] ids = {R.drawable.call_locate_white,R.drawable.call_locate_orange,R.drawable.call_locate_blue
	    ,R.drawable.call_locate_gray,R.drawable.call_locate_green};
	    sp = getSharedPreferences("config", MODE_PRIVATE);
	    view.setBackgroundResource(ids[sp.getInt("which", 0)]);
	    textView.setText(address);
	    
	   
	    params.height = WindowManager.LayoutParams.WRAP_CONTENT;
	    params.width = WindowManager.LayoutParams.WRAP_CONTENT;
	 // �봰�����ϽǶ���
	 	params.gravity = Gravity.TOP + Gravity.LEFT;
	 	// ָ������������100 �ϱ�100������
	 	params.x = sp.getInt("lastx", 0);
	 	params.y = sp.getInt("lasty", 0);
	    params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
	    		|WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
	    params.format = PixelFormat.TRANSLUCENT; //����Ϊ��͸��
		params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
		wm.addView(view, params);
	}
	
	private class MyListenerPhone extends PhoneStateListener{
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			//state:״̬��incomingNumber:�������
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING: //������������
				//��ѯ���ݵĲ���
				String address = NumberAddressQueryUtils.queryNumber(incomingNumber);
				//Toast.makeText(getApplicationContext(), address, 1).show();
				myToast(address);
				break;
			case TelephonyManager.CALL_STATE_IDLE:
				//�绰�Ŀ���״̬���ҵ绰������ܾ�
				if(view!=null)
				{
					wm.removeView(view);
				}
				break;
			default:
				break;
			}
		}
	}
	
	class OutCallReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			String phone = getResultData();
			String address = NumberAddressQueryUtils.queryNumber(phone);
			//Toast.makeText(context, address, 1).show();
			myToast(address);
		}
		
	}
}
