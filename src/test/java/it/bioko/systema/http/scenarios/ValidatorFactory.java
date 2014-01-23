/*
 * Copyright (c) 2014																 
 *	Mikol Faro			<mikol.faro@gmail.com>
 *	Simone Mangano		<simone.mangano@ieee.org>
 *	Mattia Tortorelli	<mattia.tortorelli@gmail.com>
 *
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * 
 */

package it.bioko.systema.http.scenarios;

import static it.bioko.utils.matcher.Matchers.anyString;
import static it.bioko.utils.matcher.Matchers.matchesJSONString;
import static org.hamcrest.Matchers.equalTo;
import it.bioko.http.rest.exception.HttpError;
import it.bioko.http.rest.exception.HttpResponseBuilder;
import it.bioko.http.rest.exception.HttpResponseExceptionFactory;
import it.bioko.http.scenario.HttpScenarioFactory;
import it.bioko.http.scenario.Scenario;
import it.bioko.system.command.ValidationException;
import it.bioko.systema.entity.dummyComplex.DummyComplexDomainEntity;
import it.bioko.systema.entity.dummyComplex.DummyComplexDomainEntityBuilder;
import it.bioko.systema.factory.SystemACommands;
import it.bioko.utils.domain.ErrorEntity;
import it.bioko.utils.validator.ValidatorErrorBuilder;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.simple.JSONValue;

public class ValidatorFactory {

	
	public static Scenario postSuccesfulWithCrud()  throws Exception {
		Scenario scenario = new Scenario("post succesful");
		
		DummyComplexDomainEntityBuilder builder = new DummyComplexDomainEntityBuilder();
		builder.loadDefaultExample();
		
		scenario.addScenarioStep("insert dummy example1", HttpScenarioFactory.postSuccessful(SystemACommands.DUMMY_COMPLEX_DOMAIN_ENTITY,
				null, null, builder.build(false).toJSONString(), equalTo("["+builder.build(true).toJSONString()+"]")));
		
		return scenario;
		
	}
	
	
	public static Scenario postFailWithCrudBecauseFieldsAreMissing() throws Exception {
		Scenario scenario = new Scenario("post fail because fields are missing");
		
		DummyComplexDomainEntityBuilder builder = new DummyComplexDomainEntityBuilder();
		builder.loadDefaultExample();
		String json = builder.getJsonForFields(DummyComplexDomainEntity.AN_INTEGER_OPTIONAL_FIELD);
		
		// build expected error
		ArrayList<ErrorEntity> errors= new ArrayList<ErrorEntity>();
		errors.add(ValidatorErrorBuilder.buildMandatoryFieldsMissingError(DummyComplexDomainEntity.A_STRING_FIELD_MANDATORY_ALSO_IN_GET));
		errors.add(ValidatorErrorBuilder.buildMandatoryFieldsMissingError(DummyComplexDomainEntity.A_STRING_MANDATORY_FIELD));		
		ValidationException expectedException = new ValidationException(errors);

		HttpResponseBuilder responseBuilder = new HttpResponseBuilder(HttpResponseExceptionFactory.create());
		HttpError expectedError = responseBuilder.buildFrom(expectedException);

		scenario.addScenarioStep("post with missing fields", HttpScenarioFactory.postFailed(SystemACommands.DUMMY_COMPLEX_DOMAIN_ENTITY, 
				null, null, json, expectedError.status(), matchesJSONString(JSONValue.toJSONString(expectedError.body()))));
		
		return scenario;
	}
	
	
	public static Scenario postFailWithCrudBecauseFieldsAreMissingAndWrongTypedField() throws Exception {
		Scenario scenario = new Scenario("post fail because fields are missing and a field have wrong type");
		
		DummyComplexDomainEntityBuilder builder = new DummyComplexDomainEntityBuilder();
		builder.loadDefaultExample();
		builder.set(DummyComplexDomainEntity.AN_INTEGER_OPTIONAL_FIELD, "wrongTypedValue");
		String json = builder.getJsonForFields(DummyComplexDomainEntity.AN_INTEGER_OPTIONAL_FIELD);
		
		// build expected error
		ArrayList<ErrorEntity> errors= new ArrayList<ErrorEntity>();
		errors.add(ValidatorErrorBuilder.buildMandatoryFieldsMissingError(DummyComplexDomainEntity.A_STRING_FIELD_MANDATORY_ALSO_IN_GET));
		errors.add(ValidatorErrorBuilder.buildMandatoryFieldsMissingError(DummyComplexDomainEntity.A_STRING_MANDATORY_FIELD));
		errors.add(ValidatorErrorBuilder.buildWrongTypeError(DummyComplexDomainEntity.AN_INTEGER_OPTIONAL_FIELD));
		ValidationException expectedException = new ValidationException(errors);

		HttpResponseBuilder responseBuilder = new HttpResponseBuilder(HttpResponseExceptionFactory.create());
		HttpError expectedError = responseBuilder.buildFrom(expectedException);

		scenario.addScenarioStep("post with missing fields and wrong type", HttpScenarioFactory.postFailed(SystemACommands.DUMMY_COMPLEX_DOMAIN_ENTITY, 
				null, null, json, expectedError.status(), matchesJSONString(JSONValue.toJSONString(expectedError.body()))));
		
		return scenario;
	}
	
	
	
