package com.bobacom;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

import com.bobacom.backend.controller.UtenteControllerTest;
import com.bobacom.backend.service.ProdottoImplementationTest;
import com.bobacom.backend.service.TestKeyValuesService;



@Suite
@SelectClasses({
	TestKeyValuesService.class,
	//ProdottoImplementationTest.class,
	UtenteControllerTest.class
})
public class TestSuite {

}
