package io.impaul.harna100.roundrobinpicker.fragments;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Iterator;
import java.util.List;

import io.impaul.harna100.roundrobinpicker.R;
import io.impaul.harna100.roundrobinpicker.SharedPrefSingleton;
import io.impaul.harna100.roundrobinpicker.adapters.PlaceReviewListAdapter;
import io.impaul.harna100.roundrobinpicker.room.RoomSingleton;
import io.impaul.harna100.roundrobinpicker.room.models.Place;

public class PlacesFragment extends MyFragment {
	private List<Place> places;
	private PlaceReviewListAdapter adapter;
	private RecyclerView rv_placesList;

	public static PlacesFragment NewInstance(){
		PlacesFragment toReturn = new PlacesFragment();

		return toReturn;
	}

	@Override
	public void onResume() {
		super.onResume();
		new FillPlacesTask().execute();
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_places, container, false);
		getReferences(v);
		return v;
	}

	private void getReferences(View v) {
		rv_placesList = v.findViewById(R.id.rv_placeReviewList);
	}

	private void setupRecyclerView(){

		adapter = new PlaceReviewListAdapter(places, getActivity());
		//TODO check if places is filled
		rv_placesList.setAdapter(adapter);
		rv_placesList.setLayoutManager(new LinearLayoutManager(getContext()));
	}

	@Override
	public String getName() {
		return "PlacesFragment";
	}

	private class FillPlacesTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... voids) {
			places = RoomSingleton.GetDb(getContext()).userPlacesDao().getPlacesFromUser(SharedPrefSingleton.GetUserId(getContext()));
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
				places.removeIf(p -> !p.isDidChoose());
			}
			else{
				Iterator<Place> iter = places.iterator();
				while (iter.hasNext()) {
					Place next = iter.next();
					if(!next.isDidChoose()){
						iter.remove();
					}
				}
			}

			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					setupRecyclerView();
				}
			});

			return null;
		}
	}
}