	public static Scenario checkCrudInPUTAndFailBecause3FieldsMissing() throws Exception {
		Scenario scenario = new Scenario("Check missing mandatory fields in put and fail, all mandatory fields are missing");
		
		DummyComplexDomainEntityBuilder builder = new DummyComplexDomainEntityBuilder();
		builder.loadDefaultExample();
		
		String json = builder.getJsonForFields(DummyComplexDomainEntity.A_STRING_OPTIONAL_FIELD);

		// build expected error
		ArrayList<ErrorEntity> errors= new ArrayList<ErrorEntity>();
		errors.add(ValidatorErrorBuilder.buildMandatoryFieldsMissingError(DummyComplexDomainEntity.A_STRING_FIELD_MANDATORY_ALSO_IN_GET));
		errors.add(ValidatorErrorBuilder.buildMandatoryFieldsMissingError(DummyComplexDomainEntity.A_STRING_MANDATORY_FIELD));
		errors.add(ValidatorErrorBuilder.buildMandatoryFieldsMissingError(DummyComplexDomainEntity.ID));
		ValidationException expectedException = new ValidationException(errors);

		HttpResponseBuilder responseBuilder = new HttpResponseBuilder(HttpResponseExceptionFactory.create());
		HttpError expectedError = responseBuilder.buildFrom(expectedException);
		
		scenario.addScenarioStep("all mandatory fields missing", HttpScenarioFactory.putFailed(SystemACommands.DUMMY_COMPLEX_DOMAIN_ENTITY, 
				null, null, json, expectedError.status(), matchesJSONString(JSONValue.toJSONString(expectedError.body()))));
		
		return scenario;
	}

