package com.ingegneriadelsoftware.ProSki.DTO.Request;

import com.ingegneriadelsoftware.ProSki.Utils.Utils;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
public class LessonRequest {

    @NotNull
    @NotEmpty
    @Pattern(regexp = Utils.REGEX_EMAIL, message = Utils.ERROR_EMAIL)
    private String instructorEmail;

    @NotNull
    @NotEmpty
    @Pattern(regexp = Utils.LOCALDATETIME, message = Utils.ERROR_LOCALDATETIME)
    private String startLesson;

    @NotNull
    @NotEmpty
    @Pattern(regexp = Utils.LOCALDATETIME, message = Utils.ERROR_LOCALDATETIME)
    private String endLesson;

}
