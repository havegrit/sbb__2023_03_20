package com.mysite.sbb.question;

import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.user.SiteUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@ToString
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(length = 200)
    private String subject;
    @Column(columnDefinition = "TEXT")
    private String content;
    private LocalDateTime createDate;
    private LocalDateTime modifyDate;

    // OneToMany 자바세상에서의 편의를 위해서 필드 생성
    // 이 녀석은 실제 DB 테이블에 칼럼이 생성되지 않는다.
    // DB는 배열이나 리스트를 저장할 수 없다.
    // 칼럼에 저장할 수 없다.
    // 만들어도 되고 안 만들어도 됩니다.
    // 다만 만들면 해당 객체(질문객체)에서 관련된 답변들을 찾을 때 편합니다.
    @OneToMany(mappedBy = "question", cascade = CascadeType.REMOVE)
    // OneToMany 에는 직접객체초기화
    @LazyCollection(LazyCollectionOption.EXTRA)
    private List<Answer> answerList = new ArrayList<>();
    @ManyToOne
    private SiteUser author;
    private Integer views;
    private String category;

    @ManyToMany
    private Set<SiteUser> voters = new LinkedHashSet<>();

    public void addAnswer(Answer a) {
        a.setQuestion(this);
        answerList.add(a);
    }

    public void addVoter(SiteUser voter){
        voters.add(voter);
    }
}