	public static Scenario checkCrudInPUTAndFailBecauseIDisMissing() throws Exception {
		Scenario scenario = new Scenario("Check missing mandatory fields in put and fail, id is missing");
		
		DummyComplexDomainEntityBuilder builder = new DummyComplexDomainEntityBuilder();
		builder.loadDefaultExample();
		
		String json = builder.getJsonForFields(
				DummyComplexDomainEntity.A_STRING_OPTIONAL_FIELD,
				DummyComplexDomainEntity.A_STRING_FIELD_MANDATORY_ALSO_IN_GET,
				DummyComplexDomainEntity.A_STRING_MANDATORY_FIELD
				);

		// build expected error
		ArrayList<ErrorEntity> errors= new ArrayList<ErrorEntity>();
		errors.add(ValidatorErrorBuilder.buildMandatoryFieldsMissingError(DummyComplexDomainEntity.ID));
		ValidationException expectedException = new ValidationException(errors);

		HttpResponseBuilder responseBuilder = new HttpResponseBuilder(HttpResponseExceptionFactory.create());
		HttpError expectedError = responseBuilder.buildFrom(expectedException);
		
		scenario.addScenarioStep("id is missing", HttpScenarioFactory.putFailed(SystemACommands.DUMMY_COMPLEX_DOMAIN_ENTITY, 
				null, null, json, expectedError.status(), matchesJSONString(JSONValue.toJSONString(expectedError.body()))));
		
		return scenario;
	}

	
	public static Scenario checkCrudInGETandfail() throws Exception {
		Scenario scenario = new Scenario("Check missing mandatory field in get and fail");
		
		DummyComplexDomainEntityBuilder builder = new DummyComplexDomainEntityBuilder();
		builder.loadDefaultExample();
		HashMap<String, String> queryString = null;
		
		// build expected error
		ArrayList<ErrorEntity> errors= new ArrayList<ErrorEntity>();
		errors.add(ValidatorErrorBuilder.buildMandatoryFieldsMissingError(DummyComplexDomainEntity.A_STRING_FIELD_MANDATORY_ALSO_IN_GET));
		ValidationException expectedException = new ValidationException(errors);
		
		HttpResponseBuilder responseBuilder = new HttpResponseBuilder(HttpResponseExceptionFactory.create());
		HttpError expectedError = responseBuilder.buildFrom(expectedException);
		
		scenario.addScenarioStep("a mandatory fields is missing in post", HttpScenarioFactory.getFailed(SystemACommands.DUMMY_COMPLEX_DOMAIN_ENTITY, 
				null, queryString, null, expectedError.status(), matchesJSONString(JSONValue.toJSONString(expectedError.body()))));
		
		return scenario;
	}
	
