/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2014
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package test.integration.ejb;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;

import test.integration.Artifact;

import com.ericsson.nms.services.securityservice.api.SecurityService;

/*
 * Arquillian test - Injecting EJB, Creating Archives and Libraries
 */
@RunWith(Arquillian.class)
public class ServiceBeanTest {

    @Inject
    protected transient Logger LOGGER;

    @EJB(lookup = "java:module/SecurityServiceBean")
    protected transient SecurityService serviceBean;

    @Deployment(name = Artifact.COM_ERICSSON_OSS_SECURITY_SERVICE___TEST_EAR, order = 1)
    public static EnterpriseArchive createSecurityServiceTestArchive() {
        return Artifact.getSecurityServiceTestArchive();
    }

    /*
     * To Test serviceBean
     */
    @Test
    @OperateOnDeployment(Artifact.COM_ERICSSON_OSS_SECURITY_SERVICE___TEST_EAR)
    @InSequence(1)
    public void testBeanIsNotNull() throws KeyManagementException, InvalidKeyException, NoSuchAlgorithmException,
            IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException, NoSuchPaddingException {
        LOGGER.info("Running Test testBeanIsNotNull()");
        Assert.assertNotNull("ServiceBean should not be null", this.serviceBean);
    }

    @Test
    @OperateOnDeployment(Artifact.COM_ERICSSON_OSS_SECURITY_SERVICE___TEST_EAR)
    @InSequence(2)
    public void testGettingRequestToken() throws KeyManagementException, InvalidKeyException, NoSuchAlgorithmException,
            IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException, NoSuchPaddingException {
        LOGGER.info("Running Test testGettingRequestToken()");
        LOGGER.info(this.serviceBean.requestToken("EADASZI"));
        Assert.assertNotNull("Resolved date should not be null", this.serviceBean.requestToken("EADASZI"));
    }

}