package io.impaul.harna100.roundrobinpicker.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.impaul.harna100.roundrobinpicker.R;
import io.impaul.harna100.roundrobinpicker.activities.NavContainer;
import io.impaul.harna100.roundrobinpicker.interfaces.NavContainerInterface;
import io.impaul.harna100.roundrobinpicker.models.FragmentTypes;
import io.impaul.harna100.roundrobinpicker.models.NavItemModel;


public class NavListAdapter extends RecyclerView.Adapter<NavListAdapter.NavViewHolder> {

	private List<NavItemModel> navItems;

	public NavListAdapter() {
		navItems = new ArrayList<>();
		navItems.add(new NavItemModel("Home", R.drawable.ic_home, FragmentTypes.HOME_FRAGMENT));
//		navItems.add(new NavItemModel("Groups", R.drawable.ic_people, FragmentTypes.HOME_FRAGMENT));
//		navItems.add(new NavItemModel("Events", R.drawable.ic_dates, FragmentTypes.HOME_FRAGMENT));
		navItems.add(new NavItemModel("Logout", R.drawable.ic_logout, FragmentTypes.LOGOUT));
	}

	@Override
	public NavViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		View v = inflater.inflate(R.layout.cell_nav_item, parent, false);

		return new NavViewHolder(v);
	}

	@Override
	public void onBindViewHolder(NavViewHolder holder, int position) {
		holder.setNavModel(navItems.get(position));
	}

	@Override
	public int getItemCount() {
		return navItems.size();
	}

	class NavViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

		private NavItemModel navItemModel;
		private ImageView iv_navCell;
		private TextView tv_navCell;
		private RelativeLayout rl_navCell;

		public NavViewHolder(View itemView) {
			super(itemView);
			getReferences(itemView);
			itemView.setOnClickListener(this);
		}

		private void getReferences(View itemView) {
			iv_navCell = itemView.findViewById(R.id.iv_navCellIcon);
			tv_navCell = itemView.findViewById(R.id.tv_navTvCell);
			rl_navCell = itemView.findViewById(R.id.rl_navCell);
		}

		public void setNavModel(NavItemModel navItemModel) {
			this.navItemModel = navItemModel;
			setLayout();
			setListeners();
		}

		private void setListeners() {

		}

		private void setLayout(){
			iv_navCell.setImageResource(navItemModel.getNavIcon());
			tv_navCell.setText(navItemModel.getNavName());
		}

		@Override
		public void onClick(View view) {
			NavContainerInterface nci = (NavContainerInterface) view.getContext();
			nci.changeFragment(navItemModel.getFragType());
			nci.hideDrawer();
		}
	}
}
