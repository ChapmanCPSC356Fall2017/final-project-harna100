package io.impaul.harna100.roundrobinpicker.activities;

import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import io.impaul.harna100.roundrobinpicker.R;
import io.impaul.harna100.roundrobinpicker.fragments.HomeFragment;
import io.impaul.harna100.roundrobinpicker.fragments.PlacesFragment;
import io.impaul.harna100.roundrobinpicker.interfaces.NavContainerInterface;

public class MainActivity extends NavContainer implements NavContainerInterface {

	private static final String TAG = "MainActivity";
	private FrameLayout fl_mainContainer;

	private Toolbar tb_mainToolbar;

	private ActionBarDrawerToggle drawerToggle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		getReferences();
		new SetNavProfileTask().execute();

		setSupportActionBar(tb_mainToolbar);


		setUpRecyclerView();
		setupToolBar();
		setListeners();

		setFragment(PlacesFragment.NewInstance(), false);

	}



	@Override
	protected void getReferences() {
		super.getReferences();

		fl_mainContainer = findViewById(R.id.fl_mainContainer);
		tb_mainToolbar = findViewById(R.id.tb_mainToolbar);
	}

	private void setListeners() {

	}



	private void setupToolBar(){
		getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
		drawerToggle = new ActionBarDrawerToggle(this,
				dl_drawer,
				R.string.drawer_open,
				R.string.drawer_close){
			public void onDrawerClosed(View view){
				super.onDrawerClosed(view);
			}
			public void onDrawerOpened(View drawerView){
				super.onDrawerOpened(drawerView);
			}
		};

		dl_drawer.addDrawerListener(drawerToggle);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(drawerToggle.onOptionsItemSelected(item)){
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
}
