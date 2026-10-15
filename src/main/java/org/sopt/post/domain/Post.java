package org.sopt.post.domain;

import java.util.concurrent.atomic.AtomicLong;

public class Post {
    private String title;
    private String content;
    private final String author;
    private PostCategory category;
    private final AtomicLong viewCount = new AtomicLong(0);
    private final long id;

    public Post(String title, String content, String author, PostCategory category, long id) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.category = category;
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public String getContent() {
        return this.content;
    }

    public String getAuthor() {
        return author;
    }

    public PostCategory getCategory() {
        return category;
    }

    public long getViewCount() {
        return viewCount.get();
    }

    public void increaseViewCount() {
        viewCount.incrementAndGet();
    }

    public long getId() {
        return id;
    }

    public void update(String title, String content, PostCategory category) {
        this.title = title;
        this.content = content;
        this.category = category;
    }
}