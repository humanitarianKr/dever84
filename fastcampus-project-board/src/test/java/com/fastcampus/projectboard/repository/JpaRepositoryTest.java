package com.fastcampus.projectboard.repository;

import com.fastcampus.projectboard.config.JpaConfig;

import com.fastcampus.projectboard.domain.Article;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@DisplayName("JPA연결테스트")
@Import(JpaConfig.class)
@DataJpaTest  // yaml에 설정을 따르지 않고 임의의 DB환경으로 테스트 한다. 설정을 사용하게 하려면 언노테이션(@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)) 추가 하면 되고 전역에서 쓰고 싶으면 test.database.replace yaml에 추가
class JpaRepositoryTest {
     private final ArticleRepository articleRepository;
     private final ArticleCommentRepository articleCommentRepository;

    public JpaRepositoryTest(@Autowired ArticleRepository articleRepository, @Autowired ArticleCommentRepository articleCommentRepository) {
        this.articleRepository = articleRepository;
        this.articleCommentRepository = articleCommentRepository;
    }

    @DisplayName("select 테스트")
    @Test
    void givenTestData_whenSelecting_thenWoksFine(){
         // Given

        // When
         List<Article> articles = articleRepository.findAll();
        // Then
        assertThat(articles).isNotNull().hasSize(123);
     }

    @DisplayName("insert 테스트")
    @Test
    void givenTestData_whenInserting_thenWoksFine(){
        // Given
        long previousCount = articleRepository.count(); //기존 카운트 갯수를 가져옴

        // When
        Article savedArticle = articleRepository.save(Article.of("new article", "new content", "#spring"));
        // Then
        assertThat(articleRepository.count()).isEqualTo(previousCount + 1);
    }
    @DisplayName("update 테스트")
    @Test
    void givenTestData_whenUpdateting_thenWoksFine(){
        // Given
        Article article =  articleRepository.findById(1L).orElseThrow();
        String updatedHashtag = "#springboot";
        article.setHashtag(updatedHashtag);

        // When
        Article savedArticle = articleRepository.saveAndFlush(article);
        //테스트시에는 롤백이 수행되기 때문에 flush처리 해줘야 변경점을 확인 할 수 있다. save 대신 saveAndFlush써도 무방 롤백되기 대문에 실제 반영은 안된다.
        //articleRepository.flush();
        // Then
        assertThat(savedArticle).hasFieldOrPropertyWithValue("hashtag", updatedHashtag);
    }

    @DisplayName("delete 테스트")
    @Test
    void givenTestData_whenDeleteting_thenWoksFine(){
        // Given
        Article article =  articleRepository.findById(1L).orElseThrow();
        long previousArticleCount = articleRepository.count();
        long previousArticleCommentCount = articleCommentRepository.count();
        int deletedCommentsSize = article.getArticleComments().size();

        String updatedHashtag = "#springboot";
        article.setHashtag(updatedHashtag);

        // When
        articleRepository.delete(article);
        //테스트시에는 롤백이 수행되기 때문에 flush처리 해줘야 변경점을 확인 할 수 있다. save 대신 saveAndFlush써도 무방 롤백되기 대문에 실제 반영은 안된다.
        //articleRepository.flush();
        // Then
        assertThat(articleRepository.count()).isEqualTo(previousArticleCount -1);
        assertThat(articleCommentRepository.count()).isEqualTo(previousArticleCount - deletedCommentsSize);
    }
}