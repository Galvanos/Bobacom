package com.bobacom.backend.utilities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

import com.bobacom.backend.exceptions.AcademyException;

public class DateOperations {
	private final static String PATTERN_DATE = "dd/MM/yyyy";

	public static String dateToString(LocalDateTime myDate) {
		return dateToString(PATTERN_DATE,myDate);
	}
	public static String dateToString(String pattern, LocalDateTime myDate) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern, Locale.ITALIAN);		
		return myDate.format(formatter);
	}
	
	public static LocalDate stringToDate(String input){
		LocalDate r = null;
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(PATTERN_DATE, Locale.ITALIAN);
			r=  LocalDate.parse(input, formatter);
			
		} catch (DateTimeParseException e) {
			throw new AcademyException("Formato della data invalido; formato previsto: " + PATTERN_DATE);
		}
		return r;
	}
}
