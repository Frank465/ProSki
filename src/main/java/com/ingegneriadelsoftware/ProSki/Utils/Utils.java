package com.ingegneriadelsoftware.ProSki.Utils;

import org.springframework.cglib.core.Local;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class Utils {

    public static final String REGEX_EMAIL = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
    public static final String SPECIALITA = "^(?i)(sky|snowboard)$";
    public static final String SESSO = "^(?i)(uomo|donna)$";
    public static final String LOCALDATE = "^(0[1-9]|[1-2][0-9]|3[01])/(0[1-9]|1[0-2])/\\d{4}$";
    public static final String LOCALDATETIME = "^(\\d{2})/(\\d{2})/(\\d{4}) (\\d{2}):(\\d{2})$";
    public static final String LOCALTIME = "^([01][0-9]|2[0-3]):[0-5][0-9]$";
    public static final String ERROR_EMAIL = "Formato mail non valido";
    public static final String ERROR_LOCALDATE = "La data deve essere nel formato dd/MM/yyyy";
    public static final String ERROR_LOCALDATETIME = "La data deve essere nel formato dd/MM/yyyy HH:mm";
    public static final String ERROR_LOCALTIME = "L'orario deve essere nel formato HH:mm";
    public static final String IS_DOUBLE = "^((\\d*\\.\\d+)|(\\d+\\.?))$";


    public static LocalDateTime formatterDataTime(String data) throws DateTimeException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return LocalDateTime.parse(data, formatter);
    }

    public static LocalDate formatterData(String data) throws DateTimeException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return LocalDate.parse(data, formatter);
    }

    public static LocalTime formatterTime(String time) throws DateTimeException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return LocalTime.parse(time, formatter);
    }
}
