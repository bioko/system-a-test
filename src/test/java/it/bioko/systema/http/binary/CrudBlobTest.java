package it.bioko.systema.http.binary;

import static com.jayway.restassured.RestAssured.expect;
import static it.bioko.utils.matcher.Matchers.equalToStream;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import it.bioko.http.rest.exception.HttpError;
import it.bioko.http.rest.exception.HttpResponseBuilder;
import it.bioko.http.rest.exception.HttpResponseExceptionFactory;
import it.bioko.system.KILL_ME.commons.GenericFieldValues;
import it.bioko.system.entity.binary.BinaryEntity;
import it.bioko.system.exceptions.CommandExceptionsFactory;
import it.bioko.systema.http.SystemATestAbstract;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;

import org.json.simple.JSONValue;
import org.junit.Before;
import org.junit.Test;

import com.jayway.restassured.response.Response;

public class CrudBlobTest extends SystemATestAbstract {

	private static final String EXAMPLE_1 = "example.jpg";
	private static final URL EXAMPLE_1_URL = CrudBlobTest.class.getClassLoader().getResource(EXAMPLE_1);

	private static final String EXAMPLE_2 = "example2.png";
	private static final URL EXAMPLE_2_URL = CrudBlobTest.class.getClassLoader().getResource(EXAMPLE_2);
	
	private static final String BLOB = "my-blob" + "/";
	private String _blobUrl;
	
	@Before
	public void createUrls() {
		_blobUrl = getSystemAUrl() + BLOB;
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
		HttpError httpError = httpResponseBuilder.buildFrom(CommandExceptionsFactory.createEntityNotFound(BinaryEntity.class.getSimpleName(), "1"));
		expect().
		statusCode(httpError.status()).
		body(equalTo(JSONValue.toJSONString(httpError.body()))).
		get(_blobUrl + "1");
	}
	
	@Test
	public void getFailed() throws Exception {
		HttpResponseBuilder httpResponseBuilder = new HttpResponseBuilder(HttpResponseExceptionFactory.create());
		HttpError httpError = httpResponseBuilder.buildFrom(CommandExceptionsFactory.createEntityNotFound(BinaryEntity.class.getSimpleName(), "1"));
		expect().
		statusCode(httpError.status()).
		body(equalTo(JSONValue.toJSONString(httpError.body()))).
		get(_blobUrl + "1");
	}
	
	@Test
	public void headFailed() throws Exception {
		HttpResponseBuilder httpResponseBuilder = new HttpResponseBuilder(HttpResponseExceptionFactory.create());
		HttpError httpError = httpResponseBuilder.buildFrom(CommandExceptionsFactory.createEntityNotFound(BinaryEntity.class.getSimpleName(), "1"));
		expect().
		statusCode(httpError.status()).
		head(_blobUrl + "1");
	}
	
	@Test
	public void deleteFailed() throws Exception {
		HttpResponseBuilder httpResponseBuilder = new HttpResponseBuilder(HttpResponseExceptionFactory.create());
		HttpError httpError = httpResponseBuilder.buildFrom(CommandExceptionsFactory.createEntityNotFound(BinaryEntity.class.getSimpleName(), "1"));
		expect().
		statusCode(httpError.status()).
		body(equalTo(JSONValue.toJSONString(httpError.body()))).
		delete(_blobUrl + "1");
	}
	
	@Test
	public void putFailed() throws Exception {
		HttpResponseBuilder httpResponseBuilder = new HttpResponseBuilder(HttpResponseExceptionFactory.create());
		HttpError httpError = httpResponseBuilder.buildFrom(CommandExceptionsFactory.createEntityNotFound(BinaryEntity.class.getSimpleName(), "1"));
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
		HttpError httpError = httpResponseBuilder.buildFrom(CommandExceptionsFactory.createEntityNotFound(BinaryEntity.class.getSimpleName(), "1"));
		expect().
		statusCode(httpError.status()).
		body(equalTo(JSONValue.toJSONString(httpError.body()))).
		get(_blobUrl);
	}
}
