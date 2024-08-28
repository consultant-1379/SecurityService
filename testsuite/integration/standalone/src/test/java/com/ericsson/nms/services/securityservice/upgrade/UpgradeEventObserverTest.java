/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2013
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.services.securityservice.upgrade;

import static com.ericsson.oss.itpf.sdk.upgrade.UpgradePhase.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.nms.services.securityservice.upgrade.UpgradeEventObserver;
import com.ericsson.oss.itpf.sdk.upgrade.UpgradeEvent;

@RunWith(MockitoJUnitRunner.class)
public class UpgradeEventObserverTest {
    static final String EXPECTED_ACCEPT_RESPONSE = "OK";

    @Mock
    private UpgradeEvent upgradeEvent;

    @InjectMocks
    UpgradeEventObserver objUnderTest = new UpgradeEventObserver();

    @Test
    public void upgradeEventIsAcceptedWithUpgradePhaseServiceInstanceUpgradePrepare() {
        when(upgradeEvent.getPhase()).thenReturn(SERVICE_INSTANCE_UPGRADE_PREPARE);
        objUnderTest.upgradeNotificationObserver(upgradeEvent);
        verify(upgradeEvent).accept(EXPECTED_ACCEPT_RESPONSE);
    }

    @Test
    public void upgradeEventIsAcceptedWithUpgradePhaseServiceClusterUpgradePrepare() {
        when(upgradeEvent.getPhase()).thenReturn(SERVICE_CLUSTER_UPGRADE_PREPARE);
        objUnderTest.upgradeNotificationObserver(upgradeEvent);
        verify(upgradeEvent).accept(EXPECTED_ACCEPT_RESPONSE);
    }

    @Test
    public void upgradeEventIsAcceptedWithUpgradePhaseServiceClusterUpgradeFailed() {
        when(upgradeEvent.getPhase()).thenReturn(SERVICE_CLUSTER_UPGRADE_FAILED);
        objUnderTest.upgradeNotificationObserver(upgradeEvent);
        verify(upgradeEvent).accept(EXPECTED_ACCEPT_RESPONSE);
    }

    @Test
    public void upgradeEventIsAcceptedWithUpgradePhaseServiceClusterUpgradeFinishedSuccessfully() {
        when(upgradeEvent.getPhase()).thenReturn(SERVICE_INSTANCE_UPGRADE_FINISHED_SUCCESSFULLY);
        objUnderTest.upgradeNotificationObserver(upgradeEvent);
        verify(upgradeEvent).accept(EXPECTED_ACCEPT_RESPONSE);
    }

    @Test
    public void upgradeEventIsAcceptedWithUpgradePhaseServiceInstanceUpgradeFailed() {
        when(upgradeEvent.getPhase()).thenReturn(SERVICE_INSTANCE_UPGRADE_FAILED);
        objUnderTest.upgradeNotificationObserver(upgradeEvent);
        verify(upgradeEvent).accept(EXPECTED_ACCEPT_RESPONSE);
    }

    @Test
    public void upgradeEventIsAcceptedWithUpgradePhaseServiceInstanceUpgradeFinishedSuccessfully() {
        when(upgradeEvent.getPhase()).thenReturn(SERVICE_CLUSTER_UPGRADE_FINISHED_SUCCESSFULLY);
        objUnderTest.upgradeNotificationObserver(upgradeEvent);
        verify(upgradeEvent).accept(EXPECTED_ACCEPT_RESPONSE);
    }

}
