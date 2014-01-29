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

import static com.jayway.restassured.RestAssured.expect;
import static org.biokoframework.utils.matcher.Matchers.matchesJSONString;

import org.biokoframework.systema.entity.dummy1.DummyEntity1;
import org.biokoframework.systema.entity.dummy1.DummyEntity1Builder;
import org.junit.Before;
import org.junit.Test;

public class EncodingTest extends SystemATestAbstract {

		
	static final byte[] UTF8_BYTES = new byte[] {
		(byte) 195, (byte) 169, // LATIN SMALL LETTER E WITH ACUTE
		(byte) 195, (byte) 184, // LATIN SMALL LETTER O WITH STROKE
		(byte) 195, (byte) 145,  // LATIN CAPITAL LETTER N WITH TILDE
	};
	
	static final byte[] ISO8859_1_BYTES = new byte[] {
		(byte) 233, // LATIN SMALL LETTER E WITH ACUTE
		(byte) 248,  // LATIN SMALL LETTER O WITH STROKE
		(byte) 209, // LATIN CAPITAL LETTER N WITH TILDE
	};
	
	static final byte[] ENTITY_1_UTF8_JSON_FIRST_PART = "{ \"value\":\"utf8 ".getBytes();
	static final byte[] ENTITY_1_ISO8859_1_JSON_FIRST_PART = "{ \"value\":\"iso8859-1 ".getBytes();
	static final byte[] ENTITY_1_JSON_SECOND_PART = "\" }".getBytes();

	byte[] _entity1utf8 = new byte[UTF8_BYTES.length + ENTITY_1_UTF8_JSON_FIRST_PART.length + ENTITY_1_JSON_SECOND_PART.length];
	byte[] _entity1latin1 = new byte[ISO8859_1_BYTES.length + ENTITY_1_ISO8859_1_JSON_FIRST_PART.length + ENTITY_1_JSON_SECOND_PART.length];
	 
	@Before
	public void buildByteSequences() {
		System.arraycopy(ENTITY_1_UTF8_JSON_FIRST_PART, 0, _entity1utf8, 0, ENTITY_1_UTF8_JSON_FIRST_PART.length);
		System.arraycopy(UTF8_BYTES, 0, _entity1utf8, ENTITY_1_UTF8_JSON_FIRST_PART.length, UTF8_BYTES.length);
		System.arraycopy(ENTITY_1_JSON_SECOND_PART, 0, 
				_entity1utf8,  ENTITY_1_UTF8_JSON_FIRST_PART.length + UTF8_BYTES.length,
				ENTITY_1_JSON_SECOND_PART.length);

		System.arraycopy(ENTITY_1_ISO8859_1_JSON_FIRST_PART, 0, _entity1latin1, 0, ENTITY_1_ISO8859_1_JSON_FIRST_PART.length);
		System.arraycopy(ISO8859_1_BYTES, 0, _entity1latin1, ENTITY_1_ISO8859_1_JSON_FIRST_PART.length, ISO8859_1_BYTES.length);
		System.arraycopy(ENTITY_1_JSON_SECOND_PART, 0, 
				_entity1latin1,  ENTITY_1_ISO8859_1_JSON_FIRST_PART.length + ISO8859_1_BYTES.length,
				ENTITY_1_JSON_SECOND_PART.length);
	}

	@Test
	public void entitiesWithDifferentEncoding() {
		
		expect().
		statusCode(200).
		given().
		content(
				_entity1latin1
		).
		header("Content-Type", "text/html; charset=iso8859-1").
		post(getEntity1Url());
		
		expect().
		statusCode(200).
		given().
		content(
				_entity1utf8
		).
		header("Content-Type", "text/html; charset=utf-8").
		post(getEntity1Url());
	}

	@Test
	public void entitiesWith3BytesUtf8() {
		DummyEntity1 dummyEntity1 = new DummyEntity1Builder().build("1");
		dummyEntity1.set(DummyEntity1.VALUE, "– …");

		expect().
		statusCode(200).
		body(matchesJSONString("[" + dummyEntity1.toJSONString() + "]")).
		given().
		param(DummyEntity1.VALUE, dummyEntity1.get(DummyEntity1.VALUE)).
		param("command", "POST_dummy-entity1").
		get(getLocalHostUrl() + "/only-get");
		
		expect().
		statusCode(200).
		body(matchesJSONString("[" + dummyEntity1.toJSONString() + "]")).
		given().
		get(getEntity1Url() + "1");
		
	}
		
}
