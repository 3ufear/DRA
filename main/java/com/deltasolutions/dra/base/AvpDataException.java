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

/**
 * The AvpDataException signals invalid operations on Avp data.
 * 
 * @author erick.svenson@yahoo.com
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class AvpDataException extends Exception {

  private static final long serialVersionUID = -5898449417016355792L;

  protected Avp avp; 

  /**
   * Default constructor
   */
  public AvpDataException(Avp avp) {
    super();
    this.avp = avp;
  }

  /**
   * Constructor with reason string
   * @param message reason string
   */
  public AvpDataException(String message, Avp avp) {
    super(message);
    this.avp = avp;
  }

  /**
   * Constructor with reason string and parent exception
   * @param message message reason string
   * @param cause parent exception
   */
  public AvpDataException(String message, Throwable cause, Avp avp) {
    super(message, cause);
    this.avp = avp;
  }

  /**
   * Constructor with parent exception
   * @param cause  parent exception
   */
  public AvpDataException(Throwable cause, Avp avp) {
    super(cause);
    this.avp = avp;
  }

  /**
   * Default constructor
   */
  public AvpDataException() {
    super();
  }

  /**
   * Constructor with reason string
   * @param message reason string
   */
  public AvpDataException(String message) {
    super(message);
  }

  /**
   * Constructor with reason string and parent exception
   * @param message message reason string
   * @param cause parent exception
   */
  public AvpDataException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructor with parent exception
   * @param cause  parent exception
   */
  public AvpDataException(Throwable cause) {
    super(cause);
  }

    public AvpDataException(String message, int code, long vendorId) {

    }

    public AvpDataException(String s, AvpDataException e, int code, long vendor) {

    }

    public Avp getAvp() {
    return avp;
  }

}
