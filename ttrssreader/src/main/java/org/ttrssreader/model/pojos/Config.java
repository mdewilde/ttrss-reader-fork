/*
 * Copyright (c) 2016, Marceau Dewilde
 *
 * This file is part of ttrss-reader-fork. This program is free software; you
 * can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation;
 * either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details. You should have received a
 * copy of the GNU General Public License along with this program; If
 * not, see http://www.gnu.org/licenses/.
 */
package org.ttrssreader.model.pojos;

import com.google.gson.annotations.SerializedName;

/**
 * tt-rss configuration parameters, as returned by getConfig API call.
 */
public class Config {

	/**
	 * path to icons on the server filesystem
	 */
	@SerializedName("icons_dir")
	public String iconsDir;

	/**
	 * path to icons when requesting them over http
	 */
	@SerializedName("icons_url")
	public String iconsUrl;

	/**
	 * whether update daemon is running
	 */
	@SerializedName("daemon_is_running")
	public boolean daemonIsRunning;

	/**
	 * amount of subscribed feeds (this can be used to refresh feedlist when this amount changes)
	 */
	@SerializedName("num_feeds")
	public int numFeeds;

	@Override
	public String toString() {
		return "Config{" +
				"iconsDir='" + iconsDir + '\'' +
				", iconsUrl='" + iconsUrl + '\'' +
				", daemonIsRunning=" + daemonIsRunning +
				", numFeeds=" + numFeeds +
				'}';
	}

}
