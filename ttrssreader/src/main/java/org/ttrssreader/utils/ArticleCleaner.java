package org.ttrssreader.utils;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.stringtemplate.v4.ST;
import org.ttrssreader.R;
import org.ttrssreader.controllers.Controller;
import org.ttrssreader.model.pojos.Article;
import org.ttrssreader.model.pojos.Feed;
import org.ttrssreader.model.pojos.Label;
import org.ttrssreader.preferences.Constants;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

public class ArticleCleaner {

	private final String htmlTemplate;
	private final String labelColorTemplate;
	private final String style;
	private final String theme;
	private final String bottomNavigationTemplate;
	private final String noteTemplate;
	private final String notePrefix;
	private final String language;
	private final boolean hyphenate;
	private final String hyphenation;
	private final String cachefolder;
	private final String attachmentImagesTemplate;
	private final String attachmentMediaTemplate;
	private final String mediaPlay;
	private final String mediaDisplayLink;

	public ArticleCleaner(Context context, boolean alignLeft, String theme, String language, boolean hyphenate, String cachefolder) {
		this.htmlTemplate = context.getString(R.string.HTML_TEMPLATE_SCROLLABLE);
		this.labelColorTemplate = context.getString(R.string.LABELCOLOR_TEMPLATE);
		this.style = new ST(context.getString(R.string.STYLE_TEMPLATE_SCROLLABLE), '$', '$')
						.add("TEXT_ALIGN", context.getString(alignLeft ? R.string.ALIGN_LEFT : R.string.ALIGN_JUSTIFY))
						.render();
		this.theme = theme;
		this.bottomNavigationTemplate = context.getString(R.string.BOTTOM_NAVIGATION_TEMPLATE);
		this.noteTemplate = context.getString(R.string.NOTE_TEMPLATE);
		this.notePrefix = context.getString(R.string.Commons_HtmlPrefixNote);
		this.language = language;
		this.hyphenate = hyphenate;
		this.hyphenation = new ST(context.getString(R.string.JAVASCRIPT_HYPHENATION_TEMPLATE), '$', '$')
							.add("LANGUAGE", language)
							.render();
		this.cachefolder = cachefolder;
		this.attachmentImagesTemplate = context.getString(R.string.ATTACHMENT_IMAGES_TEMPLATE);
		this.attachmentMediaTemplate = context.getString(R.string.ATTACHMENT_MEDIA_TEMPLATE);
		this.mediaPlay = context.getString(R.string.ArticleActivity_MediaPlay);
		this.mediaDisplayLink = context.getString(R.string.ArticleActivity_MediaDisplayLink);
	}

	public String clean(Feed feed, Article article, String cachedImages) {

		// Load html from Controller and insert content// Article-Prefetch-Stuff from Raw-Ressources and System
		ST htmlTmpl = new ST(htmlTemplate, '$', '$');

		// Hyphenation Javascript
		if (hyphenate) {
			htmlTmpl.add("HYPHENATION", hyphenation);
		}

		// Replace alignment-marker: align:left or align:justify
		htmlTmpl.add("STYLE", style);

		// General values
		htmlTmpl.add("THEME", theme);
		htmlTmpl.add("CACHE_DIR", cachefolder);
		htmlTmpl.add("LANGUAGE", language);

		// Special values for this article
		htmlTmpl.add("article", article);
		htmlTmpl.add("feed", feed);
		htmlTmpl.add("CACHED_IMAGES", cachedImages);
		htmlTmpl.add("LABELS", cleanLabels(article.labels));
	//	htmlTmpl.add("UPDATED", DateUtils.getDateTimeCustom(getActivity(), article.updated));
		htmlTmpl.add("ATTACHMENTS", getAttachmentsMarkup(article.attachments));
		htmlTmpl.add("CONTENT", cleanContent(article.content));

		// Navigation buttons
		if (Controller.getInstance().showButtonsMode() == Constants.SHOW_BUTTONS_MODE_HTML) {
			htmlTmpl.add("NAVIGATION", bottomNavigationTemplate);
		}

		// Note of the article
		if (article.note != null && article.note.length() > 0) {
			String note_template = new ST(noteTemplate, '$', '$')
									.add("NOTE", notePrefix + " " + article.note)
									.render();
			htmlTmpl.add("NOTE_TEMPLATE", note_template);
		}

		return htmlTmpl.render();
	}

	// Remove all html tags and content that doesn't meet this set of allowed stuff
	private String cleanContent(String content) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			return content;
		}
		return Jsoup.clean(content, Whitelist.relaxed());
	}

	private String cleanLabels(Collection<Label> labels) {
		if (labels == null || labels.isEmpty()) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (Label label : labels) {
			if (label.checked) {
				if (sb.length() > 0) {
					sb.append(", ");
				}

				String labelString = label.caption;
				if (label.foregroundColor != null && label.backgroundColor != null)
					labelString = String
							.format(labelColorTemplate, label.foregroundColor, label.backgroundColor, label.caption);
				sb.append(labelString);
			}
		}
		return sb.toString();
	}

	private String formatDate(Date date) {
		return "TODO";
	}

	/**
	 * generate HTML code for attachments to be shown inside article
	 *
	 * @param attachments collection of attachment URLs
	 */
	private String getAttachmentsMarkup(Set<String> attachments) {
		StringBuilder content = new StringBuilder();
		Map<String, Collection<String>> attachmentsByMimeType = FileUtils.groupFilesByMimeType(attachments);

		if (attachmentsByMimeType.isEmpty()) return "";

		for (String mimeType : attachmentsByMimeType.keySet()) {
			Collection<String> mimeTypeUrls = attachmentsByMimeType.get(mimeType);
			if (mimeTypeUrls.isEmpty()) return "";

			if (mimeType.equals(FileUtils.IMAGE_MIME)) {
				ST st = new ST(attachmentImagesTemplate);
				st.add("items", mimeTypeUrls);
				content.append(st.render());
			} else {
				ST st = new ST(attachmentMediaTemplate);
				st.add("items", mimeTypeUrls);
				String linkText = mimeType.equals(FileUtils.AUDIO_MIME) || mimeType.equals(FileUtils.VIDEO_MIME)
						? mediaPlay : mediaDisplayLink;
				st.add("linkText", linkText);
				content.append(st.render());
			}
		}

		return content.toString();
	}

}
