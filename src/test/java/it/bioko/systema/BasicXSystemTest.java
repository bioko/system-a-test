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

package it.bioko.systema;

import static org.junit.Assert.assertEquals;
import it.bioko.system.KILL_ME.XSystem;
import it.bioko.system.KILL_ME.commons.GenericFieldNames;
import it.bioko.system.KILL_ME.exception.SystemException;
import it.bioko.system.command.CommandException;
import it.bioko.system.entity.login.Login;
import it.bioko.system.entity.login.LoginBuilder;
import it.bioko.utils.domain.EntityBuilder;
import it.bioko.utils.fields.FieldNames;
import it.bioko.utils.fields.Fields;

import org.json.simple.JSONValue;
import org.junit.Ignore;
import org.junit.Test;

@Ignore("Probabilmente sono cadaveri!")
public class BasicXSystemTest {

	private XSystem _system;

//	@Before
//	public void setUp() throws Exception {
//		XSystemIdentityCard promoCard = new XSystemIdentityCard(SystemNames.BUONOBOX, "1.0", ConfigurationEnum.DEV);
//		_system = new PromoSystemFactory().createSystem(promoCard);
//	}
	
	@Test
	public void twoWorkingCommands() throws SystemException {
		EntityBuilder<Login> login = new LoginBuilder().loadDefaultExample();
		login.setId("1");
		
		Fields input = Fields.empty();
		input.put(FieldNames.COMMAND_NAME, "POST_login");
		input.putAll(login.build(false).fields());
		
		Fields output = _system.execute(input);
		
		Fields input2 = Fields.empty();
		input2.put(FieldNames.COMMAND_NAME, "GET_login");
		input2.put(GenericFieldNames.USER_EMAIL, "matto");
		input2.put(GenericFieldNames.PASSWORD, "fatto");
		
		output = _system.execute(input2);
		
		assertEquals("[" + login.build(true).toJSONString() + "]", 
				JSONValue.toJSONString(output.valueFor(GenericFieldNames.RESPONSE)));
	}

	@Test(expected = CommandException.class)
	public void missingCommandName() throws SystemException {
		Fields input = Fields.empty();
		input.put(FieldNames.NAME, FieldNames.NAME_VALUE);
		
		
		_system.execute(input);
	}
	
}
