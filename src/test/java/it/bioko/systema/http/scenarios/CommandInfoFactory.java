package it.bioko.systema.http.scenarios;

import static it.bioko.utils.matcher.Matchers.substringMatchesPattern;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.startsWith;
import it.bioko.http.scenario.HttpScenarioFactory;
import it.bioko.http.scenario.JSonExpectedResponseBuilder;
import it.bioko.http.scenario.Scenario;
import it.bioko.system.KILL_ME.commons.GenericFieldNames;
import it.bioko.system.entity.description.CommandEntity;
import it.bioko.system.entity.description.CommandEntityBuilder;
import it.bioko.system.entity.description.ParameterEntity;
import it.bioko.system.entity.description.ParameterEntityBuilder;

import java.util.ArrayList;
import java.util.HashMap;

import org.hamcrest.core.AllOf;

public class CommandInfoFactory {

	public static Scenario createGetCommandListCommand() throws Exception {
		
		Scenario collector = new Scenario("Get command list");
		collector.addScenarioStep("Get (OPTIONS) command list", HttpScenarioFactory.optionsSuccessful(
				"command-list",
				null,
				null,
				null,
				AllOf.<String>allOf(
					startsWith("[{\""),
					substringMatchesPattern("\"name\":\"OPTIONS_command-list\""),
					substringMatchesPattern("\"name\":\"OPTIONS_command-invocation-info\""),
					endsWith("\"}]"))));
		
		return collector;
		
	}
	
	public static Scenario createGetCommandInvocationInfoOnCommandList() throws Exception {
		Scenario collector = new Scenario("Get command invocation info on command list command");
		String commandName = "OPTIONS_command-list";
		
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(GenericFieldNames.COMMAND, commandName);
		
		CommandEntityBuilder commandEntityBuilder = new CommandEntityBuilder();
		commandEntityBuilder.loadDefaultExample();
		commandEntityBuilder.set(CommandEntity.NAME, commandName);
		
		ArrayList<ParameterEntity> outputList = new ArrayList<ParameterEntity>();
		ParameterEntityBuilder parameterEntityBuilder = new ParameterEntityBuilder();
		parameterEntityBuilder.set(ParameterEntity.NAME, GenericFieldNames.NAME);
		outputList.add(parameterEntityBuilder.build(false));
		
		commandEntityBuilder.setOutput(outputList );
		
		collector.addScenarioStep("Get (OPTIONS) command invocation info", HttpScenarioFactory.optionsSuccessful(
				"command-invocation-info", 
				null, 
				map, 
				null, 
				equalTo(JSonExpectedResponseBuilder.asArray(commandEntityBuilder.build(false).toJSONString()))));
		
		return collector;
	}
	
}
