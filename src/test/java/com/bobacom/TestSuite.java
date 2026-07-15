package com.bobacom;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

import com.bobacom.backend.service.TestKeyValuesService;



@Suite
@SelectClasses({
TestKeyValuesService.class
})
public class TestSuite {

}
