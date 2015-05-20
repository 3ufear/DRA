/*
 * JBoss, Home of Professional Open Source
 * Copyright 2006, Red Hat, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package com.deltasolutions.dra.base;

public interface IMessage {

  /**
   * The message is not sent to the network
   */
  int STATE_NOT_SENT = 0;

  /**
   * 	The message has been sent to the network
   */
  int STATE_SENT = 1;

  /**
   * The message is buffered ( not use yet )
   */
  int STATE_BUFFERED = 2;

  /**
   *  Stack received answer to this message
   */
  int STATE_ANSWERED = 3;

  /**
   * Return state of message
   * @return state of message
   */
  int getState();

  /**
   * Set new state
   * @param newState new state value
   */
  void setState(int newState);

  /**
   * Return header applicationId
   * @return header applicationId
   */
  long getHeaderApplicationId();

  /**
   * Set header message application id
   * @param applicationId header message application id
   */
  void setHeaderApplicationId(long applicationId);

  /**
   * Return flags as inteher
   * @return flags as inteher
   */
  int getFlags();

  /**
   * Create timer for request timout procedure
   * @param scheduledFacility timer facility
   * @param timeOut value of timeout
   * @param timeUnit time unit
   */

  /**
   * Set hop by hop id
   * @param hopByHopId   hopByHopId value
   */
  void setHopByHopIdentifier(long hopByHopId);

  /**
   * Set end by end id
   * @param endByEndId  endByEndId value
   */
  void setEndToEndIdentifier(long endByEndId);

  /**
   * Return application id
   * @return application id
   */
  ApplicationId getSingleApplicationId();

  /**
   * Return application id
   * @return application id
   */
  ApplicationId getSingleApplicationId(long id);

  /**
   * Return duplication key of message
   * @return duplication key of message
   */
  String getDuplicationKey();

  /**
   * Generate duplication key
   * @param host origination host
   * @param endToEndId end to end id
   * @return duplication key
   */
  String getDuplicationKey(String host, long endToEndId);

  /**
   * Create clone object
   * @return clone
   */
  Object clone();

  AvpSet getAvps();

  void setRequest(boolean request);

  boolean isRequest();

  long getHopByHopIdentifier();

  long getEndToEndIdentifier();

  int getCommandCode();

  void setNetworkRequest(boolean isNetwork);

  boolean isNetworkRequest();

  String getSessionId();

}
