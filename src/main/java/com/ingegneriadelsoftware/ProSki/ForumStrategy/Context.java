package com.ingegneriadelsoftware.ProSki.ForumStrategy;

import com.ingegneriadelsoftware.ProSki.Model.User;
import org.springframework.stereotype.Component;

@Component
public class Context {

    private PublishingStrategy publishingStrategy;

    public void setPublishingStrategy(PublishingStrategy publishingStrategy) {
        this.publishingStrategy = publishingStrategy;
    }

    public String executeMessageStrategy(String email, User user, String message) {
        return publishingStrategy.publishingMessage(email, user, message);
    }

    public String executeCommentStrategy(Integer idMessege, User user, String comment) {
        return publishingStrategy.publishingComment(idMessege, user, comment);
    }
}
