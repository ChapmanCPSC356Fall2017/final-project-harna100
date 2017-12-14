package io.impaul.harna100.roundrobinpicker.room.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.text.TextUtils;

import com.google.android.gms.maps.model.LatLng;

import io.impaul.harna100.roundrobinpicker.models.PlaceModel;
import io.impaul.harna100.roundrobinpicker.places.models.DetailPlace;

@Entity(tableName = "places")
public class Place {
	@PrimaryKey(autoGenerate = true)
	private int id;

	private String name;

	@ColumnInfo(name = "photo_url")
	private String photoUrl;

	@ColumnInfo(name = "phone_number")
	private String phoneNumber;

	private String address;

	@ColumnInfo(name = "description")
	private String description;

	private double lat;

	private double lng;

	@ColumnInfo(name = "did_choose")
	private boolean didChoose;
	@ColumnInfo(name = "international_phone_number")
	private String internationalPhoneNumber;
	@ColumnInfo(name = "google_place_id")
	private String googlePlaceId;
	private double rating;
	private String url;
	private String hours;
	@ColumnInfo(name = "photo_path_on_device")
	private String photoPathOnDevice;

	public Place(){

	}

	public static Place NewPlace(DetailPlace detailPlace){
		Place toReturn = new Place();

		toReturn.address = detailPlace.formattedAdress;
		toReturn.phoneNumber = detailPlace.formmatedPhoneNumber;
		toReturn.internationalPhoneNumber = detailPlace.internationalPhoneNumber;
		toReturn.name = detailPlace.name;
		toReturn.googlePlaceId = detailPlace.placeId;
		toReturn.rating = detailPlace.rating;
		toReturn.url = detailPlace.url;
		if(detailPlace.openingHours != null){
			toReturn.hours = TextUtils.join("\n", detailPlace.openingHours.weekdayText);
		}
		toReturn.photoPathOnDevice = detailPlace.photoPathOnDevice;
		toReturn.lat = detailPlace.geometry.location.lat;
		toReturn.lng = detailPlace.geometry.location.lng;
		return toReturn;
	}

	public static Place NewPlace(PlaceModel placeModel){
		Place toReturn = new Place();
		if(placeModel.getPlaceId() != -1){
			toReturn.id = placeModel.getPlaceId();
		}
		toReturn.name = placeModel.getName();
		toReturn.address = placeModel.getAddress();
		toReturn.description = placeModel.getDescription();
		toReturn.phoneNumber = placeModel.getPhoneNumber();
		toReturn.photoUrl = placeModel.getPhotoUrl();
		toReturn.lat = placeModel.getLatLng().latitude;
		toReturn.lng = placeModel.getLatLng().longitude;


		return toReturn;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhotoUrl() {
		return photoUrl;
	}

	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	public LatLng getLatLng(){
		return new LatLng(getLat(), getLng());
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isDidChoose() {
		return didChoose;
	}

	public void setDidChoose(boolean didChoose) {
		this.didChoose = didChoose;
	}


	public String getInternationalPhoneNumber() {
		return internationalPhoneNumber;
	}

	public void setInternationalPhoneNumber(String internationalPhoneNumber) {
		this.internationalPhoneNumber = internationalPhoneNumber;
	}

	public String getGooglePlaceId() {
		return googlePlaceId;
	}

	public void setGooglePlaceId(String googlePlaceId) {
		this.googlePlaceId = googlePlaceId;
	}

	public double getRating() {
		return rating;
	}

	public void setRating(double rating) {
		this.rating = rating;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getHours() {
		return hours;
	}

	public void setHours(String hours) {
		this.hours = hours;
	}

	public String getPhotoPathOnDevice() {
		return photoPathOnDevice;
	}

	public void setPhotoPathOnDevice(String photoPathOnDevice) {
		this.photoPathOnDevice = photoPathOnDevice;
	}

	@Override
	public String toString() {
		return "Place{" +
				"id=" + id +
				", name='" + name + '\'' +
				", photoUrl='" + photoUrl + '\'' +
				", phoneNumber='" + phoneNumber + '\'' +
				", address='" + address + '\'' +
				", description='" + description + '\'' +
				", lat=" + lat +
				", lng=" + lng +
				", didChoose=" + didChoose +
				'}';
	}
}
