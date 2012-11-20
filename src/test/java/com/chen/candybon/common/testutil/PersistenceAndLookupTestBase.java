package com.chen.candybon.common.testutil;

import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;
import java.util.logging.Level;


/**
 * Base class used for testing persistence classes.
 *
 * @author Xiaowei Chen
 */
public abstract class PersistenceAndLookupTestBase {
    private static final int MIN_LINE_LENGTH = 5;
    private static final Logger LOG = LoggerFactory.getLogger(PersistenceAndLookupTestBase.class.getName());

    private static final String PERSISTENCE_UNIT_NAME = "unit_test";
    private static final String DB_URL_KEY = "wong.test.jdbc.url";
    private static final String DB_USER_KEY = "wong.test.jdbc.user";
    private static final String DB_PASSWORD_KEY = "wong.test.jdbc.password";
    public static final String TARGET_DB = "targetdb";
    public static final String HSQLDB = "hsqldb";


    private static EntityManagerFactory emf = null;
    private static final String DDL_AUTO = "hibernate.hbm2ddl.auto";
    private static final String DB_DIALECT = "hibernate.dialect";
    private static final String DB_DRIVER_CLASS = "hibernate.connection.driver_class";
    private static final String SHOW_SQL = "hibernate.show_sql";
    private static final String CONNECTION_URL = "hibernate.connection.url";
    private static final String USERNAME = "hibernate.connection.username";
    private static final String PASSWORD = "hibernate.connection.password";
    private EntityManager entityManager = null;
    public static Boolean USE_HSQLDB = false;

    @BeforeClass
    public static void setEntityManagerFactory() {
        Map<String, String> map = new HashMap<String, String>();
        String targetDb = System.getProperties().getProperty(TARGET_DB);
        if (targetDb != null && targetDb.equals(HSQLDB)) {
            USE_HSQLDB = true;
        }
        if (USE_HSQLDB) {
            map.put(CONNECTION_URL, "jdbc:hsqldb:mem:unit-testing-jpa;create=true");
            map.put(USERNAME, "sa");
            map.put(PASSWORD, "");
            map.put(DDL_AUTO, "create-drop");
            map.put(DB_DIALECT, "org.hibernate.dialect.HSQLDialect");
            map.put(DB_DRIVER_CLASS, "org.hsqldb.jdbcDriver");
            map.put(SHOW_SQL, "false");
            System.out.println("======================================================");
            System.out.println("======================================================");
            System.out.println("======================================================");
            System.out.println("==========Running unit test with Hsqldb===============");
            System.out.println("======================================================");
            System.out.println("======================================================");
            System.out.println("======================================================");
        } else {
            addProperty(map, DB_URL_KEY, CONNECTION_URL);
            addProperty(map, DB_USER_KEY, USERNAME);
            addProperty(map, DB_PASSWORD_KEY, PASSWORD);
            System.out.println("======================================================");
            System.out.println("======================================================");
            System.out.println("======================================================");
            System.out.println("========== Running unit test with Mysql ==============");
            System.out.println("======================================================");
            System.out.println("======================================================");
            System.out.println("======================================================");
        }
        emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME, map);

        java.util.logging.Logger hibernateLogger = java.util.logging.Logger.getLogger("org.hibernate.cfg");
        hibernateLogger.setLevel(Level.WARNING);
        hibernateLogger = java.util.logging.Logger.getLogger("org.hibernate.tool");
        hibernateLogger.setLevel(Level.WARNING);
        hibernateLogger = java.util.logging.Logger.getLogger("org.hibernate.hql");
        hibernateLogger.setLevel(Level.WARNING);
        hibernateLogger = java.util.logging.Logger.getLogger("org.hibernate.connection");
        hibernateLogger.setLevel(Level.WARNING);
        hibernateLogger = java.util.logging.Logger.getLogger("org.hibernate.dialect");
        hibernateLogger.setLevel(Level.WARNING);
        hibernateLogger = java.util.logging.Logger.getLogger("org.hibernate.impl");
        hibernateLogger.setLevel(Level.WARNING);
        hibernateLogger = java.util.logging.Logger.getLogger("org.hibernate.transaction");
        hibernateLogger.setLevel(Level.WARNING);
    }

    public PersistenceAndLookupTestBase() {
    }

    protected boolean isDeployTest() {
        return false;
    }

    protected abstract String getDbScriptFile();

    protected abstract String getOptionalPath();

    protected EntityManager getEntityManager() {
        if (entityManager == null) {
            entityManager = emf.createEntityManager();
        }
        return entityManager;
    }

    protected void beginTransaction() {
        getEntityManager().getTransaction().begin();
    }

    protected void commitTransaction() {
        if (!getEntityManager().getTransaction().getRollbackOnly()) {
            getEntityManager().getTransaction().commit();
        } else {
            rollbackTransaction();
        }
    }

    protected void beginTx() {
        beginTransaction();
    }

    protected void commitTx() {
        commitTransaction();
    }

    protected void rollbackTx() {
        rollbackTransaction();
    }

    /**
     * Rolls back the transaction if a transaction is active.
     * The method is safe to call even if no transaction is active.
     */
    protected void rollbackTransaction() {

        if (!isDeployTest()) {

            if (getEntityManager().getTransaction().isActive()) {
                getEntityManager().getTransaction().rollback();
            }
        }
    }

    protected void initDb() {
        LOG.debug("Initializing DB...");

        String fileName = getDbScriptFile();

        if (fileName != null) {

            try {
                FileReader in = null;

                try {
                    in = new FileReader(fileName);
                } catch (FileNotFoundException e) {
                    in = new FileReader(getOptionalPath() + fileName);
                }

                BufferedReader buf = new BufferedReader(in);
                String line = "";

                while ((line = buf.readLine()) != null) {

                    if (line.startsWith("#") || (line.length() < MIN_LINE_LENGTH)) {
                        // do nothing
                    } else {
                        getEntityManager().createNativeQuery(line).executeUpdate();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private static void addProperty(Map<String, String> map, String systemPropertyName,
                                    String persistencePropertyName) {
        String value = System.getProperties().getProperty(systemPropertyName);

        if (value != null) {
            map.put(persistencePropertyName, value);
        }
    }

}
