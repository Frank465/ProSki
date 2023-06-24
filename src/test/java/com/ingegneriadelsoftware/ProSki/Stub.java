package com.ingegneriadelsoftware.ProSki;

import com.ingegneriadelsoftware.ProSki.DTO.Request.*;
import com.ingegneriadelsoftware.ProSki.DTO.Response.LessonResponse;
import com.ingegneriadelsoftware.ProSki.DTO.Response.LocationResponse;
import com.ingegneriadelsoftware.ProSki.DTO.Response.MessageResponse;
import com.ingegneriadelsoftware.ProSki.DTO.Utils.CommentDTO;
import com.ingegneriadelsoftware.ProSki.DTO.Utils.MessageDTO;
import com.ingegneriadelsoftware.ProSki.DTO.Utils.SkiDTO;
import com.ingegneriadelsoftware.ProSki.DTO.Utils.SnowboardDTO;
import com.ingegneriadelsoftware.ProSki.Model.*;
import com.ingegneriadelsoftware.ProSki.Utils.Utils;
import jdk.jshell.execution.Util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

public class Stub {

    public static VendorEquipmentRequest getVendorEquipmentRequestStub() {
        VendorEquipmentRequest vendorEquipmentRequest = new VendorEquipmentRequest();
        vendorEquipmentRequest.setVendorEmail(getVendorStub().getEmail());
        vendorEquipmentRequest.setSki(getSkiListStub());
        vendorEquipmentRequest.setSnowboards(getSnowboardListStub());
        return vendorEquipmentRequest;
    }

    public static EquipementVendorAvailable getEquipementVendorAvailableStub() {
        EquipementVendorAvailable equipementVendorAvailable = new EquipementVendorAvailable();
        equipementVendorAvailable.setVendorEmail(getVendorStub().getEmail());
        equipementVendorAvailable.setStartDate("12/12/2024");
        equipementVendorAvailable.setEndDate("12/12/2024");
        return equipementVendorAvailable;
    }

    public static Offer getOfferStub() {
        Offer offer = new Offer();
        offer.setIdOffer(1);
        offer.setName("offer1");
        offer.setDate(LocalDate.now());
        offer.setDiscount(23);
        offer.setPlan(getPlanStub());
        return offer;
    }

    public static List<Location> getLocationListStub() {
        List<Location> locationResponses = new ArrayList<>();
        for(int i = 1; i < 10; i++) {
            Location location = new Location();
            location.setLocationId(i);
            location.setName("location"+ i);
            location.setPriceSubscription(35.00);
            location.setStartOfSeason(LocalDate.now());
            location.setEndOfSeason(LocalDate.now());
            location.setOpeningSkiLift(LocalTime.now());
            location.setClosingSkiLift(LocalTime.now());
            locationResponses.add(location);
        }
        return locationResponses;
    }

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

    public static MessageRequest getMessageRequestStub() {
        MessageRequest messageRequest = new MessageRequest();
        messageRequest.setUsername("maestro1@gmail.com");
        messageRequest.setMessage("ciao ciao");
        return messageRequest;
    }

    public static CommentRequest getCommentRequestStub() {
        CommentRequest commentRequest = new CommentRequest();
        commentRequest.setIdMessage(1);
        commentRequest.setUsername("user1@gmail.com");
        commentRequest.setComment("ciao ciao");
        return commentRequest;
    }

    public static List<MessageDTO> getMessageDTOListStub() {
        List<MessageDTO> messageDTOList = new ArrayList<>();
        for(int i = 1; i < 10; i++) {
            MessageDTO messageDTO = new MessageDTO();
            messageDTO.setIdMessage(i);
            messageDTO.setUser(getUserStub().getUserId() + i);
            messageDTO.setMessage("ciao"+ i);
            messageDTO.setComments(getCommentDTOListStub());
            messageDTOList.add(messageDTO);
        }
        return messageDTOList;
    }

    public static List<CommentDTO> getCommentDTOListStub() {
        List<CommentDTO> commentDTOList = new ArrayList<>();
        for(int i = 1; i < 10; i++) {
            CommentDTO commentDTO = new CommentDTO();
            commentDTO.setId(i);
            commentDTO.setUser(getUserStub().getEmail().concat(""+i));
            commentDTO.setComment("ciao" + 1);
            commentDTOList.add(commentDTO);
        }
        return commentDTOList;
    }

