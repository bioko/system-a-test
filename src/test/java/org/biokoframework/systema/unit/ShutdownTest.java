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

import com.google.inject.Injector;

public class ShutdownTest {

	private Injector fInjector;

//	@Test
//	public void testShutdownListenerIsCalled() throws Exception {
//		XSystemIdentityCard identityCard = new XSystemIdentityCard(SystemNames.SYSTEM_A, "1.0", ConfigurationEnum.DEV);
//		
//		TestShutdownListener.triggered = false;
//		
//		XSystem theSystem = AnnotatedSystemFactory.createSystem(identityCard, new SystemAContextFactory(), SystemACommands.class, fInjector);
//		
//		// theSystem._context.addShutdownListener(new )
//		// Performed by the context factory
//		
//		theSystem.shutdown();
//			
//		assertThat(TestShutdownListener.triggered, is(true));
//	}
//
//	@Before
//	public void createInjector() {
//		fInjector = Guice.createInjector(
//				new SystemAMemRepoModule(),
//				new CurrentTimeModule(ConfigurationEnum.DEV),
//				new EmailModule(ConfigurationEnum.DEV),
//				new QueueModule(ConfigurationEnum.DEV),
//				new CronModule(ConfigurationEnum.DEV),
//				new RandomModule(ConfigurationEnum.DEV),
//				new AbstractModule() {
//					@Override
//					protected void configure() {
//						bindConstant().annotatedWith(Names.named("cronEmailAddress")).to("dummy@example.it");
//						bindConstant().annotatedWith(Names.named("noReplyEmailAddress")).to("dummy@example.it");
//					}
//				});
//	}
	
}
