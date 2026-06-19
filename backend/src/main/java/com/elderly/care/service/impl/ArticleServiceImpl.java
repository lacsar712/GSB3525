package com.elderly.care.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.elderly.care.entity.Article;
import com.elderly.care.mapper.ArticleMapper;
import com.elderly.care.service.ArticleService;
import org.springframework.stereotype.Service;

@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {}
