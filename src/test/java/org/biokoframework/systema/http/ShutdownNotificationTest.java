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

package org.biokoframework.systema.http;

import org.biokoframework.systema.injection.SystemAServletConfig;
import org.biokoframework.systema.misc.TestShutdownListener;
import org.junit.Ignore;
import org.junit.Test;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@Ignore("not yet required")
public class ShutdownNotificationTest extends SystemATestAbstract {
	
	@Override
	public void startServlet() throws Exception {
		// NOP
	}
	
	@Override
	public void stopServlet() throws Exception {
		// NOP
	}
	
	@Test
	public void shutdownNotificationTest() throws Exception {
		
		TestShutdownListener.triggered = false;
		
		// START SERVLET
		super.startServlet();

		String aCommandUrl = getLocalHostUrl() + "dummy-entity1/";
		
		
		// theSystem._context.addSystemListener(new Test)
		// Performed by the context factory

		// Set up the system
		given().
		content("").
		header("Content-Type", "text/html; charset=iso8859-1").
		post(aCommandUrl);

		super.stopServlet();
		
		// theSystem._context.getSystemListeners().systemShuttedDown();
		// Performed by the servlet
		
		assertThat(TestShutdownListener.triggered, is(true));
		
	}
	
	@Override
	protected SystemAServletConfig getServletConfig() {
		return new SystemAServletConfig();
	}
	
	
}
