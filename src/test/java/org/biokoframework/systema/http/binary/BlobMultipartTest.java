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

package org.biokoframework.systema.http.binary;

import com.jayway.restassured.response.Response;
import org.biokoframework.http.scenario.JSonExpectedResponseBuilder;
import org.biokoframework.system.KILL_ME.commons.GenericFieldValues;
import org.biokoframework.systema.commons.SystemACommandNames;
import org.biokoframework.systema.entity.dummy1.DummyEntity1;
import org.biokoframework.systema.entity.dummy1.DummyEntity1Builder;
import org.biokoframework.systema.http.SystemATestAbstract;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;

import static com.jayway.restassured.RestAssured.expect;
import static org.biokoframework.utils.matcher.Matchers.equalToStream;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class BlobMultipartTest extends SystemATestAbstract {

	private static final String EXAMPLE_1 = "example.jpg";
	private static final URL EXAMPLE_1_URL = CrudBlobTest.class.getClassLoader().getResource(EXAMPLE_1);
	
	private static final String BLOB = "my-blob" + "/";
	private String _blobUrl;
	
	@Before
	public void createUrls() {
		_blobUrl = getLocalHostUrl() + BLOB;
	}
	
	@Test
	public void simpleTest() throws Exception {
		File file = new File(EXAMPLE_1_URL.toURI());
		
		DummyEntity1 entity1 = new DummyEntity1Builder().loadDefaultExample().build(true);
		
		expect().
		statusCode(200).
		given().
		when().
		multiPart(DummyEntity1.VALUE, entity1.get(DummyEntity1.VALUE)).
		multiPart("my-blob", file, GenericFieldValues.JPEG_CONTENT_TYPE).
		post(getLocalHostUrl() + SystemACommandNames.MULTIPART_MULTIPLE);
		
		Response response = 
		expect().
		statusCode(200).
		header("Content-Length", equalTo(Long.toString(file.length()))).
		contentType("image/jpeg").
		when().
		given().
		get(_blobUrl + "1");
		
		InputStream actualStream = response.getBody().asInputStream();
		
		assertThat(actualStream, is(equalToStream(new FileInputStream(file))));
		
		expect().
		statusCode(200).
		body(
				equalTo(JSonExpectedResponseBuilder.asArray(entity1.toJSONString()))
		).
		when().
		given().
		get(getEntity1Url() + "1");
	}
	
}
