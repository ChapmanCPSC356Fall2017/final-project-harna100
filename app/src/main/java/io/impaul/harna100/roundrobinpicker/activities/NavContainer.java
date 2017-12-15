package io.impaul.harna100.roundrobinpicker.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import io.impaul.harna100.roundrobinpicker.R;
import io.impaul.harna100.roundrobinpicker.SharedPrefSingleton;
import io.impaul.harna100.roundrobinpicker.adapters.NavListAdapter;
import io.impaul.harna100.roundrobinpicker.fragments.HomeFragment;
import io.impaul.harna100.roundrobinpicker.fragments.MyFragment;
import io.impaul.harna100.roundrobinpicker.fragments.PlacesFragment;
import io.impaul.harna100.roundrobinpicker.interfaces.NavContainerInterface;
import io.impaul.harna100.roundrobinpicker.models.FragmentTypes;
import io.impaul.harna100.roundrobinpicker.room.RoomSingleton;
import io.impaul.harna100.roundrobinpicker.room.models.User;


public abstract class NavContainer extends AppCompatActivity implements NavContainerInterface {

	protected TextView tv_navHeaderProfile;
	protected TextView tv_navHeaderPersonName;
	protected TextView tv_navHeaderEmail;
	protected NavigationView nv_navBar;
	protected DrawerLayout dl_drawer;
	protected RecyclerView rv_navList;
	protected User currUser;
	protected FragmentTypes currFragment = FragmentTypes.PLACES_FRAGMENT;

	protected void getReferences(){
		nv_navBar = findViewById(R.id.nv_navBar);
		dl_drawer = findViewById(R.id.dl_drawer);
		rv_navList = findViewById(R.id.rv_navList);
		tv_navHeaderEmail = findViewById(R.id.tv_navHeaderEmail);
		tv_navHeaderPersonName = findViewById(R.id.tv_navHeaderPersonName);
		tv_navHeaderProfile = findViewById(R.id.tv_navHeaderProfile);
	}

	protected void setNavProfile(){
		currUser = RoomSingleton.GetDb(this).userDao().findOneById(SharedPrefSingleton.GetUserId(this));

		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				tv_navHeaderProfile.setText(currUser.getName().substring(0,1).toUpperCase());
				tv_navHeaderPersonName.setText(currUser.getName());
				tv_navHeaderEmail.setText(currUser.getEmail());
			}
		});

	}


	protected void setUpRecyclerView() {
		rv_navList.setLayoutManager(new LinearLayoutManager(this));
		rv_navList.setAdapter(new NavListAdapter());
		rv_navList.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
	}

	protected void setFragment(MyFragment fragToSet, boolean addToBackStack) {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.replace(R.id.fl_mainContainer, fragToSet);
		if(addToBackStack){
			ft.addToBackStack(fragToSet.getName());
		}
		ft.commit();
	}

	@Override
	public void changeFragment(FragmentTypes fragment) {
		if(fragment == currFragment){
			return;
		}
		currFragment = fragment;
		switch(fragment){
			case HOME_FRAGMENT:
//				setFragment(HomeFragment.NewInstance(), true);
				Toast.makeText(this, "Home Screen Not Implemented", Toast.LENGTH_SHORT).show();
				currFragment = FragmentTypes.PLACES_FRAGMENT;
				break;
			case PLACES_FRAGMENT:
				setFragment(PlacesFragment.NewInstance(), true);
				break;
			case LOGOUT:
				SharedPrefSingleton.Logout(this);
				startActivity(new Intent(this, LoginActivity.class));
				finish();
				break;
			default:
				break;
		}
	}

	@Override
	public void hideDrawer() {
		((DrawerLayout) findViewById(R.id.dl_drawer)).closeDrawer(Gravity.START);
	}

	protected class SetNavProfileTask extends AsyncTask<Void,Void,Void>{
		@Override
		protected Void doInBackground(Void... voids) {
			setNavProfile();
			return null;
		}
	}
}
