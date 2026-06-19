package com.elderly.care.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.elderly.care.entity.Article;
import com.elderly.care.mapper.ArticleMapper;
import com.elderly.care.websocket.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ArticlePushTask {
    private static final Logger log = LoggerFactory.getLogger(ArticlePushTask.class);

    @Autowired
    private ArticleMapper articleMapper;

    // Run every 10 seconds
    @Scheduled(fixedRate = 10000)
    public void pushScheduledArticles() {
        QueryWrapper<Article> wrapper = new QueryWrapper<>();
        wrapper.le("schedule_time", LocalDateTime.now())
               .eq("is_pushed", false);

        List<Article> articles = articleMapper.selectList(wrapper);
        for (Article article : articles) {
            log.info("Pushing article: {}", article.getTitle());
            // Send WebSocket notification
            String msg = "NEW_ARTICLE:" + article.getId() + ":" + article.getTitle();
            if ("ALL".equals(article.getTargetRole())) {
                WebSocketServer.broadcastMessage(msg);
            } else {
                WebSocketServer.sendMessageToRole(article.getTargetRole(), msg);
            }
            // Mark as pushed
            article.setIsPushed(true);
            articleMapper.updateById(article);
        }
    }
}
