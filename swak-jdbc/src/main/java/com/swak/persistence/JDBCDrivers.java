package com.swak.persistence;

import java.sql.SQLException;

import com.alibaba.druid.util.JdbcConstants;
import com.swak.persistence.dialect.Dialect;
import com.swak.persistence.dialect.H2Dialect;
import com.swak.persistence.dialect.MsSQLDialect;
import com.swak.persistence.dialect.MySQLDialect;
import com.swak.persistence.dialect.OracleDialect;
import com.swak.persistence.dialect.SqlLiteDialect;

/**
 * JDBCDrivers 驱动
 * 
 * @author lifeng
 * @date 2020年11月2日 下午9:15:36
 */
public class JDBCDrivers {

	private static Boolean mysql_driver_version_6 = null;

	static String JTDS = "jtds";

	static String MOCK = "mock";

	static String HSQL = "hsql";

	static String DB2 = "db2";

	static String DB2_DRIVER = "com.ibm.db2.jcc.DB2Driver"; // Type4
	static String DB2_DRIVER2 = "COM.ibm.db2.jdbc.app.DB2Driver"; // Type2
	static String DB2_DRIVER3 = "COM.ibm.db2.jdbc.net.DB2Driver"; // Type3

	static String POSTGRESQL = "postgresql";
	static String POSTGRESQL_DRIVER = "org.postgresql.Driver";

	static String SYBASE = "sybase";

	static String SQL_SERVER = "sqlserver";
	static String SQL_SERVER_DRIVER = "com.microsoft.jdbc.sqlserver.SQLServerDriver";
	static String SQL_SERVER_DRIVER_SQLJDBC4 = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	static String SQL_SERVER_DRIVER_JTDS = "net.sourceforge.jtds.jdbc.Driver";

	static String ORACLE = "oracle";
	static String ORACLE_DRIVER = "oracle.jdbc.OracleDriver";
	static String ORACLE_DRIVER2 = "oracle.jdbc.driver.OracleDriver";

	static String ALI_ORACLE = "AliOracle";
	static String ALI_ORACLE_DRIVER = "com.alibaba.jdbc.AlibabaDriver";

	static String MYSQL = "mysql";
	static String MYSQL_DRIVER = "com.mysql.jdbc.Driver";
	static String MYSQL_DRIVER_6 = "com.mysql.cj.jdbc.Driver";
	static String MYSQL_DRIVER_REPLICATE = "com.mysql.jdbc.";

	static String MARIADB = "mariadb";
	static String MARIADB_DRIVER = "org.mariadb.jdbc.Driver";

	static String DERBY = "derby";

	static String HBASE = "hbase";

	static String HIVE = "hive";
	static String HIVE_DRIVER = "org.apache.hive.jdbc.HiveDriver";

	static String H2 = "h2";
	static String H2_DRIVER = "org.h2.Driver";

	static String DM = "dm";
	static String DM_DRIVER = "dm.jdbc.driver.DmDriver";

	static String KINGBASE = "kingbase";
	static String KINGBASE_DRIVER = "com.kingbase.Driver";

	static String GBASE = "gbase";
	static String GBASE_DRIVER = "com.gbase.jdbc.Driver";

	static String XUGU = "xugu";
	static String XUGU_DRIVER = "com.xugu.cloudjdbc.Driver";

	static String OCEANBASE = "oceanbase";
	static String OCEANBASE_ORACLE = "oceanbase_oracle";
	static String OCEANBASE_DRIVER = "com.alipay.oceanbase.jdbc.Driver";

	static String INFORMIX = "informix";

	/**
	 * 阿里云odps
	 */
	static String ODPS = "odps";
	static String ODPS_DRIVER = "com.aliyun.odps.jdbc.OdpsDriver";

	static String TERADATA = "teradata";
	static String TERADATA_DRIVER = "com.teradata.jdbc.TeraDriver";

	/**
	 * Log4JDBC
	 */
	static String LOG4JDBC = "log4jdbc";
	static String LOG4JDBC_DRIVER = "net.sf.log4jdbc.DriverSpy";

