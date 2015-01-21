package com.ly.mobilesafe.test;

import java.util.Random;

import android.test.AndroidTestCase;

import com.ly.mobilesafe.dao.BlackNumberDao;


public class TestBlackDB extends AndroidTestCase {
	public void testAdd() throws Exception {
		BlackNumberDao dao = new BlackNumberDao(getContext());
		long basenumber = 13500000000l;
		Random random = new Random();
		for (int i = 0; i < 100; i++) {
			dao.add(String.valueOf(basenumber+i), String.valueOf(random.nextInt(3)+1));
		}
	}
}
