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
package org.eclipse.keyple.core.seproxy.plugin.state;

import org.eclipse.keyple.core.seproxy.plugin.AbstractObservableLocalReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultWaitForSeInsertion extends AbstractObservableState {

    /** logger */
    private static final Logger logger = LoggerFactory.getLogger(DefaultWaitForSeInsertion.class);

    public DefaultWaitForSeInsertion(AbstractObservableLocalReader reader) {
        super(MonitoringState.WAIT_FOR_SE_INSERTION, reader);
    }

    @Override
    public void onEvent(AbstractObservableLocalReader.InternalEvent event) {
        logger.trace("[{}] onEvent => Event {} received in currentState {}", reader.getName(), event,
                state);
        switch (event) {
            case SE_INSERTED:
                if (this.reader.processSeInserted()) {
                    switchState(MonitoringState.WAIT_FOR_SE_PROCESSING);
                } else {
                    switchState(MonitoringState.WAIT_FOR_SE_REMOVAL);
                }
                break;

            case STOP_DETECT:
                switchState(MonitoringState.WAIT_FOR_START_DETECTION);
                break;

            case TIME_OUT:
                switchState(MonitoringState.WAIT_FOR_SE_INSERTION);
                break;

            default:
                logger.trace("Ignore event");
        }
    }

    /*
     * @Override protected void onSeInserted() {
     * logger.debug("Se Inserted event received for reader {}", reader.getName()); if
     * (this.reader.processSeInserted()) {
     * this.reader.switchState(MonitoringState.WAIT_FOR_SE_PROCESSING); }else{
     * this.reader.switchState(MonitoringState.WAIT_FOR_SE_REMOVAL); } }
     * 
     * @Override protected void onStartDetection() {
     * logger.debug("Start Detection event received for reader {}", reader.getName()); }
     * 
     * @Override protected void onStopDetection() {
     * logger.debug("Stop Detection event received for reader {}", reader.getName());
     * 
     * }
     * 
     * @Override protected void onSeProcessed() {
     * logger.debug("Se Processed event received for reader {}", reader.getName());
     * 
     * }
     * 
     * @Override protected void onSeRemoved() {
     * logger.debug("Se Removed event received for reader {}", reader.getName()); }
     * 
     */
    @Override
    public void activate() {}


    @Override
    public void deActivate() {}


}
