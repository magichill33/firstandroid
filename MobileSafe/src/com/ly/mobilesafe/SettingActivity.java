package com.ly.mobilesafe;

import com.ly.mobilesafe.service.AddressService;
import com.ly.mobilesafe.service.CallSmsSafeService;
import com.ly.mobilesafe.ui.SettingClickView;
import com.ly.mobilesafe.ui.SettingItemView;
import com.ly.mobilesafe.utils.ServiceUtils;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SettingActivity extends Activity {

	private SettingItemView siv_update;
	private SharedPreferences sp;
	
	//�����Ƿ�����ʾ������
	private SettingItemView siv_show_address;
	private Intent showAddress;
	
	//���ù�������ʾ����
	private SettingClickView scv_changebg;
	
	//��������������
	private SettingItemView siv_callsms_safe;
	private Intent callSmsSafeIntent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		siv_update = (SettingItemView) findViewById(R.id.siv_update);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		
		boolean update = sp.getBoolean("update", false);
		if(update)
		{
			//�Զ������Ѿ�����
			siv_update.setChecked(true);
		}else{
			//�Զ������Ѿ��ر�
			siv_update.setChecked(false);
		}
		
		siv_update.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Editor editor = sp.edit();
				if(siv_update.isChecked())
				{
					siv_update.setChecked(false);
					editor.putBoolean("update", false);
					
				}else{
					siv_update.setChecked(true);
					editor.putBoolean("update",true);
				}
				editor.commit();
			}
		});
		
		//���ú����������ʾ�ռ�
		siv_show_address = (SettingItemView)findViewById(R.id.siv_show_address);
		showAddress = new Intent(this, AddressService.class);
		boolean isServiceRunning = ServiceUtils.
				isServiceRunning(SettingActivity.this, AddressService.class.getName());
		if(isServiceRunning)
		{
			siv_show_address.setChecked(true);
		}
		else
		{
			siv_show_address.setChecked(false);
		}
		
		siv_show_address.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(siv_show_address.isChecked()){
					// ��Ϊ��ѡ��״̬
					siv_show_address.setChecked(false);
					stopService(showAddress);
				}else{
					// ѡ��״̬
					siv_show_address.setChecked(true);
					startService(showAddress);
				}
				
			}
		});
		
		//���ú����������ʾ����
		scv_changebg = (SettingClickView) findViewById(R.id.scv_changebg);
		scv_changebg.setTitle("��������ʾ����");
		final String[] items = {"��͸��","������","��ʿ��","������","ƻ����"};
		final int which = sp.getInt("which", 0);
		scv_changebg.setDesc(items[which]);
		
		scv_changebg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				int idx = sp.getInt("which", 0);
				AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
				builder.setSingleChoiceItems(items, idx, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Editor editor = sp.edit();
						editor.putInt("which", which);
						editor.commit();
						scv_changebg.setDesc(items[which]);
						
						dialog.dismiss();
					}
				});
				
				builder.setNegativeButton("ȡ��", null);
				builder.show();
			}
		});
		
		//��������������
		siv_callsms_safe = (SettingItemView) findViewById(R.id.siv_callsms_safe);
		callSmsSafeIntent = new Intent(this,CallSmsSafeService.class);
		siv_callsms_safe.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(siv_callsms_safe.isChecked())
				{
					siv_callsms_safe.setChecked(false);
					stopService(callSmsSafeIntent);
				}else{
					siv_callsms_safe.setChecked(true);
					startService(callSmsSafeIntent);
				}
			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		showAddress = new Intent(this, AddressService.class);
		boolean isServiceRunning = ServiceUtils.
				isServiceRunning(SettingActivity.this, AddressService.class.getName());
		if(isServiceRunning)
		{
			siv_show_address.setChecked(true);
		}
		else
		{
			siv_show_address.setChecked(false);
		}
		
		boolean isCallSmsServiceRunning = ServiceUtils.
				isServiceRunning(SettingActivity.this, CallSmsSafeService.class.getName());
		if(isCallSmsServiceRunning)
		{
			siv_callsms_safe.setChecked(true);
		}else{
			siv_callsms_safe.setChecked(false);
		}
	}


}
