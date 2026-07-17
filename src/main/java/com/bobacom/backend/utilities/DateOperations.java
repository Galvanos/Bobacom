package com.bobacom.backend.utilities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

import com.bobacom.backend.exceptions.AcademyException;

public class DateOperations {
	private final static String PATTERN_DATE = "dd/MM/yyyy";
	
	public static String dateToString(LocalDate myDate) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(PATTERN_DATE, Locale.ITALIAN);		
		return myDate.format(formatter);
	}

	public static String dateToString(LocalDateTime myDate) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(PATTERN_DATE, Locale.ITALIAN);		
		return myDate.format(formatter);
	}
	
	public static LocalDate stringToDate(String input) throws AcademyException{
		if(input == null)
			return null;
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
