package com.ly.mobilesafe.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class NumberAddressQueryUtils {
	private static String path = 
			"data/data/com.ly.mobilesafe/files/address.db";
	
	public static String queryNumber(String number)
	{
		String address = number;
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);
		Cursor cursor;
		String sql="select location from data2 where data2.[id] = (select outkey from data1 where data1.[id] = ?)";
		//�ֻ�����13 14 15 16 18������ʽ
		if(number.matches("^1[345678]\\d{9}$"))
		{
			
			cursor = db.rawQuery(sql, new String[]{number.substring(0, 7)});
			while(cursor.moveToNext())
			{
				address = cursor.getString(0);
			}
			cursor.close();
		}else{
			switch (number.length()) {
			case 3:
				// 110
				address = "�˾�����";
				break;
			case 4:
				// 5554
				address = "ģ����";
				break;
			case 5:
				// 10086
				address = "�ͷ��绰";
				break;
			case 7:
				//
				address = "���غ���";
				break;
			case 8:
				address = "���غ���";
				break;
			default:
				if(number.length()>10&&number.startsWith("0"))
				{
					sql = "select location from data2 where area = ?";
					cursor = db.rawQuery(sql, new String[]{number.substring(1, 3)});
					while(cursor.moveToNext())
					{
						String location = cursor.getString(0);
						address = location.substring(0, location.length()-2);
						//address = location.substring(0, location.length());
					}
					cursor.close();
					
					cursor = db.rawQuery(sql,new String[]{number.substring(1, 4)});
					while(cursor.moveToNext())
					{
						String location = cursor.getString(0);
						address = location.substring(0, location.length()-2);
						//address = location.substring(0, location.length());
					}
					cursor.close();
					
				}
				break;
			}
		}
		return address;
		
	}
}
