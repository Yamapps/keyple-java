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
package org.eclipse.keyple.core.seproxy.plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.eclipse.keyple.core.seproxy.plugin.state.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link AbstractThreadedObservableLocalReader} class implements monitoring functions for a
 * local reader based on a self-managed execution thread.
 * <p>
 * The thread is started when the first observation is added and stopped when the last observation
 * is removed.
 * <p>
 * It manages a machine in a currentState that conforms to the definitions given in
 * {@link AbstractObservableLocalReader}
 */
public abstract class AbstractThreadedObservableLocalReader extends AbstractObservableLocalReader {
    /** logger */
    private static final Logger logger =
            LoggerFactory.getLogger(AbstractThreadedObservableLocalReader.class);

    protected ExecutorService executorService;
    private long timeoutSeInsert = 10000;//TODO make this configurable
    private long timeoutSeRemoval = 10000;//TODO make this configurable

    /**
     * Reader constructor
     * @param pluginName the name of the plugin that instantiated the reader
     * @param readerName the name of the reader
     */
    public AbstractThreadedObservableLocalReader(String pluginName, String readerName) {
        this(pluginName, readerName, Executors.newSingleThreadExecutor());


    }

    /**
     *  Reader constructor
     * @param pluginName the name of the plugin that instantiated the reader
     * @param readerName the name of the reader
     * @param executorService if needed a executor can be specify, else a embedded single threaded executor will be used
     */
    public AbstractThreadedObservableLocalReader(String pluginName, String readerName,
            ExecutorService executorService) {
        super(pluginName, readerName);
        this.executorService = executorService;
        /* Init state */
        switchState(getInitState());
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    @Override
    protected Map<AbstractObservableState.MonitoringState, AbstractObservableState> initStates() {
        Map<AbstractObservableState.MonitoringState, AbstractObservableState> states =
                new HashMap<AbstractObservableState.MonitoringState, AbstractObservableState>();
        states.put(AbstractObservableState.MonitoringState.WAIT_FOR_SE_INSERTION,
                new ThreadedWaitForSeInsertion(this, timeoutSeInsert));
        states.put(AbstractObservableState.MonitoringState.WAIT_FOR_SE_PROCESSING,
                new DefaultWaitForSeProcessing(this));
        states.put(AbstractObservableState.MonitoringState.WAIT_FOR_SE_REMOVAL,
                new ThreadedWaitForSeRemoval(this, timeoutSeRemoval));
        states.put(AbstractObservableState.MonitoringState.WAIT_FOR_START_DETECTION,
                new DefaultWaitForStartDetect(this));
        return states;
    }



    // static final AtomicInteger threadCount = new AtomicInteger();

    /**
     * Add a reader observer.
     * <p>
     * The observer will receive all the events produced by this reader (card insertion, removal,
     * etc.)
     * <p>
     * In the case of a {@link AbstractThreadedObservableLocalReader}, a thread is created if it
     * does not already exist (when the first observer is added).
     *
     * @param observer the observer object public final void
     *        addObserver(ObservableReader.ReaderObserver observer) { super.addObserver(observer);
     *        // if an observer is added to an empty list, start the observation if
     *        (super.countObservers() == 1) { //instantiateThread();
     *        currentState.onEvent(StateEvent.START_DETECT); } }
     */

    /**
     * Remove a reader observer.
     * <p>
     * The observer will not receive any of the events produced by this reader.
     * <p>
     * Terminate the monitoring thread if {@link AbstractThreadedObservableLocalReader}.
     * <p>
     * The thread is created if it does not already exist
     *
     * @param observer the observer object public final void
     *        removeObserver(ObservableReader.ReaderObserver observer) {
     *        super.removeObserver(observer); if (super.countObservers() == 0) {
     * 
     *        //if (thread != null) { // //stopThread(); //}
     * 
     *        currentState.onEvent(StateEvent.STOP_DETECT); } }
     */

    /**
     * Remove all the observers of the reader
     * 
     * @Override public final void clearObservers() { super.clearObservers();
     * 
     *           //if (thread != null) { // //stopThread(); //}
     * 
     *           currentState.onEvent(StateEvent.STOP_DETECT); }
     */
    /*
     * private void instantiateThread() { logger.debug("Start monitoring the reader {}",
     * this.getName()); thread = new EventThread(this);
     * 
     * }
     * 
     * private void startThread() { if (thread != null) { thread.start(); } }
     * 
     * private void stopThread() { logger.debug("Stop the reader monitoring."); thread.end(); thread
     * = null; }
     */

    /**
     * In addition to the processing done by the super method, this method starts the monitoring
     * process. //TODO OD: does not, does it?
     * 
     * @param defaultSelectionsRequest the {@link AbstractDefaultSelectionsRequest} to be executed
     *        when a SE is inserted
     * @param notificationMode the notification mode enum (ALWAYS or MATCHED_ONLY)
     * @Override public final void setDefaultSelectionRequest( AbstractDefaultSelectionsRequest
     *           defaultSelectionsRequest, ObservableReader.NotificationMode notificationMode) {
     *           super.setDefaultSelectionRequest(defaultSelectionsRequest, notificationMode); }
     */

    /*
     * @Override public void startSeDetection(ObservableReader.PollingMode pollingMode) {
     * super.startSeDetection(pollingMode); // unleash the monitoring thread to initiate the SE
     * detection (if available and needed)
     * 
     * //if (thread != null) { // thread.startSeDetection(); //}
     * 
     * //currentState.onEvent(StateEvent.START_DETECT); }
     */

    /*
     * @Override public void stopSeDetection() { // unleash the monitoring thread to initiate the SE
     * detection (if available and needed)
     * 
     * //if (thread != null) { // thread.stopSeDetection(); //}
     * 
     * currentState.onEvent(StateEvent.STOP_DETECT); }
     */

    /**
     * Initiates the removal sequence
     * 
     * @Override protected final void startRemovalSequence() {
     * 
     *           // if (thread != null) { // thread.startRemovalSequence(); // }
     *           currentState.onEvent(StateEvent.SE_PROCESSED); }
     */



    /**
     */

    // private EventThread thread;

    /**
     * Thread wait timeout in ms
     * <p>
     * This value will be used to avoid infinite waiting time. When set to 0 (default), no timeout
     * check is performed.
     * <p>
     * See setThreadWaitTimeout method.
     */
    private long threadWaitTimeout = 0;





    private class EventThread extends Thread {

        /**
         * Reader instance that runs this thread
         */

        AbstractThreadedObservableLocalReader reader;

        /**
         * If the thread should be kept a alive
         */
        private volatile boolean running = true;

        /**
         * Current reader currentState private volatile MonitoringState monitoringState =
         * MonitoringState.WAIT_FOR_START_DETECTION;
         */

        /**
         * previous currentState (logging purposes) private volatile MonitoringState previousState =
         * MonitoringState.WAIT_FOR_START_DETECTION;
         */

        /**
         * Synchronization objects and flags TODO Improve this mechanism by using classes and
         * methods of the java.util.concurrent package
         */
        private final Object waitForStartDetectionSync = new Object();
        private final Object waitForSeProcessing = new Object();

        // these flags help to distinguish notify and timeout when wait is exited.
        private boolean startDetectionNotified = false;
        private boolean seProcessingNotified = false;
        private volatile boolean stopDetectionNotified = false;

        /**
         * Constructor
         *
         * @param reader, reader that runs this thread
         */
        EventThread(AbstractThreadedObservableLocalReader reader) {
            /*
             * super("observable-reader-events-" + threadCount.addAndGet(1) +
             * reader.getName().replace(" ", ""));
             */
            super("observable-reader-events-" + reader.getName().replace(" ", ""));

            logger.debug("Instantiate thread with name {} for reader {}", this.getName(),
                    reader.getName());
            setDaemon(true);
            this.reader = reader;
        }

        /**
         * Marks the thread as one that should end when the last cardWaitTimeout occurs
         */
        void end() {
            running = false;
            this.interrupt(); // exit io wait if needed
        }

        /**
         * Makes the current change from WAIT_FOR_START_DETECTION to WAIT_FOR_SE_INSERTION
         */
        void startSeDetection() {
            logger.debug("Start SeDetection on thread with name {} for reader {}", this.getName(),
                    this.reader);
            startDetectionNotified = true;
            synchronized (waitForStartDetectionSync) {
                waitForStartDetectionSync.notify();
            }
        }

        /**
         * Makes the current change to WAIT_FOR_START_DETECTION
         */
        void stopSeDetection() {
            logger.debug("Stop SeDetection on thread with name {} for reader {}", this.getName(),
                    this.reader.getName());
            stopDetectionNotified = true;
        }


        /**
         * Makes the current change from WAIT_FOR_SE_PROCESSING to WAIT_FOR_SE_REMOVAL.
         * <p>
         * Handle the signal from the application to terminate the operations with the current SE
         * (ChannelControl set to CLOSE_AFTER).
         * <p>
         * We handle here two different cases:
         * </p>
         * <ul>
         * <li>the notification is executed in the same thread (reader monitoring thread): in this
         * case the seProcessingNotified flag is set when processSeInserted/notifyObservers/update
         * ends. The monitoring thread can continue without having to wait for the end of the SE
         * processing.</li>
         * <li>the notification is executed in a separate thread: in this case the processSeInserted
         * method will have finished before the end of the SE processing and the reader monitoring
         * thread is already waiting with the waitForRemovalSync object. Here we release the
         * waitForRemovalSync object by calling its notify method.</li>
         * </ul>
         */
        void startRemovalSequence() {
            logger.debug("Start RemovalSequence on thread with name {} for reader {}",
                    this.getName(), this.reader.getName());
            seProcessingNotified = true;
            synchronized (waitForSeProcessing) {
                waitForSeProcessing.notify();
            }
        }


        /**
         * Return monitoring currentState
         *
         * @return MonitoringState AbstractObservableState.MonitoringState getMonitoringState() {
         *         return this.currentState.monitoringState; }
         */

        /**
         * Thread loop
         */
        public void run() {
            long startTime; // timeout management
            while (running) {
                /*
                 * logger.trace("Reader currentState machine: previous {}, new {}", previousState,
                 * monitoringState); previousState = monitoringState;
                 * 
                 * try { switch (monitoringState) { case WAIT_FOR_START_DETECTION: // We are waiting
                 * for the application to start monitoring SE insertions // with the call to
                 * startSeDetection.
                 * 
                 * // We notify the application of the current currentState. // notifyObservers( //
                 * new ReaderEvent(this.pluginName, AbstractLocalReader.this.name, //
                 * ReaderEvent.EventType.AWAITING_SE_START_DETECTION, null));
                 * 
                 * // to distinguish between timeout and notification startDetectionNotified =
                 * false;
                 * 
                 * // Loop until we are notified (call to setDefaultSelectionRequest) or //
                 * interrupted (call to end) while (true) { synchronized (waitForStartDetectionSync)
                 * { // sleep a little waitForStartDetectionSync
                 * .wait(WAIT_FOR_SE_DETECTION_EXIT_LATENCY); } if (startDetectionNotified) { // the
                 * application has requested the start of monitoring monitoringState =
                 * MonitoringState.WAIT_FOR_SE_INSERTION; // exit loop break; } if
                 * (Thread.interrupted()) { // a request to stop the thread has been made running =
                 * false; // exit loop break; } } // exit switch break; case WAIT_FOR_SE_INSERTION:
                 * // We are waiting for the reader to inform us that a card is inserted. while
                 * (true) { if (stopDetectionNotified) { stopDetectionNotified = false; // reset
                 * flag // the application has requested the start of monitoring monitoringState =
                 * MonitoringState.WAIT_FOR_START_DETECTION; // exit loop break; }
                 * 
                 * 
                 * if (((SmartInsertionReader) this.reader)// TODO OD : if object is // not
                 * SmartInsertionReader? .waitForCardPresent(WAIT_FOR_SE_INSERTION_EXIT_LATENCY)) {
                 * seProcessingNotified = false; // a SE has been inserted, the following process //
                 * (processSeInserted) will end with a SE_INSERTED or // SE_MATCHED notification
                 * according to the // DefaultSelectionRequest. // If a DefaultSelectionRequest is
                 * set with the MATCHED_ONLY // flag and the SE presented does not match, then the
                 * // processSeInserted method will return false to indicate // that this SE can be
                 * ignored. if (processSeInserted()) { // Note: the notification to the application
                 * was made by // processSeInserted // We'll wait for the end of its processing
                 * monitoringState = MonitoringState.WAIT_FOR_SE_PROCESSING; } else { // An
                 * unexpected SE has been detected, we wait for its // removal monitoringState =
                 * MonitoringState.WAIT_FOR_SE_REMOVAL; } // exit loop break; } if
                 * (Thread.interrupted()) { // a request to stop the thread has been made running =
                 * false; // exit loop break; } } // exit switch break; case WAIT_FOR_SE_PROCESSING:
                 * // loop until notification of the end of the SE processing operation, an // SE
                 * withdrawal or a request to stop is made. // An global timeout period is also
                 * checked to avoid infinite waiting; // exceeding the time limit leads to the
                 * notification of an // TIMEOUT_ERROR // event and stops monitoring startTime =
                 * System.currentTimeMillis(); while (true) { if (seProcessingNotified) { // the
                 * application has completed the processing, we move to the // SE if
                 * (currentPollingMode == ObservableReader.PollingMode.CONTINUE) { monitoringState =
                 * MonitoringState.WAIT_FOR_SE_REMOVAL; } else { // We close the channels now and
                 * notify the application of // the SE_REMOVED event. processSeRemoved();
                 * monitoringState = MonitoringState.WAIT_FOR_START_DETECTION; } // exit loop break;
                 * } // TODO OD : why test instanceof SmartPresenceReader and use // isSePresent? if
                 * (this.reader instanceof SmartPresenceReader && !isSePresent()) { // the SE has
                 * been removed, we return to the currentState of waiting // for insertion // We
                 * notify the application of the SE_REMOVED event. processSeRemoved();
                 * monitoringState = MonitoringState.WAIT_FOR_SE_INSERTION; // exit loop break; }
                 * else { logger.trace(
                 * "Reader is not SmartPresenceReader : please notify finalize  currentState of SeProcessing"
                 * ); } if (Thread.interrupted()) { // a request to stop the thread has been made
                 * running = false; // exit loop break; } if (this.reader.getThreadWaitTimeout() !=
                 * 0 && System.currentTimeMillis() - startTime > this.reader
                 * .getThreadWaitTimeout()) { // update the new currentState before notify (unit
                 * test issue) monitoringState = MonitoringState.WAIT_FOR_START_DETECTION; // We
                 * notify the application of the TIMEOUT_ERROR event. notifyObservers(new
                 * ReaderEvent(this.reader.getPluginName(), this.reader.getName(),
                 * ReaderEvent.EventType.TIMEOUT_ERROR, null)); logger.error(
                 * "The SE's processing time has exceeded the specified limit."); // exit loop
                 * break; } synchronized (waitForSeProcessing) { // sleep a little
                 * waitForSeProcessing.wait(WAIT_FOR_SE_PROCESSING_EXIT_LATENCY); } } // exit switch
                 * break; case WAIT_FOR_SE_REMOVAL: // We are waiting for the reader to inform us
                 * when a card is inserted. // An global timeout period is also checked to avoid
                 * infinite waiting; // exceeding the time limit leads to the notification of an //
                 * TIMEOUT_ERROR // event and stops monitoring startTime =
                 * System.currentTimeMillis(); while (true) { if (((this.reader instanceof
                 * SmartPresenceReader) && ((SmartPresenceReader)
                 * AbstractThreadedObservableLocalReader.this) .waitForCardAbsentNative(
                 * WAIT_FOR_SE_REMOVAL_EXIT_LATENCY)) || ((!(this.reader instanceof
                 * SmartPresenceReader)) && !this.reader.isSePresentPing())) { // the SE has been
                 * removed, we close all channels and return to // the currentState of waiting //
                 * for insertion // We notify the application of the SE_REMOVED event.
                 * processSeRemoved(); if (currentPollingMode ==
                 * ObservableReader.PollingMode.CONTINUE) { monitoringState =
                 * MonitoringState.WAIT_FOR_SE_INSERTION; } else { // We close the channels now and
                 * notify the application of // the SE_REMOVED event. processSeRemoved();// TODO OD
                 * : already done in line 478 monitoringState =
                 * MonitoringState.WAIT_FOR_START_DETECTION; } // exit loop break; } else { if
                 * (!(AbstractThreadedObservableLocalReader.this instanceof SmartPresenceReader)) {
                 * // we just send a "ping" APDU and the communication was // successful, we sleep a
                 * // little to avoid too intensive processing. try { Thread.sleep(30);// TODO JPF:
                 * use constant } catch (InterruptedException e) { // forwards the exception
                 * upstairs Thread.currentThread().interrupt(); } } } if (threadWaitTimeout != 0 &&
                 * System.currentTimeMillis() - startTime > threadWaitTimeout) { // update the new
                 * currentState before notify (unit test issue) monitoringState =
                 * MonitoringState.WAIT_FOR_START_DETECTION; // We notify the application of the
                 * TIMEOUT_ERROR event. notifyObservers(new ReaderEvent(this.reader.getPluginName(),
                 * this.reader.getName(), ReaderEvent.EventType.TIMEOUT_ERROR, null)); logger.warn(
                 * "The time limit for the removal of the SE has been exceeded."); // exit loop
                 * break; } } // exit switch break; } } catch (InterruptedException ex) {
                 * logger.debug("Exiting monitoring thread."); running = false; } catch
                 * (NoStackTraceThrowable e) {
                 * logger.trace("[{}] Exception occurred in monitoring thread: {}",
                 * this.reader.getName(), e.getMessage()); running = false; }
                 */

            }
        }
    }


}
