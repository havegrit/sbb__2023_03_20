package com.mysite.sbb.answer;

import com.mysite.sbb.user.SiteUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
public interface AnswerRepository extends JpaRepository<Answer, Integer> {
    @Modifying
    @Transactional
    @Query(value = "ALTER TABLE answer AUTO_INCREMENT = 1", nativeQuery = true)
    void clearAutoIncrement();

    List<Answer> findByQuestionId(Integer id);

    List<Answer> findByAuthor(SiteUser user);

    Page<Answer> findByQuestionId(Integer id, Pageable pageable);
}
