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

package org.biokoframework.systema.unit;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import org.biokoframework.systema.command.DummyEmptyCommand;
import org.biokoframework.systema.command.PrintLoginIdCommand;
import org.biokoframework.systema.factory.SystemACommands;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.biokoframework.system.ConfigurationEnum;
import org.biokoframework.system.KILL_ME.SystemNames;
import org.biokoframework.system.KILL_ME.XSystemIdentityCard;
import org.biokoframework.system.KILL_ME.commons.GenericFieldNames;
import org.biokoframework.system.KILL_ME.commons.HttpMethod;
import org.biokoframework.system.command.AbstractCommandHandler;
import org.biokoframework.system.command.Command;
import org.biokoframework.system.command.CommandException;
import org.biokoframework.system.context.Context;
import org.biokoframework.system.event.SystemListener;
import org.biokoframework.system.factory.AnnotatedCommandHandlerFactory;
import org.biokoframework.system.repository.core.AbstractRepository;
import org.biokoframework.utils.domain.DomainEntity;
import org.biokoframework.utils.fields.Fields;
import org.biokoframework.utils.repository.Repository;
import org.junit.Ignore;
import org.junit.Test;

public class AnnotatedCrudHideOnTest {

	@Test
	public void testCrudInserted() throws IllegalArgumentException, IllegalAccessException, InstantiationException, ClassNotFoundException, CommandException {
		XSystemIdentityCard identityCard = new XSystemIdentityCard(SystemNames.SYSTEM_A, "1.0", ConfigurationEnum.DEV);
		Context context = getFarloccoContext();
		AbstractCommandHandler ch = AnnotatedCommandHandlerFactory.create(SystemACommands.class, context, identityCard);
		
		Command c = ch.getByName(HttpMethod.GET.name()+"_"+SystemACommands.DUMMY_ENTITY1_HIDDEN_ON_PROD);
		assertNotNull(c);
	}
	
	@Test
	public void testCrudNotInserted() throws IllegalArgumentException, IllegalAccessException, InstantiationException, ClassNotFoundException, CommandException {
		XSystemIdentityCard identityCard = new XSystemIdentityCard(SystemNames.SYSTEM_A, "1.0", ConfigurationEnum.PROD);
		Context context = getFarloccoContext();
		AbstractCommandHandler ch = AnnotatedCommandHandlerFactory.create(SystemACommands.class, context, identityCard);
		
		Command c = ch.getByName(HttpMethod.GET.name()+"_"+SystemACommands.DUMMY_ENTITY1_HIDDEN_ON_PROD);	
		assertNull(c);
	}
	
	@Test
	public void testChangeCommandImplDependingOnConfiguration() throws Exception {
		
		XSystemIdentityCard devCard = new XSystemIdentityCard(SystemNames.SYSTEM_A, "1.0", ConfigurationEnum.DEV);
		Context devContext = getFarloccoContext();
		AbstractCommandHandler devCh = AnnotatedCommandHandlerFactory.create(SystemACommands.class, devContext, devCard);
		
		Command c = devCh.getByName(HttpMethod.POST.name() + "_" + SystemACommands.DEV_MUTANT_COMMAND);
		assertThat(c, is(instanceOf(DummyEmptyCommand.class)));
		
		XSystemIdentityCard prodCard = new XSystemIdentityCard(SystemNames.SYSTEM_A, "1.0", ConfigurationEnum.PROD);
		Context prodContext = getFarloccoContext();
		AbstractCommandHandler prodCh = AnnotatedCommandHandlerFactory.create(SystemACommands.class, prodContext, prodCard);
		
		c = prodCh.getByName(HttpMethod.POST.name() + "_" + SystemACommands.PROD_MUTANT_COMMAND);
		assertThat(c, is(instanceOf(PrintLoginIdCommand.class)));
	
		XSystemIdentityCard demoCard = new XSystemIdentityCard(SystemNames.SYSTEM_A, "1.0", ConfigurationEnum.DEMO);
		Context demoContext = getFarloccoContext();
		AbstractCommandHandler demoCh = AnnotatedCommandHandlerFactory.create(SystemACommands.class, demoContext, demoCard);
		
		c = demoCh.getByName(HttpMethod.POST.name() + "_" + SystemACommands.PROD_MUTANT_COMMAND);
		assertThat(c, is(nullValue()));
		
	}

	@Ignore
	@Test
	public void testChangeAuthDependingOnConfiguration() throws Exception {
		
		XSystemIdentityCard devCard = new XSystemIdentityCard(SystemNames.SYSTEM_A, "1.0", ConfigurationEnum.DEV);
		Context devContext = getFarloccoContext();
		AbstractCommandHandler devCh = AnnotatedCommandHandlerFactory.create(SystemACommands.class, devContext, devCard);
		
		Command c = devCh.getByName(HttpMethod.POST.name() + "_" + SystemACommands.DEV_MUTANT_COMMAND);
		assertThat(c.execute(Fields.empty()), is(equalTo(Fields.single(GenericFieldNames.RESPONSE, new ArrayList<DomainEntity>()))));
		
		XSystemIdentityCard prodCard = new XSystemIdentityCard(SystemNames.SYSTEM_A, "1.0", ConfigurationEnum.PROD);
		Context prodContext = getFarloccoContext();
		AbstractCommandHandler prodCh = AnnotatedCommandHandlerFactory.create(SystemACommands.class, prodContext, prodCard);
		
		c = prodCh.getByName(HttpMethod.POST.name() + "_" + SystemACommands.PROD_MUTANT_COMMAND);
		boolean failed = false;
		try {
			c.execute(Fields.empty());
		} catch (Exception exception) {
			assertThat(exception, is(instanceOf(PrintLoginIdCommand.class)));
			failed = true;
		} finally {
			assertThat(failed, is(true));
		}
		
	}

	private Context getFarloccoContext() {
		Context context = new Context() {
			
			@Override
			public void setSystemProperty(String name, String value) {				
			}
			
			@Override
			public void setCommandHandler(AbstractCommandHandler commandHandler) {
			}
			
			@Override
			public void put(String name, Object value) {
			}
			
			@Override
			public String getSystemProperty(String name) {
				return null;
			}
			
			@Override
			public String getSystemName() {
				return "test";
			}
			
			@Override
			public <DE extends DomainEntity> Repository<DE> getRepository(String repoName) {				
				return null;
			}
			
			@Override
			public Logger getLogger() {
				return Logger.getRootLogger();
			}
			
			@Override
			public AbstractCommandHandler getCommandHandler() {
				return null;
			}
			
			@Override
			public <T> T get(String name) {
				return null;
			}
			
			@Override
			public void addRepository(String repoName, AbstractRepository<?> repo) {
			}

			@Override
			public void addSystemListener(SystemListener listener) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public List<SystemListener> getSystemListeners() {
				// TODO Auto-generated method stub
				return null;
			}
		};
		
		return context;
	}
	
	
}
