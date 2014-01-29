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

package org.biokoframework.systema.http.scenarios;

import static org.hamcrest.Matchers.equalTo;

import java.io.InputStream;

import org.biokoframework.http.scenario.HttpMultipartScenarioStep;
import org.biokoframework.http.scenario.JSonExpectedResponseBuilder;
import org.biokoframework.http.scenario.Scenario;
import org.biokoframework.system.KILL_ME.commons.GenericFieldValues;
import org.biokoframework.system.KILL_ME.commons.HttpMethod;
import org.biokoframework.systema.command.MultipartCommand;
import org.biokoframework.systema.commons.SystemACommandNames;
import org.biokoframework.systema.entity.dummyMultipart.DummyMultipart;
import org.biokoframework.systema.entity.dummyMultipart.DummyMultipartBuilder;


public class MultipartHttpScenarioFactory {
	
	public static final String	C64KERNEL_BIN_FILE	=	"testdata/C64Kernel.bin";
	public static final String	MANTRA_TXT_FILE		=	"testdata/mantra.txt";
	

	public static Scenario testWithoutExecutionStep() throws Exception {
		
		
		Scenario scenario = new Scenario("Upload multipart form");
		
		DummyMultipartBuilder builder = new DummyMultipartBuilder();
		builder.loadDefaultExample();
		
		HttpMultipartScenarioStep multipartScenarioStep = new HttpMultipartScenarioStep(
				SystemACommandNames.MULTIPART_COMMAND,
				HttpMethod.POST.toString(), 
				null,  // headers
				null,	// parameters
				200,	// expected status code	
				equalTo(JSonExpectedResponseBuilder.asArray(builder.build(true).toJSONString())));
		
		
		multipartScenarioStep.addPart(DummyMultipart.FIRST_TEXT_FIELD, builder.build(true).get(DummyMultipart.FIRST_TEXT_FIELD).toString());
		multipartScenarioStep.addPart(DummyMultipart.SECOND_TEXT_FIELD,builder.build(true).get(DummyMultipart.SECOND_TEXT_FIELD).toString());
		// 1st file
		InputStream firstFileIStream = MultipartHttpScenarioFactory.class.getClassLoader().getResourceAsStream(C64KERNEL_BIN_FILE);
		multipartScenarioStep.addPart(MultipartCommand.FIRST_FILE_PART_NAME, firstFileIStream, GenericFieldValues.OCTET_CONTENT_TYPE);
		// 2ndfile
		InputStream secondFileIStream = MultipartHttpScenarioFactory.class.getClassLoader().getResourceAsStream(C64KERNEL_BIN_FILE);		
		multipartScenarioStep.addPart(MultipartCommand.SECOND_FILE_PART_NAME, secondFileIStream, GenericFieldValues.TEXT_CONTENT_TYPE);
		
		scenario.addScenarioStep("Upload multipart form", multipartScenarioStep);
		
	
		
		return scenario;
	}
	

}