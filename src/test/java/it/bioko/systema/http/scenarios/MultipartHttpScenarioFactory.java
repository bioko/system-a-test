package it.bioko.systema.http.scenarios;

import static org.hamcrest.Matchers.equalTo;
import it.bioko.http.scenario.HttpMultipartScenarioStep;
import it.bioko.http.scenario.JSonExpectedResponseBuilder;
import it.bioko.http.scenario.Scenario;
import it.bioko.system.KILL_ME.commons.GenericFieldValues;
import it.bioko.system.KILL_ME.commons.HttpMethod;
import it.bioko.systema.command.MultipartCommand;
import it.bioko.systema.commons.SystemACommandNames;
import it.bioko.systema.entity.dummyMultipart.DummyMultipart;
import it.bioko.systema.entity.dummyMultipart.DummyMultipartBuilder;

import java.io.InputStream;


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
		
		
		multipartScenarioStep.addPart(DummyMultipart.FIRST_TEXT_FIELD,builder.build(true).get(DummyMultipart.FIRST_TEXT_FIELD));
		multipartScenarioStep.addPart(DummyMultipart.SECOND_TEXT_FIELD,builder.build(true).get(DummyMultipart.SECOND_TEXT_FIELD));
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