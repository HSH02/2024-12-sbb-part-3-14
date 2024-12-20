package com.mysite.sbb.controller;

import com.mysite.sbb.model.answer.dto.AnswerRequestDTO;
import com.mysite.sbb.model.question.dto.QuestionRequestDTO;
import com.mysite.sbb.model.question.entity.Question;
import com.mysite.sbb.model.question.dto.QuestionDetailResponseDTO;
import com.mysite.sbb.service.impl.QuestionServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

@RequestMapping("/question")
@RequiredArgsConstructor
@Controller
public class QuestionController {

    private final QuestionServiceImpl questionServiceImpl;

    @GetMapping("/list")
    public String list(Model model,
                       @RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "kw", defaultValue = "") String kw) {
        model.addAttribute("paging", questionServiceImpl.getList(page, kw));
        model.addAttribute("kw", kw);
        return "question_list";
    }

    @GetMapping(value = "/detail/{id}")
    public String detail(Model model,
                         @PathVariable("id") Integer id,
                         @RequestParam(value = "page", defaultValue = "0") int page,
                         @RequestParam(value = "sortKeyword", defaultValue = "createDate") String sortKeyword) {

        QuestionDetailResponseDTO question = this.questionServiceImpl.getQuestionDetail(id, page, sortKeyword);

        model.addAttribute("question", question);
        model.addAttribute("sort", sortKeyword); // 선택된 정렬 기준 전달
        model.addAttribute("answerRequestDTO", new AnswerRequestDTO()); // Form 초기화
        return "question_detail";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    public String questionCreate(@ModelAttribute QuestionRequestDTO questionRequestDTO) {
        return "question_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String questionCreate(@Valid QuestionRequestDTO questionRequestDTO, BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasErrors()) {
            return "question_form";
        }
        questionServiceImpl.create(questionRequestDTO, principal.getName());
        return "redirect:/question/list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String questionModify(QuestionRequestDTO questionRequestDTO, @PathVariable("id") Integer id, Principal principal) {

        Question question = questionServiceImpl.getQuestion(id);

        if (!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }

        questionRequestDTO.setSubject(question.getSubject());
        questionRequestDTO.setContent(question.getContent());
        return "question_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String questionModify(@Valid QuestionRequestDTO questionRequestDTO, BindingResult bindingResult,
                                 Principal principal, @PathVariable("id") Integer id) {
        if (bindingResult.hasErrors()) {
            return "question_form";
        }
        questionServiceImpl.modify(id, questionRequestDTO, principal.getName());
        return String.format("redirect:/question/detail/%s", id);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String questionDelete(@PathVariable("id") Integer id, Principal principal) {
        this.questionServiceImpl.delete(id, principal.getName());
        return "redirect:/";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/{id}")
    public String questionVote(@PathVariable("id") Integer id, Principal principal) {
        this.questionServiceImpl.vote(id, principal.getName());
        return String.format("redirect:/question/detail/%s", id);
    }

}