	static String PHOENIX = "phoenix";
	static String PHOENIX_DRIVER = "org.apache.phoenix.jdbc.PhoenixDriver";
	static String ENTERPRISEDB = "edb";
	static String ENTERPRISEDB_DRIVER = "com.edb.Driver";

	static String KYLIN = "kylin";
	static String KYLIN_DRIVER = "org.apache.kylin.jdbc.Driver";

	static String SQLITE = "sqlite";
	static String SQLITE_DRIVER = "org.sqlite.JDBC";

	static String ALIYUN_ADS = "aliyun_ads";
	static String ALIYUN_DRDS = "aliyun_drds";

	static String PRESTO = "presto";
	static String PRESTO_DRIVER = "com.facebook.presto.jdbc.PrestoDriver";

	static String ELASTIC_SEARCH = "elastic_search";

	static String ELASTIC_SEARCH_DRIVER = "com.alibaba.xdriver.elastic.jdbc.ElasticDriver";

	static String CLICKHOUSE = "clickhouse";
	static String CLICKHOUSE_DRIVER = "ru.yandex.clickhouse.ClickHouseDriver";

	static String KDB = "kdb";
	static String KDB_DRIVER = "com.inspur.jdbc.KdDriver";

	static String POLARDB = "polardb";
	static String POLARDB_DRIVER = "com.aliyun.polardb.Driver";

	static String METRICS = "metrics";
	static String METRICS_DRIVER = "com.swak.persistence.metrics.MetricsDriver";

	/**
	 * 加载驱动类
	 * 
	 * @param className
	 * @return
	 */
	public static Class<?> loadDriver(String className) {
		Class<?> clazz = null;

		if (className == null) {
			return null;
		}

		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			// skip
		}

		ClassLoader ctxClassLoader = Thread.currentThread().getContextClassLoader();
		if (ctxClassLoader != null) {
			try {
				clazz = ctxClassLoader.loadClass(className);
			} catch (ClassNotFoundException e) {
				// skip
			}
		}

