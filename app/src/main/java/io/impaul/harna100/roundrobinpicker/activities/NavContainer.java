package io.impaul.harna100.roundrobinpicker.activities;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;

import io.impaul.harna100.roundrobinpicker.R;
import io.impaul.harna100.roundrobinpicker.SharedPrefSingleton;
import io.impaul.harna100.roundrobinpicker.fragments.HomeFragment;
import io.impaul.harna100.roundrobinpicker.fragments.MyFragment;
import io.impaul.harna100.roundrobinpicker.interfaces.NavContainerInterface;
import io.impaul.harna100.roundrobinpicker.models.FragmentTypes;


public abstract class NavContainer extends AppCompatActivity implements NavContainerInterface {



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
		switch(fragment){
			case HOME_FRAGMENT:
				setFragment(HomeFragment.NewInstance(), true);
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
}
