package io.impaul.harna100.roundrobinpicker.room.daos;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import io.impaul.harna100.roundrobinpicker.room.models.User;

@Dao
public interface UserDao {

	@Query("SELECT * FROM users")
	List<User> getAll();

	@Query("SELECT * FROM users WHERE id IN (:userIds)")
	List<User> loadAllByIds(int[] userIds);

	@Query("SELECT * FROM users WHERE name LIKE :nameToSearch LIMIT 1")
	User findByName(String nameToSearch);

	@Query("SELECT * FROM users WHERE email=:emailToAuth AND password=:passwordToAuth")
	User authUser(String emailToAuth, String passwordToAuth);

	@Query("SELECT COUNT(*) FROM users WHERE email=:emailToCheck")
	int emailExists(String emailToCheck);

	@Query("SELECT COUNT(*) FROM users WHERE username=:usernameToCheck")
	int userExists(String usernameToCheck);

	@Query("SELECT * FROM users WHERE id=:userId")
	User findOneById(int userId);

	@Insert
	long[] insertAll(User... users);

	@Delete
	void delete(User user);

	@Query("DELETE FROM users")
	void nukeTable();
}
