package io.impaul.harna100.roundrobinpicker.places;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import io.impaul.harna100.roundrobinpicker.activities.LoginActivity;
import io.impaul.harna100.roundrobinpicker.activities.SplashScreenActivity;
import io.impaul.harna100.roundrobinpicker.places.models.DetailPlace;
import io.impaul.harna100.roundrobinpicker.places.models.DetailRaw;
import io.impaul.harna100.roundrobinpicker.places.models.NearbyRaw;
import io.impaul.harna100.roundrobinpicker.places.models.RoughPlace;
import io.impaul.harna100.roundrobinpicker.room.RoomSingleton;
import io.impaul.harna100.roundrobinpicker.room.models.Place;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;

public class PlaceUtil {

	public static final String TAG = "PlaceUtil";

	private String key;

	public PlaceUtil(String key){
		this.key = key;
	}

	public List<DetailPlace> getNearByDetailPlaces(){
		return null;
	}

	public List<RoughPlace> getNearyByRoughPlaces(){
		return null;
	}

	public NearbyRawTask getNearbyRaw(String distance, String locationInLatLng, View progressbar, SplashScreenActivity splashScreenActivity){
		return new NearbyRawTask(distance, locationInLatLng, progressbar, splashScreenActivity);
	}

	protected String GetNearbyUrl(String distance, String locationInLatLng){
		String baseUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
//		String key = "AIzaSyCcEY0R4SXHcTwT7Y76pO2T8XbB7m1o10U";
//		String location = "33.793339,-117.852069";
		String[] location = {"location", locationInLatLng};
		String[] radius = {"radius", distance};
		String[] keyword = {"keyword", "restaurant"};
		String[] key = {"key", this.key};

		return GetUrl(baseUrl, key, location, radius, keyword);
	}

	protected String GetDetailUrl(String placeId){
		String baseUrl = "https://maps.googleapis.com/maps/api/place/details/json";

		String[] place = {"placeid", placeId};
		return GetUrl(baseUrl, place);
	}

	private String getPhotoUrl(String photoReference){
		String baseUrl = "https://maps.googleapis.com/maps/api/place/photo";
		String[] photo = {"photoreference", photoReference};
		String[] maxWidth = {"maxwidth", "400"};
		return GetUrl(baseUrl, photo, maxWidth);
	}

	private String GetUrl(String baseUrl , String[]... queryPairs){
		HttpUrl.Builder urlBuilder = HttpUrl.parse(baseUrl).newBuilder();
		urlBuilder.addQueryParameter("key", key);
		for (String[] queryPair : queryPairs) {
			urlBuilder.addQueryParameter(queryPair[0], queryPair[1]);
		}
		return urlBuilder.build().toString();
	}

