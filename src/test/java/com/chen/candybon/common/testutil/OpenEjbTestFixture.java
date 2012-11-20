/*
 * Copyright (c) XIAOWEI CHEN, 2009.
 * All Rights Reserved. Reproduction in whole or in part is prohibited
 * without the written consent of the copyright owner.
 * 
 * XIAOWEI CHEN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. XIAOWEI CHEN SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 * 
 * All rights reserved.
 */
package com.chen.candybon.common.testutil;

import org.junit.After;
import org.junit.BeforeClass;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.transaction.UserTransaction;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.fail;

/**
 * Default uses HSQLDB
 *
 * To use mysql:
 *  -Dopenejbtargetdb=mysql
 *
 */
public class OpenEjbTestFixture {

    private static String showSql = "false";

    @BeforeClass
    public static void setupForClass() {
        Properties openejbProp = new Properties();

        InputStream in = OpenEjbTestFixture.class.getClassLoader().getResourceAsStream("openejb.properties");
        if (in != null) {
            try {
                openejbProp.load(in);
                System.out.println("Got extra open ejb properties: " + openejbProp);
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        System.setProperty(Context.INITIAL_CONTEXT_FACTORY,
                "org.apache.openejb.client.LocalInitialContextFactory");
        Properties p = new Properties();
        //This can make sure the persistence.xml from other dependent module will not be put into ear file.
        //This can make sure for each persistence unit, there is only one matcher.
        p.put("openejb.deployments.classpath.ear", "false");
        p.put("openejb.nobanner", "false");
        p.put("openejb.embedded.initialcontext.close", "DESTROY");
        p.putAll(openejbProp);
        String target = System.getProperties().getProperty("openejbtargetdb");
        String optionalUrl = System.getProperties().getProperty("wong.test.jdbc.url");
        if (target != null) {
            if (target.equalsIgnoreCase("mysql")) {
                targetDb = DatabaseType.MYSQL;
            } else if (target.equalsIgnoreCase("hsqldb")) {
                targetDb = DatabaseType.HSQLDB;
            } else {
                throw new UnsupportedOperationException("not support now");
            }
        }

        p.putAll(preparePersistenceProperties(targetDb, optionalUrl));
        // enable logs for open ejb and hibernate
        p.putAll(disableOpenejbAndHibernateLog());
        try {
            context = new InitialContext(p);
            String deploymentId = p.getProperty("openejb.deploymentId.format");
            if (deploymentId != null && "{moduleId}/{ejbName}".equals(deploymentId)) {
                helpBean = (TransactionHelp) context.lookup("classes/" + TransactionHelpSlsb.class.getSimpleName()
                        + JNDI_POSTFIX);

            } else {
                helpBean = (TransactionHelp) context.lookup(TransactionHelpSlsb.class.getSimpleName() + JNDI_POSTFIX);
            }
        } catch (NamingException e) {
            e.printStackTrace();
            fail();
        }

    }

    protected static Properties disableOpenejbAndHibernateLog() {
        java.util.logging.Logger log = java.util.logging.Logger.getLogger("org.hibernate");
        log.setLevel(Level.WARNING);

        log = java.util.logging.Logger.getLogger("org.hibernate.cfg.SettingsFactory");
        log.setLevel(Level.WARNING);

        Properties p = new Properties();
        p.put("log4j.rootLogger", "fatal, C");
        p.put("log4j.category.OpenEJB", "fatal");
        p.put("log4j.category.OpenEJB.options", "fatal");
        p.put("log4j.category.OpenEJB.server", "fatal");
        p.put("log4j.category.OpenEJB.startup", "fatal");
        p.put("log4j.category.OpenEJB.startup.service", "fatal");
        p.put("log4j.category.OpenEJB.startup.config", "fatal");
        p.put("log4j.category.OpenEJB.hsql", "fatal");
        p.put("log4j.category.CORBA-Adapter", "fatal");
        p.put("log4j.category.Transaction", "fatal");
        p.put("log4j.category.org.apache.activemq", "fatal");
        p.put("log4j.category.org.apache.geronimo", "fatal");
        p.put("log4j.category.org.hibernate", "ERROR");
        p.put("log4j.logger.org.hibernate", "ERROR");
        p.put("log4j.appender.C", "org.apache.log4j.ConsoleAppender");
        p.put("log4j.appender.C.layout", "org.apache.log4j.SimpleLayout");
        return p;
    }

    @After
    public void cleanupDb() throws Exception {
        try {
            helpBean.cleanupDb();
        } catch (Exception e) {
            // Ignore error
        }
    }

    public static interface TransactionHelp {

        <V> V call(Callable<V> callable) throws Exception;

        void cleanupDb();

        public void cleanOneTable(String sql);
    }

    @Stateless
    @TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
    public static class TransactionHelpSlsb implements TransactionHelp {

        private final static Logger logger = Logger.getLogger("OpenEjbTestFixture");
        @PersistenceContext(unitName = "openejb_unit_test", type = PersistenceContextType.TRANSACTION)
        private EntityManager em;
        @EJB
        private TransactionHelp self;

        public <V> V call(Callable<V> callable) throws Exception {
            return callable.call();
        }

        public void cleanupDb() {
            for (String deleteOneTable : DELETE_ALL_TABLES) {
                try {
                    //call self which is also a slsb to make sure even if some delete sql failed,
                    // the rest can continue be executed.
                    self.cleanOneTable(deleteOneTable);
                } catch (Throwable e) {
                    // Ignore error
                    logger.warning("Delete table [" + deleteOneTable + "] failed: " + e.getMessage());
                }
            }
        }

        public void cleanOneTable(String sql) {
            em.createNativeQuery(sql).executeUpdate();
        }
    }

    public static Context getContext() {
        return context;
    }

    public static UserTransaction getUserTransaction() {
        try {
            return (UserTransaction) context.lookup("java:comp/UserTransaction");
        } catch (NamingException ex) {
            throw new IllegalStateException("Failed to lookup UserTransaction");
        }
    }

    protected static Properties preparePersistenceProperties(DatabaseType type, String optionalUrl) {
        Properties p = new Properties();
        if (type.equals(DatabaseType.MYSQL)) {

            System.out.println("======================================================");
            System.out.println("======================================================");
            System.out.println("======================================================");
            System.out.println("===== Running openejb unit test with Mysql ===========");
            System.out.println("======================================================");
            System.out.println("======================================================");
            System.out.println("======================================================");
            p.put("jdbc/candybon", "new://Resource?type=DataSource");
            p.put("jdbc/candybon.JdbcDriver", "com.mysql.jdbc.Driver");
            if (optionalUrl == null) {
                p.put("jdbc/candybon.JdbcUrl", "jdbc:mysql://127.0.0.1:3306/fasttrack_db");
            } else {
                p.put("jdbc/candybon.JdbcUrl", optionalUrl);
                System.out.println("MySQL URL = '" + optionalUrl + "'");
            }
            p.put("jdbc/candybon.Username", "fasttrack_user");
            p.put("jdbc/candybon.Password", "");

        } else if (type.equals(DatabaseType.HSQLDB)) {
            System.out.println("======================================================");
            System.out.println("======================================================");
            System.out.println("======================================================");
            System.out.println("=====Running openejb unit test with HSQLDB=============");
            System.out.println("======================================================");
            System.out.println("======================================================");
            System.out.println("======================================================");
            p.put("jdbc/candybon", "new://Resource?type=DataSource");
            p.put("jdbc/candybon.JdbcDriver", "org.hsqldb.jdbcDriver");

            p.put("jdbc/candybon.JdbcUrl", "jdbc:hsqldb:mem:candybon" + System.currentTimeMillis());
            
        }

        return p;
    }

    protected static Context context;
    protected static TransactionHelp helpBean;
    public static final String DELETE_ALL_TABLES[] = {
            };
    protected static DatabaseType targetDb = DatabaseType.HSQLDB;
    public static final String JNDI_POSTFIX = "Local";
}
