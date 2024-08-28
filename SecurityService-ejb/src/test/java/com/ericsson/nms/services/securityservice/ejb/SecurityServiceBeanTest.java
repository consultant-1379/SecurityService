/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2015
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.services.securityservice.ejb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.TimeZone;

import javax.crypto.Mac;

import org.apache.commons.codec.binary.Base64;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Class responsible to test the SecurityServiceBean.
 * 
 * @author xromsza
 * 
 */
public class SecurityServiceBeanTest extends SecurityServiceBean {

    Mac mac;
    SecurityServiceBean serviceBean;

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Initializes and mocks {@link javax.crypto.Mac} and {@link com.ericsson.nms.services.securityservice.ejb.SecurityServiceBean} objects.
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        this.mac = EasyMock.createMock(Mac.class);
        this.serviceBean = EasyMock.createMock(SecurityServiceBean.class);
        this.setLogger(logger);
    }


    /**
     * Unit test for {@link com.ericsson.nms.services.securityservice.ejb.SecurityServiceBean#getSessionKey(int)}
     */
    @Test
    public void testGetSessionKey() {

        final int expected = 32;
        Class<?> serviceBean = SecurityServiceBean.class;
        final Method[] methods = serviceBean.getDeclaredMethods();
        final SecurityServiceBean securityServiceBean = new SecurityServiceBean();

        byte[] byteArray = null;
        Object resultObject = null;

        try {
            for (Method m : methods) {
                m.setAccessible(true);

                if (m.getName().equals("getSessionKey")) {

                    resultObject = m.invoke(securityServiceBean, securityServiceBean.createCounter());
                    byteArray = (byte[]) resultObject;
                    Assert.assertEquals(expected, byteArray.length);

                    String token1 = new String(Base64.encodeBase64(byteArray));
                    String token2 = null;

                    // Loop every second until the next token is generated 
                    do {
                        token2 = token1;
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            logger.error(e.getMessage(),e);
                        }
                        resultObject = m.invoke(securityServiceBean, securityServiceBean.createCounter());
                        byteArray = (byte[]) resultObject;
                        token1 = new String(Base64.encodeBase64(byteArray));
                    } while (token2.equals(token1));

                    // Verify 2 subsequent request tokens - no delay
                    resultObject = m.invoke(securityServiceBean,securityServiceBean.createCounter());
                    byteArray = (byte[]) resultObject;
                    token2 = new String(Base64.encodeBase64(byteArray));

                    logger.debug("The same tokens in two subsequent requests:");
                    logger.debug("Token 1:" + token1);
                    logger.debug("Token 2:" + token2);
                    logger.debug("===========================================");

                    assertEquals(token1, token2);

                    // Wait more than 1 minute (61 seconds) 
                    try {
                        Thread.sleep(61000);
                    } catch (InterruptedException e) {
                        logger.error(e.getMessage(),e);
                        
                    }

                    // Verify 2 subsequent request tokens - more than 1 minute delay
                    resultObject = m.invoke(securityServiceBean, securityServiceBean.createCounter());
                    byteArray = (byte[]) resultObject;
                    token1 = new String(Base64.encodeBase64(byteArray));

                    logger.debug("A different tokens after more than 1 minute:");
                    logger.debug("Token 1:" + token1);
                    logger.debug("Token 2:" + token2);
                    logger.debug("============================================");

                    assertNotSame(token1, token2);
                }
            }
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {

            Assert.fail(e.getMessage());
        }

    }
    /**
    *  Unit test for {@link com.ericsson.nms.services.securityservice.ejb.SecurityServiceBean#createCounter()}
    */
   
    @Test
    public void testCreateCounter()  {
        final Date date = new Date();
        final int expected = (int) (date.getTime() / (long) 60000);

        int counter = 0;
        final Class<?> serviceBean = SecurityServiceBean.class;
        final Method[] methods = serviceBean.getDeclaredMethods();
        final SecurityServiceBean sb = new SecurityServiceBean();

        try {
            for (Method m : methods) {
                m.setAccessible(true);
                if (m.getName().equals("createCounter")) {
                    counter = (int) m.invoke(sb, new Object[0]);
    
                }
            }
    
            assertEquals(expected, counter);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
               Assert.fail(e.getMessage());
        }
        
    }

    /**
     *  Unit test for {@link com.ericsson.nms.services.securityservice.ejb.SecurityServiceBean#requestToken(String)}
     */
   
    @Test
    public void testRequestToken()  {

        final String output = this.requestToken("uname");
        assertNotNull(output);
        assertTrue(output.length()>0);
    }

    /**
     *  Unit test for {@link com.ericsson.nms.services.securityservice.ejb.SecurityServiceBean#readMasterKey(String)}
     */
    @Test
    public void testReadMasterKey()  {

        final String expected = "53cur1tyh31md477r";
        final Class<?> serviceBean = SecurityServiceBean.class;
        final Method[] methods = serviceBean.getDeclaredMethods();
        final SecurityServiceBean sb = new SecurityServiceBean();
        String masterKey = "";
        try {
            for (Method m : methods) {
                m.setAccessible(true);
    
                if (m.getName().equals("readMasterKey")) {
                    masterKey = m.invoke(sb, new Object[0]).toString();
    
                }
            }
        }
        catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            Assert.fail(e.getMessage());
        }

        assertEquals(expected, masterKey);
    }


    /**
     * Tests the token retrieval between time zones.
     * 
     * Unit test for {@link com.ericsson.nms.services.securityservice.ejb.SecurityServiceBean#requestToken(String)}
     * 
     */
    @Test
    public void testRequestTokenInDifferentTimeZones() {

       
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        final String output = this.requestToken("uname");
        TimeZone.setDefault(TimeZone.getTimeZone("America/Los_Angeles"));
        final String output2 = this.requestToken("uname");

        assertEquals(output, output2);
    }

}
