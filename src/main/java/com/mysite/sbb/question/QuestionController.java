package com.mysite.sbb.question;

import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.answer.AnswerForm;
import com.mysite.sbb.answer.AnswerService;
import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

@RequestMapping("/question")
@Controller
@RequiredArgsConstructor
//@Validated 생략해줘야함. 스프링 자체적으로 처리하는 로직이 있기 때문에, 어노테이션 설정을 사용자화 하고 싶은 것이 아니라면 생략하는 것이 좋다.
public class QuestionController {
    private final QuestionService questionService;
    private final AnswerService answerService;
    private final UserService userService;

    @GetMapping("/list")
    public String list(){
        return "redirect:/question/list/qna";
    }
    @GetMapping("/list/qna")
    public String qnaList(Model model,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "") String kw){
        Page<Question> paging = questionService.getList("qna", page, kw);
        model.addAttribute("paging", paging);
        return "question_list";
    }
    @GetMapping("/list/free")
    public String freeList(Model model,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "") String kw){
        Page<Question> paging = questionService.getList("free", page, kw);
        model.addAttribute("paging", paging);
        return "question_list";
    }

    @GetMapping(value = "/detail/{id}")
    public String detail(Model model, @PathVariable("id") Integer id, AnswerForm answerForm
            , @RequestParam(defaultValue = "0") int page
            , @RequestParam(defaultValue = "createDate") String so) {
        Question question = questionService.getQuestion(id);
        Page<Answer> paging = answerService.getList(question, page, so);
        questionService.increaseView(question);
        List<Answer> answerList = answerService.getAnswerByQuestionId(id);
        model.addAttribute("question", question);
        model.addAttribute("answers", answerList);
        model.addAttribute("paging", paging);
        return "question_detail";
    }
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create/qna")
    public String qnaQuestionCreate(QuestionForm questionFrom) {
        return "qna_question_form";
    }
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create/qna")
    public String qnaQuestionCreate(@Valid QuestionForm questionFrom, BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasErrors()) {
            return "qna_question_form";
        }
        SiteUser siteUser = userService.getUser(principal.getName());
        questionService.create("qna", questionFrom.getSubject(), questionFrom.getContent(), siteUser);
        return "redirect:/question/list";
    }
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create/free")
    public String freeQuestionCreate(QuestionForm questionFrom) {
        return "free_question_form";
    }
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create/free")
    public String freeQuestionCreate(@Valid QuestionForm questionFrom, BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasErrors()) {
            return "free_question_form";
        }
        SiteUser siteUser = userService.getUser(principal.getName());
        questionService.create("free", questionFrom.getSubject(), questionFrom.getContent(), siteUser);
        return "redirect:/question/list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String questionModify(QuestionForm questionForm, @PathVariable("id") Integer id, Principal principal) {
        Question question = questionService.getQuestion(id);
        if(!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        questionForm.setSubject(question.getSubject());
        questionForm.setContent(question.getContent());
        return "question_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String questionModify(@Valid QuestionForm questionForm, BindingResult bindingResult,
                                 Principal principal, @PathVariable("id") Integer id) {
        if (bindingResult.hasErrors()) {
            return "question_form";
        }

        Question question = questionService.getQuestion(id);

        if (!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }

        questionService.modify(question, questionForm.getSubject(), questionForm.getContent());

        return "redirect:/question/detail/%d".formatted(id);
    }


    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String questionDelete(Principal principal, @PathVariable("id") Integer id) {
        Question question = questionService.getQuestion(id);
        if (!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }
        questionService.delete(question);
        return "redirect:/";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/{id}")
    public String questionVote(Principal principal, @PathVariable("id") Integer id) {
        Question question = questionService.getQuestion(id);
        SiteUser siteUser = userService.getUser(principal.getName());
        questionService.vote(question, siteUser);
        return "redirect:/question/detail/%d".formatted(id);
    }
}
