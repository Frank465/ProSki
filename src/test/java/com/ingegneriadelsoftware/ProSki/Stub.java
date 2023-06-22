package com.ingegneriadelsoftware.ProSki;

import com.ingegneriadelsoftware.ProSki.DTO.Request.LocationRequest;
import com.ingegneriadelsoftware.ProSki.DTO.Response.LessonResponse;
import com.ingegneriadelsoftware.ProSki.Model.*;
import com.ingegneriadelsoftware.ProSki.Utils.Utils;
import jdk.jshell.execution.Util;

import java.util.ArrayList;
import java.util.List;

public class Stub {

    public static List<Lesson> getAllLessonsStub() {
        List<Lesson> lessonResponseList = new ArrayList<>();
        for (int i = 1; i < 10; i++) {
            lessonResponseList.add(
                    Lesson
                            .builder()
                            .id(i)
                            .instructor(getInstructorStub())
                            .startLesson(Utils.formatterDataTime("1" + i + "/12/2023 11:00"))
                            .endLesson(Utils.formatterDataTime("1" + i + "/12/2023 12:00"))
                            .build());
        }
        return lessonResponseList;
    }

    public static LocationRequest locationDTOStub() {
        LocationRequest request = new LocationRequest();
        request.setName("marileva");
        request.setPriceSubscription(35.50);
        request.setStartOfSeason("12/12/2023");
        request.setEndOfSeason("12/03/2024");
        request.setOpeningSkiLift("09:00");
        request.setClosingSkiLift("16:00");
        return request;
    }

    public static List<LessonResponse> getLessonsDTOStub() {
        List<LessonResponse> response = new ArrayList<>();
        response.add(LessonResponse.builder().idLesson(1).startLesson("12:00").endLesson("13:00").instructor("maestro1").build());
        response.add(LessonResponse.builder().idLesson(2).startLesson("12:00").endLesson("13:00").instructor("maestro2").build());
        response.add(LessonResponse.builder().idLesson(3).startLesson("12:00").endLesson("13:00").instructor("maestro3").build());
        response.add(LessonResponse.builder().idLesson(4).startLesson("12:00").endLesson("13:00").instructor("maestro4").build());
        return response;
    }

    public static User getUserStub() {
        User user = new User();
        user.setUserId(1);
        user.setName("francesco");
        user.setSurname("gigliotti");
        user.setPassword("Ciao45hy9");
        user.setGender(Gender.MAN);
        user.setDateBirth(Utils.formatterData("12/05/2001"));
        user.setEmail("francescogigliotti01@gmail.com");
        user.setRole(Role.USER);
        return user;
    }

    public static String getJwtStub_User() {
        return "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MTIzNDU2Nzg5LCJuYW1lIjoiSm9zZXBoIn0.OpOSSw7e485LOP5PrzScxHb7SR6sAOMRckfFwi4rp7o";
    }


    public static Lesson getLessonsStub() {
        Lesson lesson = new Lesson();
        lesson.setInstructor(getInstructorStub());
        lesson.setStartLesson(Utils.formatterDataTime("12/12/2023 12:00"));
        lesson.setEndLesson(Utils.formatterDataTime("12/12/2023 13:00"));
        return lesson;
    }

    public static Instructor getInstructorStub() {
        Instructor instructor = new Instructor();
        instructor.setEmail("maestro1@gmail.com");
        instructor.setName("maestro1");
        instructor.setSurname("_maestro1_");
        instructor.setLocation(getLocationStub());
        instructor.setSpeciality("sci");
        return instructor;
    }

    public static Location getLocationStub() {
        Location location = new Location();
        location.setName("location1");
        location.setPriceSubscription(35.00);
        location.setStartOfSeason(Utils.formatterData("12/12/2023"));
        location.setEndOfSeason(Utils.formatterData("12/03/2024"));
        location.setOpeningSkiLift(Utils.formatterTime("09:00"));
        location.setClosingSkiLift(Utils.formatterTime("16:00"));
        return location;
    }

    public static Plan getPlanStub() {
        Plan plan = new Plan();
        plan.setPlanId(1);
        plan.setName("Giovani");
        return plan;
    }
}
