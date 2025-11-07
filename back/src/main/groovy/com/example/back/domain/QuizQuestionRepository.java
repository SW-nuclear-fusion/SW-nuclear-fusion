// QuizQuestionRepository.java
package com.example.back.domain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, Long> {
    // DB에서 랜덤으로 5개 질문 가져오기 (H2 DB 기준, MySQL은 RAND())
<<<<<<< HEAD
    @Query(value = "SELECT * FROM quiz_questions ORDER BY RAND() LIMIT 5", nativeQuery = true)
=======
    @Query(value = "SELECT * FROM quiz_questions ORDER BY RANDOM() LIMIT 5", nativeQuery = true)
>>>>>>> cad27c24f4c0e36f3b2cf20dbc2e1a6ebc5e11dd
    List<QuizQuestion> findRandom5Questions();
}