package com.example.base.db;

import java.util.ArrayList;

import com.example.base.type.User;

public interface Database {
	
	/**
	 * Adds radio to list of recent radios
	 * 
	 * @param radio
	 */
	public void addUserToRecent(User user);
	
	/**
	 * Retrieves recent radios
	 * 
	 * @param limit
	 * @return
	 */
	public ArrayList<User> getRecentUsers(int limit);
}
