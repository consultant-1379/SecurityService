/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2012
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.services.securityservice.ejb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;

import com.ericsson.nms.services.securityservice.api.SecurityService;

/**
 * Provides security token services.
 * 
 * @author eadaszi
 * 
 */

@Stateless
public class SecurityServiceBean implements SecurityService {

    @Inject
    private Logger logger;

    /**
     * This method generates a Cryptographic token using the given parameter userId. This method will perform a hMAC(SHA256) using the username +
     * counter combination as data and a generated session key as its secret key. In the end it returns a String in the following format:
     * Counter-Token The token getting generated is called a Message Authentication Code based on the SHA 256 algorithm. This method is exposed to the
     * Client to be invoked.
     * 
     * @param userId
     *            the user identification to be associated with the generated token
     *            
     * @return a cryptographic token using the given userId.
     */
    @Override
    public String requestToken(final String userId) {

        final int counterValue = this.createCounter();
        final byte[] sessionKey = this.getSessionKey(counterValue);
        logger.info("getSessionKey Key : " + sessionKey);

        final String macData = userId + counterValue;

        Mac mac;

        byte[] doFinal = null;
        try {
            mac = Mac.getInstance("HmacSHA256");

            byte[] dataBytes;
            dataBytes = macData.getBytes("UTF-8");

            final SecretKey secret = new SecretKeySpec(sessionKey, "HmacSHA256");
            mac.init(secret);
            doFinal = mac.doFinal(dataBytes);

        } catch (NoSuchAlgorithmException | UnsupportedEncodingException | InvalidKeyException e) {
            logger.error(e.getMessage(),e);
        } 

        logger.info("Token has been generated for User:" + userId);

        return counterValue + "-" + new String(Base64.encodeBase64(doFinal));
    }

    /**
     * This method generates a session key based on the current time, counter and a master key. Using the master key as a secret key and then
     * combining the bytes of the time and the counter in a byte array, a HMac(SHA256) is performed and the session key is generated.
     * @return an integer number with the minutes since 01/01/1970 00:00:00 GMT. 
     */
    protected int createCounter() {
        final Date date = new Date();
        final int counterValue = (int) (date.getTime() / (long) 60000);
        return counterValue;
    }

    /**
     * This method generates a HMac(SHA256) byte array given integer number.
     * @param value an integer number to be used as base to HMac(SHA256) byte array 
     * @return a HMac(SHA256) byte array
     */
    private byte[] getSessionKey(final int value) {

        final byte[] counterBytes = this.toByteArray(value);
        final byte[] sessionKey = new byte[10];
        final ByteBuffer BB = ByteBuffer.wrap(sessionKey);
        BB.put(counterBytes);

        final String masterKey = this.readMasterKey();

        byte[] mKey = null;

        byte[] sessionKeyFinal = null;

        try {
            mKey = masterKey.getBytes("UTF-8");

            Mac mac = null;
            mac = Mac.getInstance("HmacSHA256");
            final SecretKey secret = new SecretKeySpec(mKey, "HmacSHA256");
            mac.init(secret);
            sessionKeyFinal = mac.doFinal(sessionKey);

        } catch (UnsupportedEncodingException | NoSuchAlgorithmException | InvalidKeyException e) {
            logger.error(e.getMessage(),e);
        }

        return sessionKeyFinal;
    }

    /**
     * This method reads the Master key from the file system. Currently it is stored in a file called mkey.txt
     * @return a {@code String} with the content of master key file 
     */
    private String readMasterKey() {

        final ClassLoader cl = Thread.currentThread().getContextClassLoader();
        final StringBuilder sBuilder = new StringBuilder();
        String mKey;
        try (
             final InputStream in = cl.getResourceAsStream("mkey.txt");
             final BufferedReader bufferedReader =  new BufferedReader(new InputStreamReader(in));
        ) 
        {
            String sCurrentLine;
            while ((sCurrentLine = bufferedReader.readLine()) != null) {
                sBuilder.append(sCurrentLine);
            }
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
        } 
        mKey = sBuilder.toString();
        return mKey;
    }

    /**
     * Transforms a 32 bits integer number into a byte array with 4 octets. 
     * @param data the 32 bits integer data to be transformed
     * @return a byte array representation of an integer number
     */
    private byte[] toByteArray(final int data) {
        return new byte[] { (byte) ((data >> 24) & 0xff), (byte) ((data >> 16) & 0xff), (byte) ((data >> 8) & 0xff), (byte) ((data >> 0) & 0xff), };
    }

    /**
     * This method is just for testing purposes. Called from unit tests only.
     * @param testLogger the test logger
     */
    protected void setLogger(final Logger testLogger) {

        this.logger = testLogger;
    }

}
