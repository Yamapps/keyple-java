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
package org.eclipse.keyple.example.remote.transport.wspolling.client_retrofit;

import java.io.IOException;
import java.util.Properties;
import java.util.Random;
import org.eclipse.keyple.example.remote.transport.wspolling.server.WsPServer;
import org.eclipse.keyple.plugin.remotese.transport.factory.ClientNode;
import org.eclipse.keyple.plugin.remotese.transport.factory.ServerNode;
import org.eclipse.keyple.plugin.remotese.transport.factory.TransportFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Web service factory, get @{@link WsPRetrofitClientImpl} and {@link WsPServer} Optimized for
 * Android and Java
 */
@SuppressWarnings("PMD.AvoidUsingHardCodedIP")
public class WsPollingFactory extends TransportFactory {

  // default values
  private final String pollingUrl = "/polling";
  private final String keypleUrl = "/keypleDTO";
  private Integer port = 8000 + new Random().nextInt((100) + 1);
  private String hostname = "0.0.0.0";
  private String protocol = "http://";
  private String serverNodeId;

  private static final Logger logger = LoggerFactory.getLogger(WsPollingFactory.class);

  public WsPollingFactory(String serverNodeId) {
    this.serverNodeId = serverNodeId;
  }

  public WsPollingFactory(String serverNodeId, String protocol, String hostname, Integer port) {
    this.port = port;
    this.serverNodeId = serverNodeId;
    this.hostname = hostname;
    this.protocol = protocol;
  }

  public WsPollingFactory(Properties serverProp, String serverNodeId) {
    if (serverProp.containsKey("server.port")) {
      this.port = Integer.decode(serverProp.getProperty("server.port"));
    }
    if (serverProp.containsKey("server.hostname")) {
      this.hostname = serverProp.getProperty("server.hostname");
    }

    if (serverProp.containsKey("server.protocol")) {
      this.protocol = serverProp.getProperty("server.protocol") + "://";
    }

    this.serverNodeId = serverNodeId;
  }

  @Override
  public ClientNode getClient(String clientNodeId) {

    String baseUrl = protocol + hostname + ":" + port;
    logger.info("Create RETROFIT Ws Polling Client to {}", baseUrl);
    return new WsPRetrofitClientImpl(baseUrl, clientNodeId, serverNodeId);
  }

  @Override
  public ServerNode getServer() throws IOException {

    logger.info("Create Ws Polling Server at {}:{}", hostname, port);
    return new WsPServer(hostname, port, keypleUrl, pollingUrl, serverNodeId);
  }

  @Override
  public String getServerNodeId() {
    return serverNodeId;
  }

  public String getBaseUrl() {
    return protocol + hostname + ":" + port;
  }
}
