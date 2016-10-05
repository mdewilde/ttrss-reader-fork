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

package org.ttrssreader.ico;

import android.graphics.Bitmap;
import android.util.Log;

import org.ttrssreader.ico.support.ICODecoder;
import org.ttrssreader.imageCache.CacheDirMaker;

import java.io.File;
import java.io.IOException;

/**
 * Holds any feed icon that is flagged as available by the tt-rss server.
 */
public class IconCache {

	private static final String TAG = IconCache.class.getName();

	private static final String ICON_DIR_NAME = "FEED_ICONS";
	private static final String EXTENSION = ".ico";

	private final String directory;

	public IconCache() {
		this.directory = CacheDirMaker.make(ICON_DIR_NAME);
	}

	public Bitmap getImage(int feedId) {
		Bitmap bitmap = null;
		if (isDiskCacheEnabled()) {
			File file = getFile(feedId);
			if (file.exists() && file.isFile()) {
				try {
					for (Bitmap bmp : ICODecoder.read(file)) {
						if (bitmap == null || bitmap.getHeight() < bmp.getHeight()) {
							bitmap = bmp;
						}
					}
				} catch (IOException e) {
					Log.w(TAG, "getImages(" + feedId + ")");
				}
			}
		}
		return bitmap;
	}

	public boolean isDiskCacheEnabled() {
		return directory != null;
	}

	public File getFile(int feedId) {
		return new File(directory, feedId + EXTENSION);
	}

}