	public static Scenario checkCrudInGETandSuccesfull() throws Exception {
		Scenario scenario = new Scenario("Check a get with a mandatory queryString filed and succesful");
		
		scenario.addScenario(postSuccesfulWithCrud());
		
		DummyComplexDomainEntityBuilder builder = new DummyComplexDomainEntityBuilder();
		builder.loadDefaultExample();
		HashMap<String, String> queryString = null; 
				
		queryString =	new HashMap<String, String>();
		queryString.put(DummyComplexDomainEntity.A_STRING_FIELD_MANDATORY_ALSO_IN_GET, builder.get(DummyComplexDomainEntity.A_STRING_FIELD_MANDATORY_ALSO_IN_GET));
		
		scenario.addScenarioStep("succesful get with a qs field", HttpScenarioFactory.getSuccessful(SystemACommands.DUMMY_COMPLEX_DOMAIN_ENTITY,
				null, queryString, null, anyString()));
		
		return scenario;
	}
	
	
	public static Scenario postCommandAndSuccess() throws Exception {
		Scenario scenario = new Scenario("post command and success");
		
		String json = "{" +
				"\"textMandatoryField\":\"some text\"" +
				"}" ;
		
		scenario.addScenarioStep("post only mandatory field", HttpScenarioFactory.postSuccessful(
				SystemACommands.VALIDATED_COMMAND,
				null, null, json, anyString()));
		
		return scenario;
	}
	
	
	public static Scenario postCommandAndSuccess2() throws Exception {
		Scenario scenario = new Scenario("post command and success");
		
		String json = "{" +
				"\"textMandatoryField\":\"some text\"," +
				"\"textOptionalField\":\"some other text\"," +
				"\"integerOptionalField\":\"12\"" +
				"}" ;
		
		scenario.addScenarioStep("post all fields", HttpScenarioFactory.postSuccessful(
				SystemACommands.VALIDATED_COMMAND,
				null, null, json, anyString()));
		
		return scenario;
	}
	
	
	public static Scenario postCommandAndFailBecauseMissingMandatoryField() throws Exception {
		Scenario scenario = new Scenario("post command and fail because missing mandatory field");
		
		String json = "{" +				
				"\"textOptionalField\":\"some other text\"," +
				"\"integerOptionalField\":\"12\"" +
				"}" ;
		
		
		// build expected error
		ArrayList<ErrorEntity> errors= new ArrayList<ErrorEntity>();
		errors.add(ValidatorErrorBuilder.buildMandatoryFieldsMissingError("textMandatoryField"));
		ValidationException expectedException = new ValidationException(errors);

		HttpResponseBuilder responseBuilder = new HttpResponseBuilder(HttpResponseExceptionFactory.create());
		HttpError expectedError = responseBuilder.buildFrom(expectedException);


		scenario.addScenarioStep("post command without mandatory field", HttpScenarioFactory.postFailed(SystemACommands.VALIDATED_COMMAND, 
				null, null, json, expectedError.status(), matchesJSONString(JSONValue.toJSONString(expectedError.body()))));
		
		
		return scenario;
	}
	
	
	public static Scenario postCommandAndFailBecauseMissingMandatoryFieldAndWrongTypeField() throws Exception {
		Scenario scenario = new Scenario("post command and fail because missing mandatory field");
		
		String json = "{" +				
				"\"textOptionalField\":\"some other text\"," +
				"\"integerOptionalField\":\"wrongType\"" +
				"}" ;
		
		
		// build expected error
		ArrayList<ErrorEntity> errors= new ArrayList<ErrorEntity>();
		errors.add(ValidatorErrorBuilder.buildMandatoryFieldsMissingError("textMandatoryField"));
		errors.add(ValidatorErrorBuilder.buildWrongTypeError("integerOptionalField"));
		ValidationException expectedException = new ValidationException(errors);

		HttpResponseBuilder responseBuilder = new HttpResponseBuilder(HttpResponseExceptionFactory.create());
		HttpError expectedError = responseBuilder.buildFrom(expectedException);


		scenario.addScenarioStep("post command without mandatory field", HttpScenarioFactory.postFailed(SystemACommands.VALIDATED_COMMAND, 
				null, null, json, expectedError.status(), matchesJSONString(JSONValue.toJSONString(expectedError.body()))));
		
		
		return scenario;
	}
	
	
	public static Scenario testUniqueExtraValidatorOnOneValidatorCommand1() throws Exception {
		Scenario scenario = new Scenario("test unique extra validator on single validator command, fail on the second insertion");		
		String json = "{" +
				"\"textMandatoryField\":\"some repeted text\"" +
				"}" ;
		
		scenario.addScenarioStep("save a simple value and success", HttpScenarioFactory.postSuccessful(
				SystemACommands.VALIDATED_COMMAND_WITH_UNIQUE_VALIDATOR,
				null, null, json, anyString()));
		
		
		ArrayList<ErrorEntity> errors= new ArrayList<ErrorEntity>();
		errors.add(ValidatorErrorBuilder.buildUniqueViolationError("textMandatoryField"));
		
		ValidationException expectedException = new ValidationException(errors);

		HttpResponseBuilder responseBuilder = new HttpResponseBuilder(HttpResponseExceptionFactory.create());
		HttpError expectedError = responseBuilder.buildFrom(expectedException);

		scenario.addScenarioStep("save another time the same value and fail because not unique", HttpScenarioFactory.postFailed(SystemACommands.VALIDATED_COMMAND_WITH_UNIQUE_VALIDATOR, 
				null, null, json, expectedError.status(), matchesJSONString(JSONValue.toJSONString(expectedError.body()))));
		
		return scenario;
	}
	
	
	public static Scenario testUniqueExtraValidatorOnOneValidatorCommand2() throws Exception {
		Scenario scenario = new Scenario("test unique extra validator using on single validator command using two inputs, fail on the second insertion of the unique field");		
		String json = "{" +
				"\"textMandatoryField\":\"first text\"," +
				"\"textOptionalField\":\"11\"" +
				"}" ;
		
		scenario.addScenarioStep("save value and succes", HttpScenarioFactory.postSuccessful(
				SystemACommands.VALIDATED_COMMAND_WITH_UNIQUE_VALIDATOR,
				null, null, json, anyString()));
		
		String json2 = "{" +
				"\"textMandatoryField\":\"second text\"," +
				"\"textOptionalField\":\"11\"" +
				"}" ;
		
		scenario.addScenarioStep("save onther text and success", HttpScenarioFactory.postSuccessful(
				SystemACommands.VALIDATED_COMMAND_WITH_UNIQUE_VALIDATOR,
				null, null, json2, anyString()));
		
		
		
		ArrayList<ErrorEntity> errors= new ArrayList<ErrorEntity>();
		errors.add(ValidatorErrorBuilder.buildUniqueViolationError("textMandatoryField"));
		
		ValidationException expectedException = new ValidationException(errors);

		HttpResponseBuilder responseBuilder = new HttpResponseBuilder(HttpResponseExceptionFactory.create());
		HttpError expectedError = responseBuilder.buildFrom(expectedException);

		scenario.addScenarioStep("save another time the same value and fail because not unique", HttpScenarioFactory.postFailed(SystemACommands.VALIDATED_COMMAND_WITH_UNIQUE_VALIDATOR, 
				null, null, json, expectedError.status(), matchesJSONString(JSONValue.toJSONString(expectedError.body()))));
		
		return scenario;
	}
	
	
	public static Scenario testUniqueExtraValidatorOnTwoVaslidatosCommand() throws Exception {
		Scenario scenario = new Scenario("test unique extra validator using on two validator command and failing with the second validator");		
		String json = "{" +
				"\"textMandatoryField\":\"first text\"," +
				"\"integerOptionalField\":\"11\"" +
				"}" ;
		
		scenario.addScenarioStep("save value and succes", HttpScenarioFactory.postSuccessful(
				SystemACommands.VALIDATED_COMMAND_WITH_TWO_UNIQUE_VALIDATORS,
				null, null, json, anyString()));
		
		String json2 = "{" +
				"\"textMandatoryField\":\"second text\"," +
				"\"integerOptionalField\":\"11\"" +
				"}" ;
		
		
		ArrayList<ErrorEntity> errors= new ArrayList<ErrorEntity>();
		errors.add(ValidatorErrorBuilder.buildUniqueViolationError("integerOptionalField"));
		
		ValidationException expectedException = new ValidationException(errors);

		HttpResponseBuilder responseBuilder = new HttpResponseBuilder(HttpResponseExceptionFactory.create());
		HttpError expectedError = responseBuilder.buildFrom(expectedException);

		scenario.addScenarioStep("save another time the same value and fail because not unique", HttpScenarioFactory.postFailed(SystemACommands.VALIDATED_COMMAND_WITH_TWO_UNIQUE_VALIDATORS, 
				null, null, json2, expectedError.status(), matchesJSONString(JSONValue.toJSONString(expectedError.body()))));
		
		return scenario;
	}
	
	
	
