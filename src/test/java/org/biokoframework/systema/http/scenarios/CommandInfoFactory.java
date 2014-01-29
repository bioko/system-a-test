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

import static org.biokoframework.utils.matcher.Matchers.substringMatchesPattern;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.startsWith;

import java.util.ArrayList;
import java.util.HashMap;

import org.biokoframework.http.scenario.HttpScenarioFactory;
import org.biokoframework.http.scenario.JSonExpectedResponseBuilder;
import org.biokoframework.http.scenario.Scenario;
import org.biokoframework.system.KILL_ME.commons.GenericFieldNames;
import org.biokoframework.system.entity.description.CommandEntity;
import org.biokoframework.system.entity.description.CommandEntityBuilder;
import org.biokoframework.system.entity.description.ParameterEntity;
import org.biokoframework.system.entity.description.ParameterEntityBuilder;
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
