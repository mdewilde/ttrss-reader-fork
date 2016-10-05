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

import android.net.Uri;
import android.util.Log;

import org.ttrssreader.controllers.Controller;
import org.ttrssreader.controllers.DBHelper;
import org.ttrssreader.model.pojos.Config;
import org.ttrssreader.networkchange.NetworkChangeSubscriber;
import org.ttrssreader.networkchange.NetworkState;
import org.ttrssreader.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;

public class IconCacher implements Runnable, NetworkChangeSubscriber {

	private static final String TAG = IconCacher.class.getSimpleName();

	private final AtomicBoolean started = new AtomicBoolean(false);
	private final AtomicBoolean mustStop = new AtomicBoolean(false);
	private final IconCache iconCache = Controller.getInstance().getIconCache();

	/**
	 * The minimum network type necessary for this task to do work.
	 * Never {@code null}, as it is final and constructor verifies non-null state
	 */
	private final NetworkState mininalNetworkState;

	public IconCacher(NetworkState networkState) {
		if (networkState == null) {
			throw new IllegalArgumentException("networkState argument can not be null");
		}
		this.mininalNetworkState = networkState;
	}

	@Override
	public void run() {
		// IconCacher instances are single-use
		if (started.compareAndSet(false, true)) {
			Controller.getInstance().subscribe(this);
			try {
				download();
			} finally {
				Controller.getInstance().unsubscribe(this);
			}
		}
	}

	@Override
	public boolean isAcceptableState(NetworkState state) {
		return state != null && !mininalNetworkState.isLessRestrictedThan(state);
	}

	@Override
	public void networkChanged(NetworkState state) {
		// this is a public method - check to make sure
		if (!isAcceptableState(state)) {
			mustStop.compareAndSet(false, true);
		}
	}

	private void download() {

		long time = System.currentTimeMillis();

		List<Integer> ids = DBHelper.getInstance().getFeedIdsWithIcon();
		if (ids.isEmpty()) {
			// no feeds with icons -> we're done
			return;
		}

		// where do we get the icons?
		Config config = Controller.getInstance().getConnector().getConfig();
		if (config == null) {
			Log.w(TAG, "download() unable to determine feed icon url");
			return;
		}

		Uri uri = Controller.getInstance().uriBuilder().path(config.iconsUrl).build();

		for (int id : ids) {
			Uri imageUrl = uri.buildUpon().appendPath(id + ".ico").build();
			try {
				long urlSize = downloadToFile(imageUrl, iconCache.getFile(id));
			} catch (Throwable t) {
				Log.e(TAG, "DownloadIconTask.run()", t);
			} finally {

			}
		}

		Log.i(TAG, String.format("download() %s ms", (System.currentTimeMillis() - time)));
	}

	/**
	 * Downloads a given URL directly to a file,
	 *
	 * @param uri the URL of the file
	 * @param file        the destination file
	 * @return length of downloaded file or negated file length if downloaded with errors.
	 * So, if returned value less or equals to 0, then the file was not cached.
	 */
	private long downloadToFile(Uri uri, File file) {
		if (mustStop.get()) return 0;
		if (!prepareEmptyFile(file)) return -1;
		if (mustStop.get()) return 0;

		long written = 0;

		try (FileOutputStream fos = new FileOutputStream(file)) {
			if (mustStop.get()) throw new InterruptedIOException("download cancelled");

			URL url = new URL(uri.toString());
			URLConnection connection = Controller.getInstance().openConnection(url);
			connection.setConnectTimeout((int) (Utils.SECOND * 2));
			connection.setReadTimeout((int) Utils.SECOND);

			try (InputStream is = connection.getInputStream()) {
				byte[] buffer = new byte[1024];
				int length;
				while (((length = is.read(buffer)) != -1)) {
					if (mustStop.get()) throw new InterruptedIOException("download cancelled");
					fos.write(buffer, 0, length);
					written += length;
				}
			}
			Log.i(TAG, String.format("Download from '%s' finished. Downloaded %d bytes", uri.toString(), written));
			return written;
		} catch (Exception e) {
			Log.e(TAG, "download error", e);
			Log.w(TAG, String.format("Stopped download from url '%s'. Downloaded %d bytes", uri.toString(), written));
			if (file.exists() && !file.delete()) {
				Log.w(TAG, "File could not be deleted: " + file.getAbsolutePath());
			}
			return -file.length();
		}
	}

	private boolean prepareEmptyFile(File file) {
		if (!file.exists()) {
			try {
				if (!file.createNewFile()) {
					Log.i(TAG, "prepareEmptyFile() File does not exist and could not be created: " + file.getAbsolutePath());
					return false;
				}
			} catch (IOException e) {
				Log.i(TAG, "prepareEmptyFile() Exception trying to create file: " + file.getAbsolutePath(), e);
				return false;
			}
		}

		if (file.length() > 0) {
			Log.d(TAG, "prepareEmptyFile() File exists and is not empty: " + file.getAbsolutePath());
			return false;
		}

		return file.canWrite();
	}

	/**
	 * Searches the given html code for img-Tags and filters out all src-attributes, beeing URLs to images.
	 *
	 * @param html the html code which is to be searched
	 * @return a set of URLs in their string representation
	 */
	private static Set<String> findAllImageUrls(String html) {
		Set<String> ret = new LinkedHashSet<>();
		if (html == null || html.length() < 10) return ret;

		int i = html.indexOf("<img");
		if (i == -1) return ret;

		// Filter out URLs without leading http, we cannot work with relative URLs (yet?).
		Matcher m = Utils.findImageUrlsPattern.matcher(html.substring(i, html.length()));

		while (m.find()) {
			String url = m.group(1);

			if (url.startsWith("http") || url.startsWith("ftp://")) ret.add(url);
		}
		return ret;
	}

}
