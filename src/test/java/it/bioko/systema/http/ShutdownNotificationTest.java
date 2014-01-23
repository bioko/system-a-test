package it.bioko.systema.http;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import it.bioko.http.rest.WebAppTest;
import it.bioko.systema.injection.SystemAServletConfig;
import it.bioko.systema.misc.TestShutdownListener;

import org.junit.Test;

public class ShutdownNotificationTest extends WebAppTest {
	
	private static final String VERSION = "1.0" + "/";
	private static final String SYSTEM_A = "systemA" + "/" + VERSION;
	
	@Test
	public void shutdownNotificationTest() throws Exception {
		
		TestShutdownListener.triggered = false;
		
		// START SERVLET
		init();
		start();

		String aCommandUrl = "http://localhost:" + getPort() + "/engagedServer/api/" + SYSTEM_A + "dummy-entity1/";
		
		
		// theSystem._context.addSystemListener(new Test)
		// Performed by the context factory

		// Set up the system
		given().
		content("").
		header("Content-Type", "text/html; charset=iso8859-1").
		post(aCommandUrl);

		stop();
		
		// theSystem._context.getSystemListeners().systemShuttedDown();
		// Performed by the servlet
		
		assertThat(TestShutdownListener.triggered, is(true));
		
	}
	
	@Override
	protected SystemAServletConfig getServletConfig() {
		return new SystemAServletConfig();
	}
	
	
}