	public static Response GetResponse(Request request){
		try {
			return new OkHttpClient().newCall(request).execute();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static JsonAdapter GetJsonAdapter(Class classToGet){
		Moshi moshi = new Moshi.Builder().build();
		return moshi.adapter(classToGet);
	}

	public class NearbyRawTask extends AsyncTask<Void, Void, List<NearbyRaw>>{

		private View progressbar;
		private String distance;
		private String locationInLatLng;
		private SplashScreenActivity splashScreenActivity;

		public NearbyRawTask(String distance, String locationInLatLng, View progressbar, SplashScreenActivity splashScreenActivity) {
			this.distance = distance;
			this.locationInLatLng = locationInLatLng;
			this.progressbar = progressbar;
			this.splashScreenActivity = splashScreenActivity;
		}

		@Override
		protected void onPreExecute() {
			progressbar.setVisibility(View.VISIBLE);
		}

		@Override
		protected List<NearbyRaw> doInBackground(Void... voids) {
			Request request = new Request.Builder()
					.url(GetNearbyUrl(distance, locationInLatLng))
					.get()
					.build();
			Response response = GetResponse(request);

			if(response == null){
				Log.e(TAG, "doInBackground: ", new Exception("Response was null"));
				return null;
			}

			JsonAdapter<NearbyRaw> jsonAdapter = GetJsonAdapter(NearbyRaw.class);

			try {
				ArrayList<NearbyRaw> toReturn = new ArrayList<>();
				toReturn.add(jsonAdapter.fromJson(response.body().source()));
				return toReturn;
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(List<NearbyRaw> nearbyRaw) {
			if(nearbyRaw == null){
				Log.e(TAG, "onPostExecute: ", new Exception("NearbyRaw was null"));
				return;
			}
			new NearbyDetailTask(this.progressbar, this.splashScreenActivity).execute(nearbyRaw.toArray(new NearbyRaw[nearbyRaw.size()]));
		}
	}
	public class NearbyDetailTask extends AsyncTask<NearbyRaw, Void, List<DetailPlace>>{

		private View progressbar;
		private SplashScreenActivity splashScreenActivity;
		public NearbyDetailTask(View progressbar, SplashScreenActivity splashScreenActivity) {
			this.progressbar = progressbar;
			this.splashScreenActivity = splashScreenActivity;
		}

		@Override
		protected List<DetailPlace> doInBackground(NearbyRaw... nearbyRaws) {
			List<DetailPlace> toReturn = new ArrayList<>();
			for (NearbyRaw nearbyRaw : nearbyRaws) {
				for (RoughPlace result : nearbyRaw.results) {
					Request request = new Request.Builder()
							.url(GetDetailUrl(result.placeId))
							.get()
							.build();
					Response response = GetResponse(request);

					JsonAdapter<DetailRaw> jsonAdapter = GetJsonAdapter(DetailRaw.class);
					try {
						toReturn.add(jsonAdapter.fromJson(response.body().source()).result);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			return toReturn;
		}

		@Override
		protected void onPostExecute(List<DetailPlace> detailPlaces) {
			new GetPhotosTask(this.progressbar, this.splashScreenActivity).execute(detailPlaces.toArray(new DetailPlace[detailPlaces.size()]));
		}
	}

	public class GetPhotosTask extends AsyncTask<DetailPlace, Void, List<DetailPlace>>{
		View progressBar;
		private SplashScreenActivity splashScreenActivity;
		public GetPhotosTask(View progressBar, SplashScreenActivity splashScreenActivity) {
			this.progressBar = progressBar;
			this.splashScreenActivity = splashScreenActivity;
		}

		@Override
		protected List<DetailPlace> doInBackground(DetailPlace... detailPlaces) {
			List<DetailPlace> toReturn = Arrays.asList(detailPlaces);

			for (DetailPlace detailPlace : toReturn) {
				if(detailPlace.photos != null && detailPlace.photos.length == 0){
					continue;
				}
				String photoReference = detailPlace.photos[0].photoReference;
				Request request = new Request.Builder()
						.url(getPhotoUrl(photoReference))
						.get()
						.build();
				Response response = GetResponse(request);
				BufferedSink sink = null;
				try {
					if (response != null && (response.body().contentType().type().equals("image"))) {
						String fileName = UUID.randomUUID().toString();
						File fileToSave = new File(progressBar.getContext().getExternalCacheDir() + File.separator + fileName + "." + response.body().contentType().subtype());
						sink = Okio.buffer(Okio.sink(fileToSave));

						sink.writeAll(response.body().source());
						detailPlace.photoPathOnDevice = fileToSave.getAbsolutePath();
						Log.d(TAG, "doInBackground: Saved at: " + detailPlace.photoPathOnDevice);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			return toReturn;
		}

		@Override
		protected void onPostExecute(List<DetailPlace> detailPlaces) {
			new AddToDbTask(this.progressBar, this.splashScreenActivity).execute(detailPlaces.toArray(new DetailPlace[detailPlaces.size()]));
		}
	}

	public class AddToDbTask extends AsyncTask<DetailPlace, Void, Void> {

		private View progressBar;
		private SplashScreenActivity splashScreenActivity;

		public AddToDbTask(View progressBar, SplashScreenActivity splashScreenActivity) {
			this.progressBar = progressBar;
			this.splashScreenActivity = splashScreenActivity;
		}

		@Override
		protected Void doInBackground(DetailPlace... detailPlaces) {
			for (DetailPlace detailPlace : detailPlaces) {
				RoomSingleton.GetDb(progressBar.getContext()).placeDao().insertPlaces(Place.NewPlace(detailPlace));
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			progressBar.setVisibility(View.GONE);
			splashScreenActivity.startActivity(new Intent(splashScreenActivity, LoginActivity.class));
			splashScreenActivity.finish();
		}
	}

}
