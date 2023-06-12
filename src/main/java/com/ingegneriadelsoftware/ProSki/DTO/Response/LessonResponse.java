package com.ingegneriadelsoftware.ProSki.DTO.Response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LessonResponse {
    private Integer idLesson;
    private String instructor;
    private String startLesson;
    private String endLesson;
}
