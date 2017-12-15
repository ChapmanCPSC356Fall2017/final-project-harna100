package io.impaul.harna100.roundrobinpicker.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;


import io.impaul.harna100.roundrobinpicker.R;
import io.impaul.harna100.roundrobinpicker.SharedPrefSingleton;
import io.impaul.harna100.roundrobinpicker.dialogs.PlaceDetailDialog;
import io.impaul.harna100.roundrobinpicker.models.PlaceModel;
import io.impaul.harna100.roundrobinpicker.room.RoomSingleton;
import io.impaul.harna100.roundrobinpicker.room.models.Place;

public class PlaceReviewListAdapter extends RecyclerView.Adapter<PlaceReviewListAdapter.PlaceReviewViewHolder>{
	private List<Place> places;
	private Activity activityContext;

	public PlaceReviewListAdapter(List<Place> places, Activity act) {
		this.places = places;
		this.activityContext = act;
	}

	@Override
	public PlaceReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		View v = inflater.inflate(R.layout.card_place_review, parent, false);

		return new PlaceReviewViewHolder(v, this);
	}

	@Override
	public void onBindViewHolder(PlaceReviewViewHolder holder, int position) {
		holder.updateValues(places.get(position));
	}

	@Override
	public int getItemCount() {
		return places.size();
	}

	private void remove(int adapterPosition) {
		places.remove(adapterPosition);
		activityContext.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				notifyItemRemoved(adapterPosition);
			}
		});

	}

	public class PlaceReviewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {
		private static final String TAG = "PlaceReviewViewHolder";

		private TextView tv_cardPlaceName;
		private TextView tv_cardPlaceAddress;
		private ImageView iv_cardPlacePhoto;
		private ImageButton ib_cardPlaceMenu;
		private PopupMenu pm_Menu;
		private PlaceReviewListAdapter adapter;

		private Place place;

		public PlaceReviewViewHolder(View itemView, PlaceReviewListAdapter placeReviewListAdapter) {
			super(itemView);
			this.adapter = placeReviewListAdapter;
			getReferences(itemView);
			itemView.setOnClickListener(this);
			pm_Menu = new PopupMenu(itemView.getContext(), this.ib_cardPlaceMenu);
			pm_Menu.inflate(R.menu.menu_card_popup);

			pm_Menu.setOnMenuItemClickListener(this);
			ib_cardPlaceMenu.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					pm_Menu.show();
				}
			});
		}

		private void getReferences(View v){
			tv_cardPlaceName = v.findViewById(R.id.tv_cardPlaceReviewName);
			iv_cardPlacePhoto = v.findViewById(R.id.iv_cardPlaceReviewPhoto);
			tv_cardPlaceAddress = v.findViewById(R.id.tv_cardPlaceReviewAddress);
			ib_cardPlaceMenu = v.findViewById(R.id.ib_cardPlaceReviewMenu);
		}

		public void updateValues(Place place){
			this.place = place;
			tv_cardPlaceName.setText(place.getName());
			tv_cardPlaceAddress.setText(place.getAddress());
			Picasso.with(iv_cardPlacePhoto.getContext())
					.load("file:" + place.getPhotoPathOnDevice())
					.fit()
					.centerCrop()
					.into(iv_cardPlacePhoto);
		}

		@Override
		public void onClick(View view) {
			Dialog d = new PlaceDetailDialog(view.getContext(), new PlaceModel(place));
			d.show();
			d.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		}

		@Override
		public boolean onMenuItemClick(MenuItem menuItem) {
			switch (menuItem.getItemId()){
				case R.id.menu_share:
					Log.d(TAG, "onMenuItemClick: Shared Pressed");
					Intent intent = new Intent();
					intent.setAction(Intent.ACTION_SEND);
					intent.setType("text/plain");
					String shareMessage = "Let's eat at " + place.getName() + "!\n" + place.getAddress() + "\n" + place.getUrl();
					intent.putExtra(Intent.EXTRA_TEXT, shareMessage);
					activityContext.startActivity(intent);
					return true;
				case R.id.menu_card_delete:
					Log.d(TAG, "onMenuItemClick: Delete Pressed");
					new DeletePlaceTask().execute();
					return true;
			}
			return false;
		}

		private class DeletePlaceTask extends AsyncTask<Void, Void, Void>{

			@Override
			protected Void doInBackground(Void... voids) {
				RoomSingleton.GetDb(tv_cardPlaceName.getContext()).userPlacesDao()
						.deleteUserPlace(place.getId(), SharedPrefSingleton.GetUserId(tv_cardPlaceName.getContext()));
				adapter.remove(getAdapterPosition());

				return null;
			}
		}
	}

}
