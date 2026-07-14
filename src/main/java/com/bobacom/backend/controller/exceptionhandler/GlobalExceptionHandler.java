package com.bobacom.backend.controller.exceptionhandler;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.bobacom.backend.dto.output.ResponseDTO;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestControllerAdvice
public class GlobalExceptionHandler {
	
	
	@ExceptionHandler(exception = Exception.class)
	 ResponseEntity<ResponseDTO> genericExceptionHandler(Exception e){
		return ResponseEntity.badRequest().body(ResponseDTO.builder()
				.msg(e.getMessage())
				.build());
	}

	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ResponseDTO> handleValidationException(MethodArgumentNotValidException e) {
		List<String> messages = e.getBindingResult()
	                .getFieldErrors()
	                .stream()
	                .map(FieldError::getDefaultMessage).toList();
		
		 messages = Optional.ofNullable(messages).orElse(Collections.emptyList());
		 
		 if(messages.isEmpty()) {
			 messages = Collections.singletonList("errore di validazione");
		 }
		 
		 String msg = messages.stream().collect(Collectors.joining("\n"));
		
		  return ResponseEntity.badRequest()
					.body(ResponseDTO.builder()
							.msg(msg)
							.build()
							);
		  
	}
}