	public static Scenario testUniqueExtraValidatorOnTwoVaslidatosCommand2() throws Exception {
		Scenario scenario = new Scenario("test unique extra validator using on two validator command and failing with the second validator");		
		String json = "{" +
				"\"textMandatoryField\":\"first text\"," +
				"\"integerOptionalField\":\"11\"" +
				"}" ;
		
		scenario.addScenarioStep("save value and succes", HttpScenarioFactory.postSuccessful(
				SystemACommands.VALIDATED_COMMAND_WITH_TWO_UNIQUE_VALIDATORS,
				null, null, json, anyString()));
		
		
		ArrayList<ErrorEntity> errors= new ArrayList<ErrorEntity>();
		errors.add(ValidatorErrorBuilder.buildUniqueViolationError("integerOptionalField"));
		errors.add(ValidatorErrorBuilder.buildUniqueViolationError("textMandatoryField"));
		
		ValidationException expectedException = new ValidationException(errors);

		HttpResponseBuilder responseBuilder = new HttpResponseBuilder(HttpResponseExceptionFactory.create());
		HttpError expectedError = responseBuilder.buildFrom(expectedException);

		scenario.addScenarioStep("save another time the same value and fail because not unique", HttpScenarioFactory.postFailed(SystemACommands.VALIDATED_COMMAND_WITH_TWO_UNIQUE_VALIDATORS, 
				null, null, json, expectedError.status(), matchesJSONString(JSONValue.toJSONString(expectedError.body()))));
		
		return scenario;
	}
	
}
