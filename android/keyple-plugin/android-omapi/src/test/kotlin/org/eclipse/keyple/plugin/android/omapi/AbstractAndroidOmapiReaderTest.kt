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
package org.eclipse.keyple.plugin.android.omapi

import io.mockk.MockKAnnotations
import io.mockk.unmockkAll
import java.io.IOException
import org.eclipse.keyple.core.seproxy.MultiSeRequestProcessing
import org.eclipse.keyple.core.seproxy.SeSelector
import org.eclipse.keyple.core.seproxy.exception.KeypleReaderException
import org.eclipse.keyple.core.seproxy.exception.KeypleReaderIOException
import org.eclipse.keyple.core.seproxy.message.ApduRequest
import org.eclipse.keyple.core.seproxy.message.ChannelControl
import org.eclipse.keyple.core.seproxy.message.SeRequest
import org.eclipse.keyple.core.seproxy.protocol.SeCommonProtocols
import org.eclipse.keyple.core.seproxy.protocol.TransmissionMode
import org.eclipse.keyple.core.util.ByteArrayUtil
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

internal abstract class AbstractAndroidOmapiReaderTest<T, V : AbstractAndroidOmapiReader> {

    companion object {
        internal const val PLUGIN_NAME = "AndroidOmapiPluginImpl"
        internal const val PO_AID = "A000000291A000000191"
        internal const val PO_AID_RESPONSE = "6F25840BA000000291A00000019102A516BF0C13C70800000000C0E11FA653070A3C230C1410019000"
    }

    abstract var nativeReader: T
    abstract var reader: V

    abstract fun mockReader(): T
    abstract fun mockReaderWithExceptionOnOpenLogicalChannel(throwable: Throwable): T
    abstract fun mockReaderWithNullOnOpenLogicalChannel(): T
    abstract fun mockReaderWithExceptionOnOpenBasicChannel(throwable: Throwable): T
    abstract fun mockReaderWithNullOnOpenBasicChannel(): T
    abstract fun mockReaderWithNoAid(): T
    abstract fun mockReaderWithExceptionOnOpenSession(throwable: Throwable): T
    abstract fun buildOmapiReaderImpl(nativeReader: T): V
    abstract fun getNativeReaderName(): String
    abstract fun mockReaderWithExceptionOnCloseChannel(throwable: Throwable): T
    abstract fun mockReaderWithExceptionWhileTransmittingApdu(throwable: Throwable): T

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        // default reader connected with secure element with poAid
        nativeReader = mockReader()

