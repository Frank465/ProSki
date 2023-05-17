package com.ingegneriadelsoftware.ProSki.Utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Utils {

    public static final String REGEX_EMAIL = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
    public static final String SPECIALITA = "^(?i)(sky|snowboard)$";
    public static final String ERROR_EMAIL = "Formato mail non valido";
    public static final String REGEX_DATA = "\\d{2}/\\d{2}/\\d{4}";
    public static final String ERROR_DATA = "La data deve essere nel formato dd/MM/yyyy";
    public static final String IS_DOUBLE = "^((\\d*\\.\\d+)|(\\d+\\.?))$";


    public static LocalDateTime formatterData(String data) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return LocalDateTime.parse(data, formatter);
    }
}
