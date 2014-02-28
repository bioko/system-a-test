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

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.biokoframework.http.AbstractSystemServletInterfaceTest;
import org.biokoframework.system.KILL_ME.SystemNames;
import org.biokoframework.system.entity.authentication.Authentication;
import org.biokoframework.system.entity.binary.BinaryEntity;
import org.biokoframework.system.entity.login.Login;
import org.biokoframework.system.repository.sql.MySQLConnector;
import org.biokoframework.system.repository.sql.SqlConstants;
import org.biokoframework.systema.entity.dummy1.DummyEntity1;
import org.biokoframework.systema.entity.dummy2.DummyEntity2;
import org.biokoframework.systema.entity.dummy3.DummyEntity3;
import org.biokoframework.systema.entity.dummy6.DummyEntity6;
import org.biokoframework.systema.entity.dummyComplex.DummyComplexDomainEntity;
import org.biokoframework.systema.entity.dummyMultipart.DummyMultipart;
import org.biokoframework.systema.injection.SystemAServletConfig;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import com.google.inject.servlet.GuiceServletContextListener;

public class SystemATestAbstract extends AbstractSystemServletInterfaceTest {
	
	private String fLocalHostUrl;
	private String fLoginUrl;
	private static final List<String> TABLE_NAMES = Arrays.asList(
				Authentication.class.getSimpleName(),
				Login.class.getSimpleName(),
				DummyEntity6.class.getSimpleName(),
				DummyEntity3.class.getSimpleName(),
				DummyEntity2.class.getSimpleName(),
				DummyEntity1.class.getSimpleName(), 
				DummyComplexDomainEntity.class.getSimpleName(),
				BinaryEntity.class.getSimpleName(),
				DummyMultipart.class.getSimpleName());
	private String fEntity1url;
	private String fEntity2url;
	private String fEntity3url;

	public static final String LOGIN = Login.class.getSimpleName().toLowerCase() + "/";

	private static ArrayList<PreparedStatement> fTruncateTableStatements;
	private static Connection fConnection;
	
	@Before
	public void startServlet() throws Exception {
		dropTablesInDB();
		init();
		start();
		
		fLocalHostUrl = getURI() + "1.0/";
		fLoginUrl = fLocalHostUrl + LOGIN;
		
		fEntity1url = getLocalHostUrl() + "dummy-entity1/";
		fEntity2url = getLocalHostUrl() + "dummy-entity2/";
		fEntity3url = getLocalHostUrl() + "dummy-entity3/";
		
	}

	@After
	public void stopServlet() throws Exception {
		stop();
	}
	
	public String getLocalHostUrl() {
		return fLocalHostUrl;
	}
	
	public String getLoginUrl() {
		return fLoginUrl;
	}
	
	public String getEntity1Url() {
		return fEntity1url;
	}
	
	public String getEntity2Url() {
		return fEntity2url;
	}

	public static final boolean USE_DB = false;
	
	@BeforeClass
	public static void connectToDB() throws IOException, SQLException {
		if (USE_DB) {
			Properties properties = new Properties();
			properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("systemA.DEMO.properties"));
			String dbUrl = properties.getProperty(SqlConstants.DB_URL);
			String dbName = properties.getProperty(SqlConstants.DB_NAME, SystemNames.SYSTEM_A);
			String dbUser = properties.getProperty(SqlConstants.DB_USER);
			String dbPassword = properties.getProperty(SqlConstants.DB_PASSWORD);
			String dbPort = properties.getProperty(SqlConstants.DB_PORT);
			
			fConnection = new MySQLConnector(dbUrl, dbName, dbUser, dbPassword, dbPort).getConnection();
			fTruncateTableStatements = new ArrayList<PreparedStatement>();
			for (String aTableName : TABLE_NAMES) {
				fTruncateTableStatements.add(fConnection.prepareStatement("drop table " + aTableName + ";"));
			}
		}
	}

	@AfterClass
	public static void disconnectFromDB() throws SQLException {
		if (USE_DB) {
			for (PreparedStatement aStatement : fTruncateTableStatements) {
				aStatement.close();
			}
			fConnection.close();
		}
	}
	
	
	private void dropTablesInDB() {
		if (USE_DB) {
			for (PreparedStatement aStatement : fTruncateTableStatements) {
				try {
					aStatement.execute();
				} catch (Exception exception) {
					System.err.println("[easy men] failure in truncating tables");
				}
			}
		}
	}

	public String getEntity3Url() {
		return fEntity3url;
	}

	@Override
	protected GuiceServletContextListener getServletConfig() {
		return new SystemAServletConfig();
	}
	
}
