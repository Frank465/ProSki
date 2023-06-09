package com.ingegneriadelsoftware.ProSki.ForumStrategy;

import com.ingegneriadelsoftware.ProSki.Model.User;

public interface PublishingStrategy {

    String publishingMessage(String emailMessenger, User user, String message);

    String publishingComment(Integer idMessenger, User user, String message);
}
