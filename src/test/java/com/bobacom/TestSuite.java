package com.bobacom;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

import com.bobacom.backend.controller.AuthControllerTest;
import com.bobacom.backend.controller.CreditoControllerTest;
import com.bobacom.backend.controller.UtenteControllerTest;
import com.bobacom.backend.service.ProdottoImplementationTest;
import com.bobacom.backend.service.TestIngredienteService;
import com.bobacom.backend.service.TestKeyValuesService;



@Suite
@SelectClasses({
	TestKeyValuesService.class,
	ProdottoImplementationTest.class,
	TestIngredienteService.class,
	UtenteControllerTest.class,
	AuthControllerTest.class,
	CreditoControllerTest.class,
	TestIngredienteService.class
})
public class TestSuite {

}
