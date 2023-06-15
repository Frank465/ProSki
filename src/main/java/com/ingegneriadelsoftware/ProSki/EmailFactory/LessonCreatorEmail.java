package com.ingegneriadelsoftware.ProSki.EmailFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LessonCreatorEmail extends CreatorEmail {
    private final String instructoreName;
    private final String userName;
    private final String speciality;

    public BuildEmail createEmail() {
        return new BuildLessonEmail(instructoreName, userName, speciality);
    }
}
