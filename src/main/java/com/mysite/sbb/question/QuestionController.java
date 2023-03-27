package com.mysite.sbb.question;

import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.answer.AnswerForm;
import com.mysite.sbb.answer.AnswerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/question")
@Controller
@RequiredArgsConstructor
//@Validated 생략해줘야함. 스프링 자체적으로 처리하는 로직이 있기 때문에, 어노테이션 설정을 사용자화 하고 싶은 것이 아니라면 생략하는 것이 좋다.
public class QuestionController {
    private final QuestionService questionService;
    private final AnswerService answerService;

    @GetMapping("/list")
    public String list(Model model, @RequestParam(value = "page", defaultValue = "0") int page){
        Page<Question> paging = questionService.getList(page);
        model.addAttribute("paging", paging);
        return "question_list";
    }

    @GetMapping(value = "/detail/{id}")
    public String detail(Model model, @PathVariable("id") Integer id, AnswerForm answerForm) {
        Question question = questionService.getQuestion(id);
        List<Answer> answerList = answerService.getAnswerByQuestionId(id);
        model.addAttribute("question", question);
        model.addAttribute("answers", answerList);
        return "question_detail";
    }

    @GetMapping("/create")
    public String questionCreate(QuestionForm questionFrom) {
        return "question_form";
    }

    @PostMapping("/create")
    public String questionCreate(@Valid QuestionForm questionFrom, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "question_form";
        }
        questionService.create(questionFrom.getSubject(), questionFrom.getContent());
        return "redirect:/question/list";
    }
}
