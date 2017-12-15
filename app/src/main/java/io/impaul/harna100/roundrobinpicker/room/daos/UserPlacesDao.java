package io.impaul.harna100.roundrobinpicker.room.daos;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.RoomWarnings;

import java.util.List;

import io.impaul.harna100.roundrobinpicker.room.models.Place;
import io.impaul.harna100.roundrobinpicker.room.models.User;
import io.impaul.harna100.roundrobinpicker.room.models.UserPlaces;

@Dao
public interface UserPlacesDao {

	@Query("SELECT" +
			"  user_places_trimmed.did_choose AS did_choose," +
			"  places.id                      AS id," +
			"  name," +
			"  photo_url," +
			"  phone_number," +
			"  address," +
			"  description," +
			"  lat," +
			"  lng," +
			"  rating," +
			"  international_phone_number," +
			"  google_place_id," +
			"  url," +
			"  hours," +
			"  photo_path_on_device" +
			" FROM (SELECT *" +
			"      FROM user_places" +
			"      WHERE user_id = :userId) AS user_places_trimmed" +
			"  JOIN places ON places.id = user_places_trimmed.place_id")
	List<Place> getPlacesFromUser(int userId);

	@Query("SELECT * FROM (SELECT * FROM user_places WHERE place_id = :placeId) AS user_places_trimmed " +
			"JOIN users ON users.id = user_places_trimmed.user_id ")
	List<User> getUsersFromPlace(int placeId);

	@Insert
	void insertPlaces(UserPlaces... places);

	@Query("DELETE FROM user_places WHERE place_id=:placeId AND user_id=:userId")
	void deleteUserPlace(int placeId, int userId);

	@Query("DELETE FROM user_places")
	void nukeTable();
}
