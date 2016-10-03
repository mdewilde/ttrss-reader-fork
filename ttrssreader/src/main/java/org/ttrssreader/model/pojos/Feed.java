/*
 * Copyright (c) 2015, Nils Braden
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



public class Feed implements Comparable<Feed> {

	public static final String ID = "id";
	public static final String CATEGORY_ID = "cat_id";
	public static final String TITLE = "title";
	public static final String URL = "feed_url";
	public static final String UNREAD = "unread";
	public static final String HAS_ICON = "has_icon";
	public static final String LAST_UPDATED = "last_updated";

	public int id = 0;
	public int categoryId = -1;
	public String title = null;
	public String url = null;
	public int unread = 0;
	public boolean hasIcon = false;
	public long lastUpdated = 0l;

	@Override
	public int compareTo(Feed fi) {
		return title.compareToIgnoreCase(fi.title);
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Feed) {
			Feed other = (Feed) o;
			return (id == other.id);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return id + "".hashCode();
	}

	public boolean isValid() {
		if (id != -1 || categoryId == -2) { // normal feed (>0) or label (-2)
			if (title != null) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return "Feed{" +
				"id=" + id +
				", categoryId=" + categoryId +
				", title='" + title + '\'' +
				", url='" + url + '\'' +
				", unread=" + unread +
				", hasIcon=" + hasIcon +
				'}';
	}
}
