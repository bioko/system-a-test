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
import org.biokoframework.http.rest.exception.HttpError;
import org.biokoframework.http.rest.exception.HttpResponseBuilder;
import org.biokoframework.http.rest.exception.HttpResponseExceptionFactory;
import org.biokoframework.system.KILL_ME.commons.GenericFieldValues;
import org.biokoframework.system.entity.binary.BinaryEntity;
import org.biokoframework.system.exceptions.CommandExceptionsFactory;
import org.biokoframework.systema.http.SystemATestAbstract;
import org.json.simple.JSONValue;
import org.junit.Before;
import org.junit.Ignore;
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

@Ignore("not yet required")
public class CrudBlobTest extends SystemATestAbstract {

	private static final String EXAMPLE_1 = "example.jpg";
	private static final URL EXAMPLE_1_URL = CrudBlobTest.class.getClassLoader().getResource(EXAMPLE_1);

	private static final String EXAMPLE_2 = "example2.png";
	private static final URL EXAMPLE_2_URL = CrudBlobTest.class.getClassLoader().getResource(EXAMPLE_2);
	
	private static final String BLOB = "my-blob" + "/";
	private String _blobUrl;
	
	@Before
	public void createUrls() {
		_blobUrl = getLocalHostUrl() + BLOB;
	}
	
	@Test
	public void postAndGetSuccessful() throws Exception {
		File file = new File(EXAMPLE_1_URL.toURI());
		
		expect().
		statusCode(200).
		when().
		given().
		multiPart("my-blob", file, GenericFieldValues.JPEG_CONTENT_TYPE).
		post(_blobUrl);
		
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
	}
	
	@Test
	public void postAndHeadSuccessful() throws Exception {
		File file = new File(EXAMPLE_1_URL.toURI());

		System.out.println("POST");
		
		expect().
		statusCode(200).
		when().
		given().
		multiPart("my-blob", file, GenericFieldValues.JPEG_CONTENT_TYPE).
		post(_blobUrl);

		System.out.println("HEAD");
		
		expect().
		statusCode(200).
		header("Content-Length", equalTo(Long.toString(file.length()))).
		header("Content-Type", equalTo("image/jpeg")).
		when().
		given().
		head(_blobUrl + "1");
	}
	
	@Test
	public void postPutAndGetSuccessful() throws Exception {
		File file = new File(EXAMPLE_1_URL.toURI());
		
		expect().
		statusCode(200).
		when().
		given().
		multiPart("my-blob", file, GenericFieldValues.JPEG_CONTENT_TYPE).
		post(_blobUrl);
		
		File updatedFile = new File(EXAMPLE_2_URL.toURI());
		
		expect().
		statusCode(200).
		when().
		given().
		multiPart("my-blob", updatedFile, GenericFieldValues.PNG_CONTENT_TYPE).
		put(_blobUrl + "1");
		
		Response response = 
		expect().
		statusCode(200).
		contentType("image/png").
		when().
		given().
		get(_blobUrl + "1");
		
		InputStream actualStream = response.getBody().asInputStream();
		
		assertThat(actualStream, is(equalToStream(new FileInputStream(updatedFile))));
	}
	
	@Test
	public void postDeleteAndGetSuccessful() throws Exception {
		File file = new File(EXAMPLE_1_URL.toURI());
		
		expect().
		statusCode(200).
		when().
		given().
		multiPart("my-blob", file, GenericFieldValues.JPEG_CONTENT_TYPE).
		post(_blobUrl);
		
		expect().
		statusCode(200).
		when().
		given().
		delete(_blobUrl + "1");
		
		HttpResponseBuilder httpResponseBuilder = new HttpResponseBuilder(HttpResponseExceptionFactory.create());
		HttpError httpError = httpResponseBuilder.buildFrom(CommandExceptionsFactory.createEntityNotFound(BinaryEntity.class, "1"));
		expect().
		statusCode(httpError.status()).
		body(equalTo(JSONValue.toJSONString(httpError.body()))).
		get(_blobUrl + "1");
	}
	
	@Test
	public void getFailed() throws Exception {
		HttpResponseBuilder httpResponseBuilder = new HttpResponseBuilder(HttpResponseExceptionFactory.create());
		HttpError httpError = httpResponseBuilder.buildFrom(CommandExceptionsFactory.createEntityNotFound(BinaryEntity.class, "1"));
		expect().
		statusCode(httpError.status()).
		body(equalTo(JSONValue.toJSONString(httpError.body()))).
		get(_blobUrl + "1");
	}
	
	@Test
	public void headFailed() throws Exception {
		HttpResponseBuilder httpResponseBuilder = new HttpResponseBuilder(HttpResponseExceptionFactory.create());
		HttpError httpError = httpResponseBuilder.buildFrom(CommandExceptionsFactory.createEntityNotFound(BinaryEntity.class, "1"));
		expect().
		statusCode(httpError.status()).
		head(_blobUrl + "1");
	}
	
	@Test
	public void deleteFailed() throws Exception {
		HttpResponseBuilder httpResponseBuilder = new HttpResponseBuilder(HttpResponseExceptionFactory.create());
		HttpError httpError = httpResponseBuilder.buildFrom(CommandExceptionsFactory.createEntityNotFound(BinaryEntity.class, "1"));
		expect().
		statusCode(httpError.status()).
		body(equalTo(JSONValue.toJSONString(httpError.body()))).
		delete(_blobUrl + "1");
	}
	
	@Test
	public void putFailed() throws Exception {
		HttpResponseBuilder httpResponseBuilder = new HttpResponseBuilder(HttpResponseExceptionFactory.create());
		HttpError httpError = httpResponseBuilder.buildFrom(CommandExceptionsFactory.createEntityNotFound(BinaryEntity.class, "1"));
		expect().
		statusCode(httpError.status()).
		body(equalTo(JSONValue.toJSONString(httpError.body()))).
		put(_blobUrl + "1");
	}
	
	public void postPostAndGetFailed() throws Exception {
		File file = new File(EXAMPLE_1_URL.toURI());
		
		expect().
		statusCode(200).
		when().
		given().
		multiPart("my-blob", file, GenericFieldValues.JPEG_CONTENT_TYPE).
		post(_blobUrl);
		
		File anOtherFile = new File(EXAMPLE_2_URL.toURI());
		
		expect().
		statusCode(200).
		when().
		given().
		multiPart("my-blob", anOtherFile, GenericFieldValues.PNG_CONTENT_TYPE).
		post(_blobUrl);
		
		HttpResponseBuilder httpResponseBuilder = new HttpResponseBuilder(HttpResponseExceptionFactory.create());
		HttpError httpError = httpResponseBuilder.buildFrom(CommandExceptionsFactory.createEntityNotFound(BinaryEntity.class, "1"));
		expect().
		statusCode(httpError.status()).
		body(equalTo(JSONValue.toJSONString(httpError.body()))).
		get(_blobUrl);
	}
}
