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

package it.bioko.systema.http;

import it.bioko.http.AbstractSystemServletInterfaceTest;
import it.bioko.system.KILL_ME.SystemNames;
import it.bioko.system.entity.authentication.Authentication;
import it.bioko.system.entity.binary.BinaryEntity;
import it.bioko.system.entity.login.Login;
import it.bioko.system.repository.sql.MySQLConnector;
import it.bioko.system.repository.sql.SqlConstants;
import it.bioko.systema.entity.dummy1.DummyEntity1;
import it.bioko.systema.entity.dummy2.DummyEntity2;
import it.bioko.systema.entity.dummy3.DummyEntity3;
import it.bioko.systema.entity.dummy6.DummyEntity6;
import it.bioko.systema.entity.dummyComplex.DummyComplexDomainEntity;
import it.bioko.systema.entity.dummyMultipart.DummyMultipart;
import it.bioko.systema.injection.SystemAServletConfig;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import com.google.inject.servlet.GuiceServletContextListener;

public class SystemATestAbstract extends AbstractSystemServletInterfaceTest {
	
	private static final String VERSION = "1.0" + "/";
	private static final String SYSTEM_A = "systemA" + "/" + VERSION;
	
	private String _localHostUrl;
	private String _systemAUrl;
	private String _loginUrl;
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
	private String _entity1url;
	private String _entity2url;
	private String _entity3url;

	public static final String LOGIN = Login.class.getSimpleName().toLowerCase() + "/";

	private static ArrayList<PreparedStatement> _truncateTableStatements;
	private static Connection _connection;
	
	@Before
	public void startServlet() throws Exception {
		dropTablesInDB();
		init();
		start();
		
		 //_localHostUrl = "http://localhost:" + "8080" + "/engagedServer/api/";
		_localHostUrl = "http://localhost:" + getPort();
		_systemAUrl = _localHostUrl + "/system-a/1.0/";
		_loginUrl = _systemAUrl + LOGIN;
		
		_entity1url = getSystemAUrl() + "dummy-entity1/";
		_entity2url = getSystemAUrl() + "dummy-entity2/";
		_entity3url = getSystemAUrl() + "dummy-entity3/";
		
	}

	@After
	public void stopServlet() throws Exception {
		stop();
	}
	
	public String getLocalHostUrl() {
		return _localHostUrl;
	}
	
	public String getSystemAUrl() {
		return _systemAUrl;
	}
	
	public String getLoginUrl() {
		return _loginUrl;
	}
	
	public String getEntity1Url() {
		return _entity1url;
	}
	
	public String getEntity2Url() {
		return _entity2url;
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
			
			_connection = new MySQLConnector(dbUrl, dbName, dbUser, dbPassword, dbPort).getConnection();
			_truncateTableStatements = new ArrayList<PreparedStatement>();
			for (String aTableName : TABLE_NAMES) {
				_truncateTableStatements.add(_connection.prepareStatement("drop table " + aTableName + ";"));
			}
		}
	}

	@AfterClass
	public static void disconnectFromDB() throws SQLException {
		if (USE_DB) {
			for (PreparedStatement aStatement : _truncateTableStatements) {
				aStatement.close();
			}
			_connection.close();
		}
	}
	
	
	private void dropTablesInDB() {
		if (USE_DB) {
			for (PreparedStatement aStatement : _truncateTableStatements) {
				try {
					aStatement.execute();
				} catch (Exception exception) {
					System.err.println("[easy men] failure in truncating tables");
				}
			}
		}
	}

	public String getEntity3Url() {
		return _entity3url;
	}

	@Override
	protected GuiceServletContextListener getServletConfig() {
		return new SystemAServletConfig();
	}
	
}
