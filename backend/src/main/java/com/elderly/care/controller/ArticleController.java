package com.elderly.care.controller;
import com.elderly.care.common.Result;
import com.elderly.care.entity.Article;
import com.elderly.care.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/article")
public class ArticleController {
    @Autowired private ArticleService articleService;
    @GetMapping("/list")
    public Result<?> list() { return Result.success(articleService.list()); }
    @PostMapping("/add")
    public Result<?> add(@RequestBody Article article) {
        articleService.save(article);
        return Result.success();
    }
    
    @PostMapping("/update")
    public Result<?> update(@RequestBody Article article) {
        articleService.updateById(article);
        return Result.success();
    }
    
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        articleService.removeById(id);
        return Result.success();
    }
}
