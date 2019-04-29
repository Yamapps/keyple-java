/********************************************************************************
 * Copyright (c) 2019 Calypso Networks Association https://www.calypsonet-asso.org/
 *
 * See the NOTICE file(s) distributed with this work for additional information regarding copyright
 * ownership.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package org.eclipse.keyple.core.seproxy.protocol;

import static org.eclipse.keyple.core.seproxy.protocol.SeProtocol.NfcCompatibility.*;
import static org.eclipse.keyple.core.seproxy.protocol.SeProtocol.ProtocolType.*;
import static org.eclipse.keyple.core.seproxy.protocol.TransmissionMode.*;

public enum SeCommonProtocol implements SeProtocol {

    /* ------------ contactless standard ---------------- */
    PROTOCOL_ISO14443_4("ISO 14443-4", STANDARD, CONTACTLESS, NFC),

    /* ------------ contactless proprietary ------------- */
    PROTOCOL_ISO14443_3A("ISO 14443-3 Type A", PROPRIETARY, CONTACTLESS, NFC),

    PROTOCOL_ISO14443_3B("ISO 14443-3 Type B", PROPRIETARY, CONTACTLESS, NFC),

    PROTOCOL_B_PRIME("Old Calypso B Prime", PROPRIETARY, CONTACTLESS, NOT_NFC),

    PROTOCOL_MIFARE_CLASSIC("Mifare Classic", PROPRIETARY, CONTACTLESS, NFC),

    PROTOCOL_MIFARE_UL("Mifare Ultra Light", PROPRIETARY, CONTACTLESS, NFC),

    PROTOCOL_MIFARE_DESFIRE("Mifare Desfire", PROPRIETARY, CONTACTLESS, NFC),

    PROTOCOL_MEMORY_ST25("Memory ST25", PROPRIETARY, CONTACTLESS, NOT_NFC),

    /* ------------ contactless wildcard protocol ------- */
    PROTOCOL_ANY("Any contactless protocol", PROPRIETARY, CONTACTLESS, NFC),

    /* ------------ contacts standard ------------------- */
    PROTOCOL_ISO7816_3("ISO 7816-3", STANDARD, CONTACTS, NOT_NFC);

    private final String name;
    private final ProtocolType protocolType;
    private final TransmissionMode transmissionMode;
    private final NfcCompatibility nfcCompatibility;

    /**
     * Constructor
     */
    SeCommonProtocol(String name, ProtocolType protocolType, TransmissionMode transmissionMode,
            NfcCompatibility nfcCompatibility) {
        this.name = name;
        this.protocolType = protocolType;
        this.transmissionMode = transmissionMode;
        this.nfcCompatibility = nfcCompatibility;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ProtocolType getProtocolType() {
        return protocolType;
    }

    @Override
    public TransmissionMode getTransmissionMode() {
        return transmissionMode;
    }

    @Override
    public NfcCompatibility getNfcCompatibility() {
        return nfcCompatibility;
    }
}