        // instantiate reader with nativeReader
        reader = buildOmapiReaderImpl(nativeReader)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }
    /*
     * TEST READERS
     */

    @Test
    fun getInstance() {
        Assert.assertNotNull(reader)
    }

    @Test
    fun getName() {
        Assert.assertEquals(getNativeReaderName(), reader.name)
    }

    @Test
    fun getTransmissionMode() {
        Assert.assertEquals(TransmissionMode.CONTACTS, reader.transmissionMode)
    }

    @Test
    fun isSEPresent() {
        Assert.assertEquals(true, reader.isSePresent)
    }

    @Test
    fun getParameters() {
        Assert.assertNotNull(reader.parameters)
    }

    @Test
    fun setParameters() {
        val parameters = HashMap<String, String>()
        parameters["key1"] = "value1"
        reader.parameters = parameters
        Assert.assertTrue(reader.parameters.size == 1)
        Assert.assertTrue(reader.parameters["key1"] == "value1")
    }

    @Test
    fun setParameter() {
        reader.setParameter("key2", "value2")
        Assert.assertTrue(reader.parameters.size == 1)
        Assert.assertTrue(reader.parameters["key2"] == "value2")
    }

    @Test(expected = KeypleReaderIOException::class)
    fun transmitIOException() {
        nativeReader = mockReaderWithExceptionOnOpenLogicalChannel(IOException())
        reader = buildOmapiReaderImpl(nativeReader)
        // test
        reader.transmitSeRequests(getSampleSeRequest(), MultiSeRequestProcessing.FIRST_MATCH, ChannelControl.KEEP_OPEN)
    }

    @Test(expected = KeypleReaderIOException::class)
    fun transmitSecurityException() {
        nativeReader = mockReaderWithExceptionOnOpenLogicalChannel(SecurityException())
        reader = buildOmapiReaderImpl(nativeReader)
        // test
        reader.transmitSeRequests(getSampleSeRequest(), MultiSeRequestProcessing.FIRST_MATCH, ChannelControl.KEEP_OPEN)
    }

    @Test
    fun transmitNoSuchElementException() {
        nativeReader = mockReaderWithExceptionOnOpenLogicalChannel(NoSuchElementException())
        reader = buildOmapiReaderImpl(nativeReader)
        // test
        val seResponseList = reader.transmitSeRequests(getSampleSeRequest(), MultiSeRequestProcessing.FIRST_MATCH, ChannelControl.KEEP_OPEN)
        Assert.assertNull(seResponseList[0]) // If container is not found a null responsed is returned
    }

    @Test(expected = KeypleReaderIOException::class)
    fun transmitOpenChannelFailed() {
        nativeReader = mockReaderWithNullOnOpenLogicalChannel()
        reader = buildOmapiReaderImpl(nativeReader)
        // test
        reader.transmitSeRequests(getSampleSeRequest(), MultiSeRequestProcessing.FIRST_MATCH, ChannelControl.KEEP_OPEN)
    }

    @Test(expected = KeypleReaderIOException::class)
    fun transmitNoAidOpenChannelFailed() {
        nativeReader = mockReaderWithNullOnOpenBasicChannel()
        reader = buildOmapiReaderImpl(nativeReader)
        // test
        reader.transmitSeRequests(getNoAidSampleSeRequest(), MultiSeRequestProcessing.FIRST_MATCH, ChannelControl.KEEP_OPEN)
    }

    @Test(expected = KeypleReaderIOException::class)
    fun transmitNoAidIOException() {
        nativeReader = mockReaderWithExceptionOnOpenBasicChannel(IOException())
        reader = buildOmapiReaderImpl(nativeReader)
        // test
        reader.transmitSeRequests(getNoAidSampleSeRequest(), MultiSeRequestProcessing.FIRST_MATCH, ChannelControl.KEEP_OPEN)
    }

    @Test(expected = KeypleReaderIOException::class)
    fun transmitNoAidSecurityException() {
        nativeReader = mockReaderWithExceptionOnOpenBasicChannel(SecurityException())
        reader = buildOmapiReaderImpl(nativeReader)
        // test
        reader.transmitSeRequests(getNoAidSampleSeRequest(), MultiSeRequestProcessing.FIRST_MATCH, ChannelControl.KEEP_OPEN)
    }

    @Test
    fun transmitNoAid() {

        // init
        nativeReader = mockReaderWithNoAid()
        reader = buildOmapiReaderImpl(nativeReader)

        // test
        val seResponseList = reader.transmitSeRequests(getSampleSeRequest(), MultiSeRequestProcessing.FIRST_MATCH, ChannelControl.KEEP_OPEN)

        // assert
        Assert.assertNull(seResponseList[0])
    }

    @Test
    fun transmitWrongProtocol() {
        // init
        val poAid = "A000000291A000000191"

        // wrong protocol
        val seRequest = SeRequest(SeSelector.builder()
                .seProtocol(SeCommonProtocols.PROTOCOL_MIFARE_UL)
                .aidSelector(SeSelector.AidSelector.builder()
                        .aidToSelect(poAid).build()).build(), ArrayList())

        // test
        val seRequests = ArrayList<SeRequest>()
        seRequests.add(seRequest)
        val seResponseList = reader.transmitSeRequests(seRequests, MultiSeRequestProcessing.FIRST_MATCH, ChannelControl.KEEP_OPEN)

        // assert
        Assert.assertNull(seResponseList[0])
    }

    @Test(expected = KeypleReaderException::class)
    fun transmitNotConnected() {

        // init
        val nativeReader = mockReaderWithExceptionOnOpenSession(IOException())
        reader = buildOmapiReaderImpl(nativeReader)

        // test
        reader.transmitSeRequests(getSampleSeRequest(), MultiSeRequestProcessing.FIRST_MATCH, ChannelControl.KEEP_OPEN)
    }

    @Test
    fun closeChannelAfterTransmit() {

        // init
        val nativeReader = mockReader()
        reader = buildOmapiReaderImpl(nativeReader)

        // test
        val seResponseList = reader.transmitSeRequests(getSampleSeRequest(), MultiSeRequestProcessing.PROCESS_ALL, ChannelControl.CLOSE_AFTER)
        Assert.assertNotNull(seResponseList)
    }

    @Test(expected = KeypleReaderIOException::class)
    @Ignore("Implementation of closePhysicalChannel() may encounter a IOException, it should be catched as a KeypleReaderIOException, it is not")
    fun exceptionOnChannelAfterTransmit() {
        // init
        val nativeReader = mockReaderWithExceptionOnCloseChannel(IOException())
        reader = buildOmapiReaderImpl(nativeReader)

        // test
        reader.transmitSeRequests(getSampleSeRequest(), MultiSeRequestProcessing.PROCESS_ALL, ChannelControl.CLOSE_AFTER)
    }

    @Test(expected = KeypleReaderIOException::class)
    fun exceptionWhileTransmittingApdu() {
        // init
        val nativeReader = mockReaderWithExceptionWhileTransmittingApdu(IOException())
        reader = buildOmapiReaderImpl(nativeReader)

        // test
        reader.transmitSeRequests(getSampleSeRequest(), MultiSeRequestProcessing.FIRST_MATCH, ChannelControl.KEEP_OPEN)
    }

    private fun getSampleSeRequest(): List<SeRequest> {

        val poApduRequestList = listOf(ApduRequest(ByteArrayUtil.fromHex("0000"), true))

        val seRequest = SeRequest(SeSelector.builder()
                .seProtocol(SeCommonProtocols.PROTOCOL_ISO7816_3)
                .aidSelector(SeSelector.AidSelector.builder()
                        .aidToSelect(PO_AID).build()).build(), poApduRequestList)

        val seRequestSet = ArrayList<SeRequest>()
        seRequestSet.add(seRequest)
        return seRequestSet
    }

    private fun getNoAidSampleSeRequest(): List<SeRequest> {

        val poApduRequestList = listOf(ApduRequest(ByteArrayUtil.fromHex("0000"), true))

        val seRequest = SeRequest(SeSelector.builder()
                .seProtocol(SeCommonProtocols.PROTOCOL_ISO7816_3).build(), poApduRequestList)

        val seRequestSet = ArrayList<SeRequest>()
        seRequestSet.add(seRequest)
        return seRequestSet
    }
}
