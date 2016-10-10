package org.ttrssreader.gui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.ttrssreader.R;
import org.ttrssreader.controllers.Controller;
import org.ttrssreader.controllers.DBHelper;
import org.ttrssreader.gui.fragments.ArticleFragment;
import org.ttrssreader.gui.fragments.FeedHeadlineListFragment;
import org.ttrssreader.model.ScrollingArticleAdapter;
import org.ttrssreader.model.pojos.Article;
import org.ttrssreader.utils.ArticleCleaner;

public class ScrollingArticleActivity extends Activity {

	public static final String FEED_ID = "feedId";
	private int feedId = 0;

	private RecyclerView recyclerView;
	private RecyclerView.Adapter adapter;
	private RecyclerView.LayoutManager layoutManager;

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(FEED_ID, feedId);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle instance) {
		feedId = instance.getInt(FEED_ID);
		super.onRestoreInstanceState(instance);
	}

	@Override
	protected void onCreate(Bundle instance) {
		super.onCreate(instance);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			feedId = extras.getInt(FEED_ID, Integer.MIN_VALUE);
		} else if (instance != null) {
			feedId = instance.getInt(FEED_ID, Integer.MIN_VALUE);
		}

		setContentView(R.layout.scrolling_articles);
		recyclerView = (RecyclerView) findViewById(R.id.scrolling_articles);

		// use this setting to improve performance if you know that changes
		// in content do not change the layout size of the RecyclerView
		recyclerView.setHasFixedSize(true);

		// use a linear layout manager
		layoutManager = new LinearLayoutManager(this);


		recyclerView.setLayoutManager(layoutManager);
		ArticleCleaner cleaner = new ArticleCleaner(this,
									Controller.getInstance().alignFlushLeft(),
									getString(Controller.getInstance().getThemeHTML()),
									Controller.getInstance().hyphenationLanguage(),
									Controller.getInstance().allowHyphenation(),
									Controller.getInstance().cacheFolder()
									);
		adapter = new ScrollingArticleAdapter(DBHelper.getInstance().getArticleIds(feedId), cleaner, this);
		recyclerView.setAdapter(adapter);
	}

}
