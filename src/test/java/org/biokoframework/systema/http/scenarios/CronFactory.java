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

import org.biokoframework.http.scenario.ExecutionScenarioStep;
import org.biokoframework.http.scenario.Scenario;
import org.biokoframework.http.scenario.mail.MailScenarioStep;
import org.biokoframework.system.services.cron.dev.DevCronService;
import org.biokoframework.systema.command.CronExampleCommand;
import org.biokoframework.systema.command.CronFailingCommand;
import org.biokoframework.systema.entity.dummy1.DummyEntity1;
import org.biokoframework.systema.misc.Dummy1Mock;

import static org.biokoframework.http.matcher.Matchers.matchesSubjectAndContent;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class CronFactory {
	
	public static Scenario createSimpleCron() throws Exception {
		
		Scenario scenario = new Scenario("Simple cron use");

		scenario.addScenarioStep("Set the value of the mock service to triangolo", new ExecutionScenarioStep() {
			@Override
			public void execute() {
				Dummy1Mock.setShape(Dummy1Mock.TRIANGOLO);
			}
		});

        scenario.addScenarioStep("Trigger DEV cron", new ExecutionScenarioStep() {
            @Override
            public void execute() throws Exception {
                DevCronService.INSTANCE.trigger(CronExampleCommand.class);
            }
        });
		
		scenario.addScenarioStep("Check that cron actually worked, mock is set to quadrato", new ExecutionScenarioStep() {
			@Override
			public void execute() {
				assertThat(Dummy1Mock.getShape().get(DummyEntity1.VALUE), is(equalTo((Object) Dummy1Mock.QUADRATO)));
			}
		});
		
		return scenario;
		
	}
	
	public static Scenario createFailureCron() throws Exception {
		
		Scenario scenario = new Scenario("Simple cron failure");

        scenario.addScenarioStep("Trigger DEV cron", new ExecutionScenarioStep() {
            @Override
            public void execute() throws Exception {
                DevCronService.INSTANCE.trigger(CronFailingCommand.class);
            }
        });

        scenario.addScenarioStep("Cron should notify the failure", new MailScenarioStep(
				"root@example.com",
				matchesSubjectAndContent(
						equalTo("Bioko cron service - failure report"),
						startsWith("Command " + CronFailingCommand.class.getName() + " failed"))));
		
		return scenario;
	}

}
