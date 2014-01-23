package it.engaged.framework.systemA.unit;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import it.bioko.system.ConfigurationEnum;
import it.bioko.system.KILL_ME.SystemNames;
import it.bioko.system.KILL_ME.XSystem;
import it.bioko.system.KILL_ME.XSystemIdentityCard;
import it.bioko.system.factory.AnnotatedSystemFactory;
import it.bioko.systema.factory.SystemACommands;
import it.bioko.systema.factory.SystemAContextFactory;
import it.bioko.systema.misc.TestShutdownListener;

import org.junit.Test;

public class ShutdownTest {

	@Test
	public void testShutdownListenerIsCalled() throws Exception {
		XSystemIdentityCard identityCard = new XSystemIdentityCard(SystemNames.SYSTEM_A, "1.0", ConfigurationEnum.DEV);
		
		TestShutdownListener.triggered = false;
		
		XSystem theSystem = AnnotatedSystemFactory.createSystem(identityCard, new SystemAContextFactory(), SystemACommands.class);
		
		// theSystem._context.addShutdownListener(new )
		// Performed by the context factory
		
		theSystem.shutdown();
			
		assertThat(TestShutdownListener.triggered, is(true));
	}
	
}
