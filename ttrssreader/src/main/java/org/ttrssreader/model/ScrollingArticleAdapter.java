package org.ttrssreader.model;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orhanobut.logger.Logger;

import org.ttrssreader.R;
import org.ttrssreader.controllers.Controller;
import org.ttrssreader.controllers.DBHelper;
import org.ttrssreader.model.pojos.Article;
import org.ttrssreader.utils.ArticleCleaner;

public class ScrollingArticleAdapter extends RecyclerView.Adapter<ScrollingArticleAdapter.ViewHolder> {

	private static final String TAG = ScrollingArticleAdapter.class.getSimpleName();

	private final int[] dataset;
	private final ArticleCleaner cleaner;
	private final Context context;

	public class ViewHolder extends RecyclerView.ViewHolder {

		public TextView title;
		public WebView content;

		public ViewHolder(RelativeLayout layout) {
			super(layout);
			this.title = (TextView) layout.findViewById(R.id.scrolled_article_title);
			this.content = (WebView) layout.findViewById(R.id.scrolled_article_content);
		}

		public void bindArticle(final Article article) {
			this.title.setText(article.title);
			this.title.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(article.url));
					context.startActivity(intent);
				}
			});
//			this.title.on();
			String content = cleaner.clean(null, article, "");

			this.content.getSettings().setJavaScriptEnabled(true);
			this.content.getSettings().setDomStorageEnabled(true);
//			this.content.addJavascriptInterface(articleJSInterface, "articleController");
			Logger.t(TAG).d(article.content);
			this.content.loadDataWithBaseURL("file:///android_asset/", content, "text/html", "utf-8", null);

//			if (!linkAutoOpened && article.content.length() < 3) {
//				if (Controller.getInstance().openUrlEmptyArticle()) {
//					Log.i(TAG, "Article-Content is empty, opening URL in browser");
//					linkAutoOpened = true;
//					openLink();
//				}
//			}

		}

	}

	public ScrollingArticleAdapter(int[] articleIds, ArticleCleaner cleaner, Context context) {
		this.dataset = articleIds;
		this.cleaner = cleaner;
		this.context = context;
	}

	@Override
	public ScrollingArticleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		RelativeLayout rl = (RelativeLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.scrolled_article, parent, false);
		return new ViewHolder(rl);
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		Article article = DBHelper.getInstance().getArticle(dataset[position]);
		holder.bindArticle(article);
	}

	@Override
	public int getItemCount() {
		return dataset.length;
	}

}
