package org.sopt.post.domain;

import org.sopt.common.entity.BaseEntity;
import org.sopt.member.domain.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Post extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String title;
    private String content;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_post_member"))
    private Member author;

    private PostCategory category;
    private Long viewCount = 0L;

    protected Post() {}

    public Post(String title, String content, Member author, PostCategory category) {
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

    public Member getAuthor() {
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
