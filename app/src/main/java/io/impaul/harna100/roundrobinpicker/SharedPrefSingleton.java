package io.impaul.harna100.roundrobinpicker;

import android.content.Context;
import android.content.SharedPreferences;

import io.impaul.harna100.roundrobinpicker.activities.LoginActivity;

public class SharedPrefSingleton {
	private static SharedPrefSingleton singleton;
	private SharedPreferences sharedPreferences;

	private SharedPrefSingleton(Context context){
		sharedPreferences = context.getApplicationContext().getSharedPreferences("main", Context.MODE_PRIVATE);
	}

	private static SharedPrefSingleton GetInstance(Context context){
		if(singleton == null){
			singleton = new SharedPrefSingleton(context);
		}
		return singleton;
	}

	private static SharedPreferences GetSharedPreferences(Context context){
		return SharedPrefSingleton.GetInstance(context).sharedPreferences;
	}

	public static void SetUserId(Context context, int toSet){
		GetSharedPreferences(context).edit().putInt("user_id", toSet).apply();
	}

	public static int GetUserId(Context context){
		return GetSharedPreferences(context).getInt("user_id",-1);
	}

	public static boolean isFirstRun(Context context) {
		if(!GetSharedPreferences(context).contains("first_run")){
			GetSharedPreferences(context).edit().putBoolean("first_run", false).apply();
			return true;
		}
		else{
			return false;
		}

	}

	public static void Logout(Context context) {
		GetSharedPreferences(context).edit().remove("user_id").apply();
	}
}
