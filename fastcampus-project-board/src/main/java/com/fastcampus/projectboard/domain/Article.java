package com.fastcampus.projectboard.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@ToString
@Table(indexes = {
        @Index(columnList = "title"),
        @Index(columnList = "hashtag"),
        @Index(columnList = "createdAt"),
        @Index(columnList = "title"),
        @Index(columnList= "createdBy")
})
@EntityListeners(AuditingEntityListener.class)
@Entity
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter @Column(nullable = false) private String title;  // 제목
    @Setter @Column(nullable = false, length  = 10000) private String content; //본문

    @Setter private String hashtag; // 해시태그

    @OrderBy("id")
    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL)
    @ToString.Exclude
    private final Set<ArticleComment> articleComments = new LinkedHashSet<>();
    //관계 설정

    @CreatedDate @Column(nullable = false) private LocalDateTime createdAt; // 생성일시
    @CreatedBy @Column(nullable = false, length = 100) private String createdBy; // 생성자
    @CreatedDate @Column(nullable = false) private LocalDateTime modifiedAt; // 수정일시
    @CreatedBy @Column(nullable = false, length = 100) private String modifiedBy; // 수정자

    protected Article() {}

    private Article(String title, String content, String hashtag) {
        this.title = title;
        this.content = content;
        this.hashtag = hashtag;
    }

    public static Article of(String title, String content, String hashtag) {
        return new Article(title, content, hashtag);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Article article = (Article) o;
        return Objects.equals(id, article.id);
        // java14에서 소개된 pattern matching 개념 확인
        /** 엔티티를 데이터베이스에 영속화 시키고 연결을 하고 사용하는 환경에서 서로 다른 두 로우가 같은 조건이 무엇인가 하는 질문에 대한 로직
         * 아래는 수기 작성 위는 인텔리제이 자동 생성 로직임 오류 발생시 아래것으로 대체 해야 한다.
         *  if (this == o) return true;
         *  if(!(o instanceof Article article)) return false;
         *  return id != null && id.equals(article.id);
         */
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
