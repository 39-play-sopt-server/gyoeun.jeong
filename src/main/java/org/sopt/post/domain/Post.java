package org.sopt.post.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.concurrent.atomic.AtomicLong;

@Entity
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String title;
    private String content;
    private String author;
    private PostCategory category;
    private Long viewCount = 0L;


    protected Post() {}

    public Post(String title, String content, String author, PostCategory category) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.category = category;
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

    public long getViewCount() { return viewCount; }

    public void increaseViewCount() { viewCount++; }

    public long getId() {
        return id;
    }

    public void update(String title, String content, PostCategory category) {
        this.title = title;
        this.content = content;
        this.category = category;
    }
}