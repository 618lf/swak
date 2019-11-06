package com.swak.wechat.message.plain;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class News implements Serializable {
	private static final long serialVersionUID = 1L;
	public List<Article> articles;

	public News(List<Article> articles) {
		this.articles = articles;
	}

	public News(Article... article) {
		this(Arrays.asList(article));
	}

	public List<Article> getArticles() {
		return articles;
	}

	public void setArticles(List<Article> articles) {
		this.articles = articles;
	}
}
