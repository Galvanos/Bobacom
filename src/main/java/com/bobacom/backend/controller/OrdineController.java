package com.bobacom.backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

 
import com.bobacom.backend.service.interfaces.IOrdineService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/rest/ordine")
public class OrdineController {
//	private Carrello carrello;
	private IOrdineService orderS;
	
}