    public static MessageResponse getMessageResponseStub() {
        MessageResponse messageResponse = new MessageResponse();
        messageResponse.setIdLocation(1);
        messageResponse.setListMessage(getMessageDTOListStub());
        return messageResponse;
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
        instructor.setId(1);
        instructor.setEmail("maestro1@gmail.com");
        instructor.setName("maestro1");
        instructor.setSurname("_maestro1_");
        instructor.setLocation(getLocationStub());
        instructor.setSpeciality("ski");
        return instructor;
    }

    public static Location getLocationStub() {
        Location location = new Location();
        location.setLocationId(1);
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

    public static BuySkipass getBuySkipass() {
        BuySkipass buySkipass = new BuySkipass();
        buySkipass.setId(1);
        buySkipass.setUser(getUserStub());
        buySkipass.setCardSkipass(getCardSkipassStub());
        buySkipass.setPrice(35.00);
        buySkipass.setDate(Utils.formatterData("12/12/2024"));
        return buySkipass;
    }

    public static CardSkipass getCardSkipassStub(){
        CardSkipass cardSkipass = new CardSkipass();
        cardSkipass.setId(1);
        cardSkipass.setCardCode("cardCode1");
        cardSkipass.setLocation(getLocationStub());
        return cardSkipass;
    }

    public static List<User> getUserListStub() {
        List<User> userList = new ArrayList<>();
        for (int i = 1; i < 10; i++) {
            User user = new User();
            user.setUserId(i);
            user.setSurname("_user_"+i);
            user.setEmail(i + "useremail@email.com");
            user.setPassword(i+"pass");
            user.setGender(Gender.WOMAN);
            user.setEnable(true);
            user.setName("user"+i);
            user.setDateBirth(Utils.formatterData("14/05/2001"));
            userList.add(user);
        }
        return userList;
    }

    public static Reservation getReservationStub() {
        Reservation reservation = new Reservation();
        reservation.setReservationId(1);
        reservation.setUser(getUserStub());
        reservation.setVendor(getVendorStub());
        reservation.setStartDate(LocalDate.now());
        reservation.setEndDate(LocalDate.now());
        reservation.setSnowboardReserved(getSnowboardListStub());
        reservation.setSkiReserved(getSkiListStub());
        return reservation;
    }

    public static Vendor getVendorStub(){
        Vendor vendor = new Vendor();
        vendor.setVendorId(1);
        vendor.setEmail("vendor1@mail.com");
        vendor.setName("vendor1");
        vendor.setLocation(getLocationStub());
        return vendor;
    }

    public static List<Ski> getSkiListStub() {
        List<Ski> skis = new ArrayList<>();
        for(int i = 1; i < 10; i++){
            Ski ski = new Ski();
            ski.setId(i);
            ski.setMeasure(189 + i);
            skis.add(ski);
        }
        return skis;
    }

    public static List<SkiDTO> getSkiDTOListStub() {
        List<SkiDTO> skis = new ArrayList<>();
        for(int i = 1; i < 10; i++){
            SkiDTO ski = new SkiDTO();
            ski.setId(i);
            ski.setMeasure(189 + i);
            skis.add(ski);
        }
        return skis;
    }

    public static List<Snowboard> getSnowboardListStub() {
        List<Snowboard> snowboards = new ArrayList<>();
        for(int i = 1; i < 10; i++){
            Snowboard snowboard = new Snowboard();
            snowboard.setId(i);
            snowboard.setMeasure(189 + i);
            snowboards.add(snowboard);
        }
        return snowboards;
    }

    public static List<SnowboardDTO> getSnowboardDTOListStub() {
        List<SnowboardDTO> snowboards = new ArrayList<>();
        for(int i = 1; i < 10; i++){
            SnowboardDTO snowboard = new SnowboardDTO();
            snowboard.setId(i);
            snowboard.setMeasure(189 + i);
            snowboards.add(snowboard);
        }
        return snowboards;
    }
}