		return clazz;
	}

	/**
	 * 获得方言
	 * 
	 * @param rawUrl
	 * @param driverClassName
	 * @return
	 */
	public static Dialect getDialect(String rawUrl, String driverClassName) {
		String type = getDbType(rawUrl, driverClassName);
		if (H2.equals(type)) {
			return new H2Dialect();
		}
		if (MYSQL.equals(type)) {
			return new MySQLDialect();
		}
		if (SQL_SERVER.equals(type)) {
			return new MsSQLDialect();
		}
		if (ORACLE.equals(type)) {
			return new OracleDialect();
		}
		if (SQLITE.equals(type)) {
			return new SqlLiteDialect();
		}
		throw new RuntimeException("unknown jdbc driver : " + rawUrl);
	}

	/**
	 * 数据库类型
	 * 
	 * @param rawUrl
	 * @param driverClassName
	 * @return
	 */
	public static String getDbType(String rawUrl, String driverClassName) {
		if (rawUrl.startsWith("jdbc:derby:") || rawUrl.startsWith("jdbc:log4jdbc:derby:")) {
			return DERBY;
		} else if (rawUrl.startsWith("jdbc:mysql:") || rawUrl.startsWith("jdbc:cobar:")
				|| rawUrl.startsWith("jdbc:log4jdbc:mysql:")) {
			return MYSQL;
		} else if (rawUrl.startsWith("jdbc:mariadb:")) {
			return MARIADB;
		} else if (rawUrl.startsWith("jdbc:oracle:") || rawUrl.startsWith("jdbc:log4jdbc:oracle:")) {
			return ORACLE;
		} else if (rawUrl.startsWith("jdbc:alibaba:oracle:")) {
			return ALI_ORACLE;
		} else if (rawUrl.startsWith("jdbc:oceanbase:")) {
			return OCEANBASE;
		} else if (rawUrl.startsWith("jdbc:oceanbase:oracle:")) {
			return OCEANBASE_ORACLE;
		} else if (rawUrl.startsWith("jdbc:microsoft:") || rawUrl.startsWith("jdbc:log4jdbc:microsoft:")) {
			return SQL_SERVER;
		} else if (rawUrl.startsWith("jdbc:sqlserver:") || rawUrl.startsWith("jdbc:log4jdbc:sqlserver:")) {
			return SQL_SERVER;
		} else if (rawUrl.startsWith("jdbc:sybase:Tds:") || rawUrl.startsWith("jdbc:log4jdbc:sybase:")) {
			return SYBASE;
		} else if (rawUrl.startsWith("jdbc:jtds:") || rawUrl.startsWith("jdbc:log4jdbc:jtds:")) {
			return JTDS;
		} else if (rawUrl.startsWith("jdbc:fake:") || rawUrl.startsWith("jdbc:mock:")) {
			return MOCK;
		} else if (rawUrl.startsWith("jdbc:postgresql:") || rawUrl.startsWith("jdbc:log4jdbc:postgresql:")) {
			return POSTGRESQL;
		} else if (rawUrl.startsWith("jdbc:edb:")) {
			return ENTERPRISEDB;
		} else if (rawUrl.startsWith("jdbc:hsqldb:") || rawUrl.startsWith("jdbc:log4jdbc:hsqldb:")) {
			return HSQL;
		} else if (rawUrl.startsWith("jdbc:odps:")) {
			return ODPS;
		} else if (rawUrl.startsWith("jdbc:db2:")) {
			return DB2;
		} else if (rawUrl.startsWith("jdbc:sqlite:")) {
			return SQLITE;
		} else if (rawUrl.startsWith("jdbc:ingres:")) {
			return "ingres";
		} else if (rawUrl.startsWith("jdbc:h2:") || rawUrl.startsWith("jdbc:log4jdbc:h2:")) {
			return H2;
		} else if (rawUrl.startsWith("jdbc:mckoi:")) {
			return "mckoi";
		} else if (rawUrl.startsWith("jdbc:cloudscape:")) {
			return "cloudscape";
		} else if (rawUrl.startsWith("jdbc:informix-sqli:") || rawUrl.startsWith("jdbc:log4jdbc:informix-sqli:")) {
			return "informix";
		} else if (rawUrl.startsWith("jdbc:timesten:")) {
			return "timesten";
		} else if (rawUrl.startsWith("jdbc:as400:")) {
			return "as400";
		} else if (rawUrl.startsWith("jdbc:sapdb:")) {
			return "sapdb";
		} else if (rawUrl.startsWith("jdbc:JSQLConnect:")) {
			return "JSQLConnect";
		} else if (rawUrl.startsWith("jdbc:JTurbo:")) {
			return "JTurbo";
		} else if (rawUrl.startsWith("jdbc:firebirdsql:")) {
			return "firebirdsql";
		} else if (rawUrl.startsWith("jdbc:interbase:")) {
			return "interbase";
		} else if (rawUrl.startsWith("jdbc:pointbase:")) {
			return "pointbase";
		} else if (rawUrl.startsWith("jdbc:edbc:")) {
			return "edbc";
		} else if (rawUrl.startsWith("jdbc:mimer:multi1:")) {
			return "mimer";
		} else if (rawUrl.startsWith("jdbc:dm:")) {
			return JdbcConstants.DM;
		} else if (rawUrl.startsWith("jdbc:kingbase:")) {
			return JdbcConstants.KINGBASE;
		} else if (rawUrl.startsWith("jdbc:gbase:")) {
			return JdbcConstants.GBASE;
		} else if (rawUrl.startsWith("jdbc:xugu:")) {
			return JdbcConstants.XUGU;
		} else if (rawUrl.startsWith("jdbc:log4jdbc:")) {
			return LOG4JDBC;
		} else if (rawUrl.startsWith("jdbc:hive:")) {
			return HIVE;
		} else if (rawUrl.startsWith("jdbc:hive2:")) {
			return HIVE;
		} else if (rawUrl.startsWith("jdbc:phoenix:")) {
			return PHOENIX;
		} else if (rawUrl.startsWith("jdbc:elastic:")) {
			return ELASTIC_SEARCH;
		} else if (rawUrl.startsWith("jdbc:clickhouse:")) {
			return CLICKHOUSE;
		} else if (rawUrl.startsWith("jdbc:presto:")) {
			return PRESTO;
		} else if (rawUrl.startsWith("jdbc:inspur:")) {
			return JdbcConstants.KDB;
		} else if (rawUrl.startsWith("jdbc:polardb")) {
			return POLARDB;
		} else {
			throw new RuntimeException("unknown jdbc driver : " + rawUrl);
		}
	}

	/**
	 * 解析驱动
	 * 
	 * @param rawUrl
	 * @return
	 * @throws SQLException
	 */
	public static String getDriverClassName(String rawUrl) {
		if (rawUrl.startsWith("jdbc:derby:")) {
			return "org.apache.derby.jdbc.EmbeddedDriver";
		} else if (rawUrl.startsWith("jdbc:mysql:")) {
			if (mysql_driver_version_6 == null) {
				mysql_driver_version_6 = loadDriver("com.mysql.cj.jdbc.Driver") != null;
			}

			if (mysql_driver_version_6) {
				return MYSQL_DRIVER_6;
			} else {
				return MYSQL_DRIVER;
			}
		} else if (rawUrl.startsWith("jdbc:log4jdbc:")) {
			return LOG4JDBC_DRIVER;
		} else if (rawUrl.startsWith("jdbc:mariadb:")) {
			return MARIADB_DRIVER;
		} else if (rawUrl.startsWith("jdbc:oracle:") //
				|| rawUrl.startsWith("JDBC:oracle:")) {
			return ORACLE_DRIVER;
		} else if (rawUrl.startsWith("jdbc:alibaba:oracle:")) {
			return ALI_ORACLE_DRIVER;
		} else if (rawUrl.startsWith("jdbc:oceanbase:")) {
			return OCEANBASE_DRIVER;
		} else if (rawUrl.startsWith("jdbc:microsoft:")) {
			return "com.microsoft.jdbc.sqlserver.SQLServerDriver";
		} else if (rawUrl.startsWith("jdbc:sqlserver:")) {
			return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
		} else if (rawUrl.startsWith("jdbc:sybase:Tds:")) {
			return "com.sybase.jdbc2.jdbc.SybDriver";
		} else if (rawUrl.startsWith("jdbc:jtds:")) {
			return "net.sourceforge.jtds.jdbc.Driver";
		} else if (rawUrl.startsWith("jdbc:fake:") || rawUrl.startsWith("jdbc:mock:")) {
			return "com.alibaba.druid.mock.MockDriver";
		} else if (rawUrl.startsWith("jdbc:postgresql:")) {
			return POSTGRESQL_DRIVER;
		} else if (rawUrl.startsWith("jdbc:edb:")) {
			return ENTERPRISEDB_DRIVER;
		} else if (rawUrl.startsWith("jdbc:odps:")) {
			return ODPS_DRIVER;
		} else if (rawUrl.startsWith("jdbc:hsqldb:")) {
			return "org.hsqldb.jdbcDriver";
		} else if (rawUrl.startsWith("jdbc:db2:")) {
			// Resolve the DB2 driver from JDBC URL
			// Type2 COM.ibm.db2.jdbc.app.DB2Driver, url = jdbc:db2:databasename
			// Type3 COM.ibm.db2.jdbc.net.DB2Driver, url =
			// jdbc:db2:ServerIP:6789:databasename
			// Type4 8.1+ com.ibm.db2.jcc.DB2Driver, url =
			// jdbc:db2://ServerIP:50000/databasename
			String prefix = "jdbc:db2:";
			if (rawUrl.startsWith(prefix + "//")) { // Type4
				return DB2_DRIVER; // "com.ibm.db2.jcc.DB2Driver";
			} else {
				String suffix = rawUrl.substring(prefix.length());
				if (suffix.indexOf(':') > 0) { // Type3
					return DB2_DRIVER3; // COM.ibm.db2.jdbc.net.DB2Driver
				} else { // Type2
					return DB2_DRIVER2; // COM.ibm.db2.jdbc.app.DB2Driver
				}
			}
		} else if (rawUrl.startsWith("jdbc:sqlite:")) {
			return SQLITE_DRIVER;
		} else if (rawUrl.startsWith("jdbc:ingres:")) {
			return "com.ingres.jdbc.IngresDriver";
		} else if (rawUrl.startsWith("jdbc:h2:")) {
			return H2_DRIVER;
		} else if (rawUrl.startsWith("jdbc:mckoi:")) {
			return "com.mckoi.JDBCDriver";
		} else if (rawUrl.startsWith("jdbc:cloudscape:")) {
			return "COM.cloudscape.core.JDBCDriver";
		} else if (rawUrl.startsWith("jdbc:informix-sqli:")) {
			return "com.informix.jdbc.IfxDriver";
		} else if (rawUrl.startsWith("jdbc:timesten:")) {
			return "com.timesten.jdbc.TimesTenDriver";
		} else if (rawUrl.startsWith("jdbc:as400:")) {
			return "com.ibm.as400.access.AS400JDBCDriver";
		} else if (rawUrl.startsWith("jdbc:sapdb:")) {
			return "com.sap.dbtech.jdbc.DriverSapDB";
		} else if (rawUrl.startsWith("jdbc:JSQLConnect:")) {
			return "com.jnetdirect.jsql.JSQLDriver";
		} else if (rawUrl.startsWith("jdbc:JTurbo:")) {
			return "com.newatlanta.jturbo.driver.Driver";
		} else if (rawUrl.startsWith("jdbc:firebirdsql:")) {
			return "org.firebirdsql.jdbc.FBDriver";
		} else if (rawUrl.startsWith("jdbc:interbase:")) {
			return "interbase.interclient.Driver";
		} else if (rawUrl.startsWith("jdbc:pointbase:")) {
			return "com.pointbase.jdbc.jdbcUniversalDriver";
		} else if (rawUrl.startsWith("jdbc:edbc:")) {
			return "ca.edbc.jdbc.EdbcDriver";
		} else if (rawUrl.startsWith("jdbc:mimer:multi1:")) {
			return "com.mimer.jdbc.Driver";
		} else if (rawUrl.startsWith("jdbc:dm:")) {
			return DM_DRIVER;
		} else if (rawUrl.startsWith("jdbc:kingbase:")) {
			return KINGBASE_DRIVER;
		} else if (rawUrl.startsWith("jdbc:gbase:")) {
			return GBASE_DRIVER;
		} else if (rawUrl.startsWith("jdbc:xugu:")) {
			return XUGU_DRIVER;
		} else if (rawUrl.startsWith("jdbc:hive:")) {
			return HIVE_DRIVER;
		} else if (rawUrl.startsWith("jdbc:hive2:")) {
			return HIVE_DRIVER;
		} else if (rawUrl.startsWith("jdbc:phoenix:thin:")) {
			return "org.apache.phoenix.queryserver.client.Driver";
		} else if (rawUrl.startsWith("jdbc:phoenix://")) {
			return PHOENIX_DRIVER;
		} else if (rawUrl.startsWith("jdbc:kylin:")) {
			return KYLIN_DRIVER;
		} else if (rawUrl.startsWith("jdbc:elastic:")) {
			return ELASTIC_SEARCH_DRIVER;
		} else if (rawUrl.startsWith("jdbc:clickhouse:")) {
			return CLICKHOUSE_DRIVER;
		} else if (rawUrl.startsWith("jdbc:presto:")) {
			return PRESTO_DRIVER;
		} else if (rawUrl.startsWith("jdbc:inspur:")) {
			return KDB_DRIVER;
		} else if (rawUrl.startsWith("jdbc:polardb")) {
			return POLARDB_DRIVER;
		} else if (rawUrl.startsWith("metrics:")) {
			return METRICS_DRIVER;
		} else {
			throw new RuntimeException("unknown jdbc driver : " + rawUrl);
		}
	}
}