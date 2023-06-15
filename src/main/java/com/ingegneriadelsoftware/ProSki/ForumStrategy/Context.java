package com.ingegneriadelsoftware.ProSki.ForumStrategy;

import com.ingegneriadelsoftware.ProSki.Model.User;
import org.springframework.stereotype.Component;

@Component
public class Context {

    private PublishingStrategy publishingStrategy;

    public void setPublishingStrategy(PublishingStrategy publishingStrategy) {
        this.publishingStrategy = publishingStrategy;
    }

    public String executeStrategy(String email, User user, String message) {
        return publishingStrategy.publishingMessage(email, user, message);
    }
}
