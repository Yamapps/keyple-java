/* **************************************************************************************
 * Copyright (c) 2018 Calypso Networks Association https://www.calypsonet-asso.org/
 *
 * See the NOTICE file(s) distributed with this work for additional information
 * regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 ************************************************************************************** */
package org.eclipse.keyple.core.seproxy.plugin;

import org.eclipse.keyple.core.seproxy.event.ObservablePlugin;
import org.eclipse.keyple.core.seproxy.event.PluginEvent;

/**
 * The {@link ObservablePluginNotifier} interface provides the API to notify the observers of an
 * {@link ObservablePlugin}
 */
public interface ObservablePluginNotifier extends ObservablePlugin {
  /**
   * Push a PluginEvent of the {@link ObservablePluginNotifier} to its registered observers.
   *
   * @param event the event (see {@link PluginEvent})
   */
  void notifyObservers(final PluginEvent event);
}
