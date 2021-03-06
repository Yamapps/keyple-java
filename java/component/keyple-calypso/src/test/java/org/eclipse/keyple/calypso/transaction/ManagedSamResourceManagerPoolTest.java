/* **************************************************************************************
 * Copyright (c) 2020 Calypso Networks Association https://www.calypsonet-asso.org/
 *
 * See the NOTICE file(s) distributed with this work for additional information
 * regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 ************************************************************************************** */
package org.eclipse.keyple.calypso.transaction;

import static org.eclipse.keyple.calypso.transaction.SamResourceManagerFactory.DEFAULT_SLEEP_TIME;
import static org.eclipse.keyple.calypso.transaction.SamResourceManagerFactory.MAX_BLOCKING_TIME;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

import org.eclipse.keyple.calypso.CalypsoBaseTest;
import org.eclipse.keyple.calypso.command.sam.SamRevision;
import org.eclipse.keyple.calypso.exception.CalypsoNoSamResourceAvailableException;
import org.eclipse.keyple.core.selection.SeResource;
import org.eclipse.keyple.core.seproxy.ReaderPoolPlugin;
import org.eclipse.keyple.core.seproxy.SeReader;
import org.eclipse.keyple.core.seproxy.exception.KeypleAllocationReaderException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(MockitoJUnitRunner.class)
public class ManagedSamResourceManagerPoolTest extends CalypsoBaseTest {

  private static final Logger logger =
      LoggerFactory.getLogger(ManagedSamResourceManagerPoolTest.class);

  @Before
  public void setUp() {
    logger.info("------------------------------");
    logger.info("Test {}", name.getMethodName() + "");
    logger.info("------------------------------");
  }

  @Test
  public void waitResources() {
    // init
    SamResourceManagerPool srmSpy = srmSpy();
    long start = System.currentTimeMillis();
    Boolean exceptionThrown = false;

    // test
    try {
      SeResource<CalypsoSam> out =
          srmSpy.allocateSamResource(
              SamResourceManager.AllocationMode.BLOCKING,
              SamIdentifier.builder()
                  .samRevision(SamRevision.AUTO)
                  .serialNumber("any")
                  .groupReference("any")
                  .build());

    } catch (CalypsoNoSamResourceAvailableException e) {
      exceptionThrown = true;
    } catch (KeypleAllocationReaderException e) {
      exceptionThrown = true;
    }
    long stop = System.currentTimeMillis();

    // assert an exception is thrown after MAX_BLOCKING_TIME
    Assert.assertTrue(exceptionThrown);
    Assert.assertTrue(stop - start > MAX_BLOCKING_TIME);
  }

  @Test
  public void getResource() throws Exception {
    // init plugin
    ReaderPoolPlugin poolPlugin = Mockito.mock(ReaderPoolPlugin.class);
    doReturn(seReaderMock()).when(poolPlugin).allocateReader(any(String.class));

    // init SamResourceManagerPool with custom pool plugin
    SamResourceManagerPool srmSpy = srmSpy(poolPlugin);
    doReturn(samResourceMock()).when(srmSpy).createSamResource(any(SeReader.class));

    long start = System.currentTimeMillis();

    // test
    SeResource<CalypsoSam> out =
        srmSpy.allocateSamResource(
            SamResourceManager.AllocationMode.BLOCKING,
            SamIdentifier.builder()
                .samRevision(SamRevision.AUTO)
                .serialNumber("any")
                .groupReference("any")
                .build());

    long stop = System.currentTimeMillis();

    // assert results
    Assert.assertNotNull(out);
    Assert.assertTrue(stop - start < MAX_BLOCKING_TIME);
  }

  /*
   * Helpers
   */
  // get a srm spy with a custom mock reader
  SamResourceManagerPool srmSpy(ReaderPoolPlugin poolPlugin) {
    return Mockito.spy(
        new SamResourceManagerPool(poolPlugin, MAX_BLOCKING_TIME, DEFAULT_SLEEP_TIME));
  }

  // get a srm spy with a default mock reader
  SamResourceManagerPool srmSpy() {
    ReaderPoolPlugin poolPlugin = Mockito.mock(ReaderPoolPlugin.class);
    return Mockito.spy(
        new SamResourceManagerPool(poolPlugin, MAX_BLOCKING_TIME, DEFAULT_SLEEP_TIME));
  }

  SamResourceManagerDefault.ManagedSamResource samResourceMock() {
    SamResourceManagerDefault.ManagedSamResource mock =
        Mockito.mock(SamResourceManagerDefault.ManagedSamResource.class);
    return mock;
  }

  SeReader seReaderMock() {
    SeReader mock = Mockito.mock(SeReader.class);
    return mock;
  }
}
