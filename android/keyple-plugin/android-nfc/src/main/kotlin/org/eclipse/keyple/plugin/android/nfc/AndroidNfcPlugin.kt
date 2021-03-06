/********************************************************************************
 * Copyright (c) 2020 Calypso Networks Association https://www.calypsonet-asso.org/
 *
 * See the NOTICE file(s) distributed with this work for additional information regarding copyright
 * ownership.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package org.eclipse.keyple.plugin.android.nfc

import org.eclipse.keyple.core.seproxy.ReaderPlugin

/**
 * The PcscPlugin interface provides the public elements used to manage the Android OMAPI plugin.
 */
interface AndroidNfcPlugin : ReaderPlugin {
    companion object {
        const val PLUGIN_NAME = "AndroidNfcPlugin"
    }
}
