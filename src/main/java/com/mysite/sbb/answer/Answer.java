package com.mysite.sbb.answer;

import com.mysite.sbb.question.Question;
import com.mysite.sbb.user.SiteUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@ToString
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(columnDefinition = "TEXT")
    private String content;
    private LocalDateTime createDate;
    private LocalDateTime modifyDate;
    @ManyToOne
    @ToString.Exclude
    private Question question;
    @ManyToOne
    private SiteUser author;
    @ManyToMany
    private Set<SiteUser> voters = new LinkedHashSet<>();

    public void addVoter(SiteUser voter){
        voters.add(voter);
    }
}