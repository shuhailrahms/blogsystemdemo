package com.shuhail.blogdemo.controller;

import com.shuhail.blogdemo.domain.Article;
import com.shuhail.blogdemo.domain.User;
import com.shuhail.blogdemo.exception.NotFoundException;
import com.shuhail.blogdemo.service.ArticleService;
import com.shuhail.blogdemo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/article")
public class ArticleController {
    private final ArticleService articleService;
    private final UserService userService;

    @Autowired
    public ArticleController(ArticleService articleService, UserService userService) {
        this.articleService = articleService;
        this.userService = userService;
    }

    @GetMapping
    public String index(Model model, @AuthenticationPrincipal UserDetails userDetails,
                        @RequestParam(required = false, value = "q") String q,
                        @RequestParam(required = false, value = "page") Integer page,
                        @RequestParam(required = false, value = "size") Integer size) {

        page = (page == null ) ? 0 : page;
        size = (size == null ) ? 20 : size;

        if (q == null) {
            model.addAttribute("articles", articleService.getAll(PageRequest.of(page, size)));
        } else {
            model.addAttribute("articles", articleService.search(q, PageRequest.of(page, size)));
        }

        return "article/index";
    }

    @GetMapping("/show/{link}")
    public String getPost(@AuthenticationPrincipal UserDetails userDetails,
                          @PathVariable String link, Model model) {
        Optional<Article> article = articleService.getByLink(link);
        if (article.isPresent()) {
            model.addAttribute("article", article.get());
        } else {
            throwNotFoundException(link);
        }

        return "article/show";
    }

    @GetMapping("/new")
    public String newPost() {
        return "article/create";
    }

    @GetMapping("/edit/{id}")
    public String editPost(@AuthenticationPrincipal UserDetails userDetails,
                           @PathVariable String id, Model model) {
        Optional<Article> article = articleService.getById(id);
        if (article.isPresent())
            model.addAttribute("article", article.get());
        else
            throwNotFoundException(id);

        return "article/create";
    }

    private String throwNotFoundException(@PathVariable String id) {
        throw new NotFoundException("Article Not Found for " + id);
    }

    @PostMapping("/delete/{id}")
    public String deletePost(@AuthenticationPrincipal UserDetails userDetails, @PathVariable String id, Model model) {
        articleService.deleteById(id);

        model.addAttribute("message", "Article with id " + id + " deleted successfully!");
        model.addAttribute("articles", articleService.getAll(PageRequest.of(0, 10)));

        return "article/index";
    }

    @PostMapping
    public String savePost(@AuthenticationPrincipal UserDetails userDetails, Article article, Model model) {
        if (article.getId() == null || article.getId().length() == 0){
            User user = userService.getByUsername(userDetails.getUsername());
            article.setAuthor(user);
        } else {
            Optional<Article> optionalArticle = articleService.getById(article.getId());
            if (optionalArticle.isPresent())
                article.setAuthor(optionalArticle.get().getAuthor());
        }
        articleService.save(article);

        return "redirect:/article/show/" + article.getLink();
    }
}