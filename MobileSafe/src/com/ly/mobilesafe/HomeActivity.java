package com.ly.mobilesafe;

import com.ly.mobilesafe.R.string;
import com.ly.mobilesafe.utils.MD5Utils;

import android.os.Bundle;
import android.renderscript.Mesh.Primitive;
import android.text.TextUtils;
import android.util.Log;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends Activity {

	protected static final String TAG = "HomeActivity";
	
	private GridView list_home;
	private ToolListAdapter adapter;
	private SharedPreferences sp;
	
	private static String[] names = {
		"�ֻ�����","ͨѶ��ʿ","�������",
		"���̹���","����ͳ��","�ֻ�ɱ��",
		"��������","�߼�����","��������"
	};
	private static int[] ids = {
		R.drawable.safe,R.drawable.callmsgsafe,R.drawable.app,
		R.drawable.taskmanager,R.drawable.netmanager,R.drawable.trojan,
		R.drawable.sysoptimize,R.drawable.atools,R.drawable.settings
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		list_home = (GridView) findViewById(R.id.list_home);
		adapter = new ToolListAdapter();
		list_home.setAdapter(adapter);
		list_home.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = null;
				switch (position) {
				case 0: //�����ֻ�����ҳ��
					showLostFindDialog();
					break;
				case 7:
					intent = new Intent(HomeActivity.this,AtoolsActivity.class);
					startActivity(intent);
					break;
				case 8:
					intent = new Intent(HomeActivity.this,SettingActivity.class);
					startActivity(intent);
					break;

				default:
					break;
				}
			}
			
		});
		
	}

	protected void showLostFindDialog() {
		//�ж��Ƿ����ù�����
		if(isSetupPwd())
		{
			showEnterDialog();
		}else{
			showSetupPwdDialog();
		}
	}

	private EditText et_setup_pwd;
	private EditText et_setup_confirm;
	private Button btnOk;
	private Button btnCancel;
	private AlertDialog dialog;
	/**
	 * ��������Ի��� 
	 */
	private void showSetupPwdDialog() {
//		EditText et_setup_pwd;
//		Button btnOk;
//		Button btnCancel;
		
		AlertDialog.Builder builder = new Builder(HomeActivity.this);
		View view = View.inflate(HomeActivity.this, R.layout.dialog_setup_password, null);
		et_setup_pwd = (EditText) view.findViewById(R.id.et_setup_pwd);
		et_setup_confirm = (EditText) view.findViewById(R.id.et_setup_confirm);
		btnOk = (Button) view.findViewById(R.id.ok);
		btnCancel = (Button) view.findViewById(R.id.cancel);
		dialog = builder.create();
		dialog.setView(view,0,0,0,0);
		dialog.show();
		btnOk.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String password = et_setup_pwd.getText().toString().trim();
				String pwd_confirm = et_setup_confirm.getText().toString().trim();
				if(TextUtils.isEmpty(password)||TextUtils.isEmpty(pwd_confirm))
				{
					Toast.makeText(HomeActivity.this, "����Ϊ��", 0).show();
					return;
				}
				//�ж��Ƿ�һ�²�ȥ����
				if(password.equals(pwd_confirm))
				{
					Editor editor = sp.edit();
					editor.putString("password", MD5Utils.md5Password(password));
					editor.commit();
					dialog.dismiss();
					Log.i(TAG, "�ѶԻ��������������ֻ�����ҳ��");
					Intent intent = new Intent(HomeActivity.this,LostFindActivity.class);
					startActivity(intent);
				}else{
					Toast.makeText(HomeActivity.this, "���벻һ��", 0).show();
					return ;
				}
			}
		});
		btnCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		
	}

	private void showEnterDialog() {
		AlertDialog.Builder builder = new Builder(HomeActivity.this);
		View view = View.inflate(HomeActivity.this, R.layout.dialog_enter_password, null);
		dialog = builder.create();
		dialog.setView(view, 0, 0, 0, 0);
		dialog.show();
		et_setup_pwd = (EditText) view.findViewById(R.id.et_setup_pwd);
		btnOk = (Button) view.findViewById(R.id.ok);
		btnCancel = (Button) view.findViewById(R.id.cancel);
		btnOk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String password = et_setup_pwd.getText().toString().trim();
				String savePassword = sp.getString("password", "");
				if(TextUtils.isEmpty(password)){
					Toast.makeText(HomeActivity.this, "����Ϊ��", 1).show();
					return;
				}
				
				if(MD5Utils.md5Password(password).equals(savePassword)){
					//�������������֮ǰ���õ�����
					//�ѶԻ���������������ҳ�棻
					dialog.dismiss();
					Log.i(TAG, "�ѶԻ��������������ֻ�����ҳ��");
					Intent intent = new Intent(HomeActivity.this,LostFindActivity.class);
					startActivity(intent);
					
				}else{
					Toast.makeText(HomeActivity.this, "�������", 1).show();
					et_setup_pwd.setText("");
					return;
				}
			}
		});
		btnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
	}

	private boolean isSetupPwd() {
		String password = sp.getString("password", null);
		return !TextUtils.isEmpty(password);
	}

	private class ToolListAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return names.length;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = View.inflate(HomeActivity.this,
					R.layout.list_item_home, null);
			ImageView iv_item = (ImageView) view.findViewById(R.id.iv_item);
			TextView tv_item = (TextView) view.findViewById(R.id.tv_item);
			iv_item.setImageResource(ids[position]);
			tv_item.setText(names[position]);
			return view;
		}
		
	}

}
