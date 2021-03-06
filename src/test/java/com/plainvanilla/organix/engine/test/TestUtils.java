package com.plainvanilla.organix.engine.test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;

import org.apache.commons.lang3.RandomStringUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import com.plainvanilla.organix.engine.model.Connection;
import com.plainvanilla.organix.engine.model.ConnectionEndpoint;
import com.plainvanilla.organix.engine.model.ConnectionType;
import com.plainvanilla.organix.engine.model.ObjectInstance;
import com.plainvanilla.organix.engine.model.ObjectType;


public class TestUtils {

    public static Configuration getConfiguration() {

        Configuration cfg = new Configuration();

        cfg.addAnnotatedClass(ObjectType.class);
        cfg.addAnnotatedClass(ConnectionType.class);
        cfg.addAnnotatedClass(Connection.class);
        cfg.addAnnotatedClass(ConnectionEndpoint.class);
        cfg.addAnnotatedClass(ObjectInstance.class);

        cfg.setProperty("hibernate.connection.driver_class", "org.hsqldb.jdbcDriver").
                setProperty("hibernate.connection.url", "jdbc:hsqldb:mem:organixTestDb").
                setProperty("hibernate.connection.username", "sa").
                setProperty("hibernate.connection.password", "").
                setProperty("hibernate.hbm2ddl.auto", "create-drop").
                setProperty("hibernate.dialect", "org.hibernate.dialect.HSQLDialect").
                setProperty("hibernate.connection.pool_size", "5").
                setProperty("hibernate.show_sql", "true").
                setProperty("hibernate.cglib.use_reflection_optimizer", "true").
                setProperty("hibernate.connection.autocommit", "false");

        return cfg;
    }

    public static <T> T initPojo(Class<T> pojo) {
        T instance = null;

        try {
            instance = pojo.newInstance();
            initPojos(instance);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return instance;
        }
    }

    public static <T> void initPojos(T... pojos) {

        for (T pojo : pojos) {

            Field[] fields = pojo.getClass().getDeclaredFields();
            for (Field field : fields) {

                field.setAccessible(true);

                StringBuilder sb = new StringBuilder(field.getName());
                String firstLetter = sb.substring(0, 1);
                sb.setCharAt(0, firstLetter.toUpperCase().charAt(0));
                sb.insert(0, "set");
                String setterName = sb.toString();

                Method setter = null;

                try {
                    setter = pojo.getClass().getDeclaredMethod(setterName);
                    setter.setAccessible(true);
                } catch (NoSuchMethodException e) {
                    //    System.out.println("Setter " + pojo.getClass().getName() + "." + setterName + " not found for field :" + field.getName());
                }

                // skip JPA @Id fields
                if (field.isAnnotationPresent(javax.persistence.Id.class) || (setter != null && setter.isAnnotationPresent(javax.persistence.Id.class))) {
                    continue;
                }

                Class type = field.getType();
                try {
                    if (type.equals(String.class)) {
                        field.set(pojo, RandomStringUtils.randomAlphabetic(((int) (Math.random() * 15)) + 1));
                    } else if (type.equals(Date.class)) {
                        field.set(pojo, new Date((long)(Math.random() * new Date().getTime())));
                    } else if (type.equals(int.class) || type.equals(Integer.class) || type.equals(long.class) || type.equals(Long.class)) {
                        field.set(pojo, (int) (Math.random() * 1000));
                    } else if (type.isEnum()) {
                        final Object[] constants = type.getEnumConstants();
                        final int order = (int)(Math.random() * constants.length);
                        field.set(pojo, constants[order]);
                    } else if (type.equals(boolean.class) || type.equals(Boolean.class)) {
                        boolean value = (Math.random() > 0.5);
                        field.set(pojo, value);
                    }

                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            System.out.println(pojo.toString());
        }
    }

    public static void userThinkTime(int sec) {
        try {
            Thread.sleep(sec  * 1000);
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
    }


    public static void randomUserThinkTime() {
        randomUserThinkTime(5);
    }

    public static void randomUserThinkTime(int maxSeconds) {
        userThinkTime((int)(Math.random() * maxSeconds));
    }
/*
    public static void runUsers(int userCount, SessionFactory sf, Long entityId, Class<? extends InteractionStrategy> strategy) {
        StrategyExecutor [] users = new StrategyExecutor[userCount];
        try {
            for (int i = 0; i < userCount; i++) {
                users[i] = new StrategyExecutor("user" + i, strategy.newInstance(), sf, entityId);
            }
            waitForThreads(users);
        } catch(Exception e) {
            e.printStackTrace();
        }

    }*/
/*
    public static void saveEntity(ModelEntity<Long> entity, SessionFactory sf) {
        Session session = sf.openSession();
        Transaction tx = session.beginTransaction();

        try {
            session.save(entity);
            tx.commit();
        } catch(HibernateException e) {
            tx.rollback();
            throw e;
        } finally {
            session.close();
        }
    }*/

    public static Object getEntityById(Long id, SessionFactory sf, Class clazz) {
        Session session = sf.openSession();
        Transaction tx = session.beginTransaction();
        Object entity = null;
        try {

            entity = session.get(clazz, id);
            tx.commit();
        } catch (HibernateException e) {
            tx.rollback();
            throw e;
        } finally {
            session.close();
        }

        return entity;
    }

    public static void waitForThreads(Thread... threads) {
        try {
            for (Thread t : threads) {
                t.join();
            }
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
    }
}
