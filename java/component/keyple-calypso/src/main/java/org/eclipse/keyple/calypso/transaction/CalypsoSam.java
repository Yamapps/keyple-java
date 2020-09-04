/* **************************************************************************************
 * Copyright (c) 2019 Calypso Networks Association https://www.calypsonet-asso.org/
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

import static org.eclipse.keyple.calypso.command.sam.SamRevision.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.keyple.calypso.command.sam.SamRevision;
import org.eclipse.keyple.core.selection.AbstractMatchingSe;
import org.eclipse.keyple.core.seproxy.message.SeResponse;
import org.eclipse.keyple.core.util.ByteArrayUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CalypsoSam extends AbstractMatchingSe {
  private static final Logger logger = LoggerFactory.getLogger(CalypsoSam.class);

  private final SamRevision samRevision;
  private final byte[] serialNumber = new byte[4];
  private final byte platform;
  private final byte applicationType;
  private final byte applicationSubType;
  private final byte softwareIssuer;
  private final byte softwareVersion;
  private final byte softwareRevision;

  /**
   * Constructor.
   *
   * @param selectionResponse the selection response from the SAM
   */
  CalypsoSam(SeResponse selectionResponse) {
    super(selectionResponse);

    String atrString =
        ByteArrayUtil.toHex(selectionResponse.getSelectionStatus().getAtr().getBytes());
    if (atrString.isEmpty()) {
      throw new IllegalStateException("ATR should not be empty.");
    }
    /* extract the historical bytes from T3 to T12 */
    String extractRegex = "3B(.{6}|.{10})805A(.{20})829000";
    Pattern pattern = Pattern.compile(extractRegex); // NOSONAR: hex strings here, regex is safe
    // to use
    Matcher matcher = pattern.matcher(atrString);
    if (matcher.find(0)) {
      byte[] atrSubElements = ByteArrayUtil.fromHex(matcher.group(2));
      platform = atrSubElements[0];
      applicationType = atrSubElements[1];
      applicationSubType = atrSubElements[2];

      // determine SAM revision from Application Subtype
      switch (applicationSubType) {
        case (byte) 0xC1:
          samRevision = C1;
          break;
        case (byte) 0xD0:
        case (byte) 0xD1:
        case (byte) 0xD2:
          samRevision = S1D;
          break;
        case (byte) 0xE1:
          samRevision = S1E;
          break;
        default:
          throw new IllegalStateException(
              String.format(
                  "Unknown SAM revision (unrecognized application subtype 0x%02X)",
                  applicationSubType));
      }

      softwareIssuer = atrSubElements[3];
      softwareVersion = atrSubElements[4];
      softwareRevision = atrSubElements[5];
      System.arraycopy(atrSubElements, 6, serialNumber, 0, 4);
      if (logger.isTraceEnabled()) {
        logger.trace(
            String.format(
                "SAM %s PLATFORM = %02X, APPTYPE = %02X, APPSUBTYPE = %02X, SWISSUER = %02X, SWVERSION = "
                    + "%02X, SWREVISION = %02X",
                samRevision.getName(),
                platform,
                applicationType,
                applicationSubType,
                softwareIssuer,
                softwareVersion,
                softwareRevision));
        logger.trace("SAM SERIALNUMBER = {}", ByteArrayUtil.toHex(serialNumber));
      }
    } else {
      throw new IllegalStateException("Unrecognized ATR structure: " + atrString);
    }
  }

  public final SamRevision getSamRevision() {
    return samRevision;
  }

  public final byte[] getSerialNumber() {
    return serialNumber;
  }

  public final byte getPlatform() {
    return platform;
  }

  public final byte getApplicationType() {
    return applicationType;
  }

  public final byte getApplicationSubType() {
    return applicationSubType;
  }

  public final byte getSoftwareIssuer() {
    return softwareIssuer;
  }

  public final byte getSoftwareVersion() {
    return softwareVersion;
  }

  public final byte getSoftwareRevision() {
    return softwareRevision;
  }
}
