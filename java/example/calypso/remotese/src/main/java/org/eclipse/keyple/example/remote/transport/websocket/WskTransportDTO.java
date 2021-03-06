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
package org.eclipse.keyple.example.remote.transport.websocket;

import org.eclipse.keyple.plugin.remotese.transport.DtoSender;
import org.eclipse.keyple.plugin.remotese.transport.model.KeypleDto;
import org.eclipse.keyple.plugin.remotese.transport.model.TransportDto;
import org.java_websocket.WebSocket;

/** Web socket Transport DTO */
class WskTransportDTO implements TransportDto {

  public KeypleDto getDto() {
    return dto;
  }

  private final KeypleDto dto;
  private final WebSocket socketWeb;

  public WskTransportDTO(KeypleDto dto, WebSocket socketWeb) {
    this.dto = dto;
    this.socketWeb = socketWeb;
  }

  public WskTransportDTO(KeypleDto dto, WebSocket socketWeb, DtoSender wskNode) {
    this.dto = dto;
    this.socketWeb = socketWeb;
  }

  @Override
  public KeypleDto getKeypleDTO() {
    return dto;
  }

  @Override
  public TransportDto nextTransportDTO(KeypleDto keypleDto) {

    return new WskTransportDTO(keypleDto, this.socketWeb);
  }

  public WebSocket getSocketWeb() {
    return socketWeb;
  }
}
