package org.ttrssreader.net;

import java.util.HashMap;
import java.util.Map;

public class RequestFactory {

	public static Map<String, String> newGetApiLevelRequest() {
		Map<String, String> map = new HashMap<>();
		map.put("op", "getApiLevel");
		return map;
	}

	public static Map<String, String> newLoginRequest() {
		Map<String, String> map = new HashMap<>();
		map.put("op", "login");
		return map;
	}

	public static Map<String, String> newLogoutRequest() {
		Map<String, String> map = new HashMap<>();
		map.put("op", "logout");
		return map;
	}

	public static Map<String, String> newIsLoggedInRequest() {
		Map<String, String> map = new HashMap<>();
		map.put("op", "isLoggedIn");
		return map;
	}

	public static Map<String, String> newGetUnreadRequest() {
		Map<String, String> map = new HashMap<>();
		map.put("op", "getUnread");
		return map;
	}

	public static Map<String, String> newGetCountersRequest() {
		Map<String, String> map = new HashMap<>();
		map.put("op", "getCounters");
		return map;
	}

	public static Map<String, String> newGetFeedsRequest() {
		Map<String, String> map = new HashMap<>();
		map.put("op", "getFeeds");
		return map;
	}

	public static Map<String, String> newGetCategoriesRequest() {
		Map<String, String> map = new HashMap<>();
		map.put("op", "getCategories");
		return map;
	}

	public static Map<String, String> newGetHeadlinesRequest() {
		Map<String, String> map = new HashMap<>();
		map.put("op", "getHeadlines");
		return map;
	}

	public static Map<String, String> newUpdateArticleRequest() {
		Map<String, String> map = new HashMap<>();
		map.put("op", "updateArticle");
		return map;
	}

	public static Map<String, String> newGetArticleRequest() {
		Map<String, String> map = new HashMap<>();
		map.put("op", "getArticle");
		return map;
	}

	public static Map<String, String> newGetConfigRequest() {
		Map<String, String> map = new HashMap<>();
		map.put("op", "getConfig");
		return map;
	}

	public static Map<String, String> newUpdateFeedRequest() {
		Map<String, String> map = new HashMap<>();
		map.put("op", "updateFeed");
		return map;

	}

	public static Map<String, String> newGetPrefRequest() {
		Map<String, String> map = new HashMap<>();
		map.put("op", "getPref");
		return map;

	}

	public static Map<String, String> newCatchupFeedRequest() {
		Map<String, String> map = new HashMap<>();
		map.put("op", "catchupFeed");
		return map;
	}

	public static Map<String, String> newGetLabelsRequest() {
		Map<String, String> map = new HashMap<>();
		map.put("op", "getLabels");
		return map;
	}

	public static Map<String, String> newSetArticleLabelRequest() {
		Map<String, String> map = new HashMap<>();
		map.put("op", "setArticleLabel");
		return map;
	}

	public static Map<String, String> newShareToPublishedRequest() {
		Map<String, String> map = new HashMap<>();
		map.put("op", "shareToPublished");
		return map;
	}

	public static Map<String, String> newSubscribeToFeedRequest() {
		Map<String, String> map = new HashMap<>();
		map.put("op", "subscribeToFeed");
		return map;
	}

	public static Map<String, String> newUnsubscribeFeedRequest() {
		Map<String, String> map = new HashMap<>();
		map.put("op", "unsubscribeFeed");
		return map;
	}

	public static Map<String, String> newGetFeedTreeRequest() {
		Map<String, String> map = new HashMap<>();
		map.put("op", "getFeedTree");
		return map;
	}

}
