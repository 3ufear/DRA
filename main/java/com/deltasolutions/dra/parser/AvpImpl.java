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

package com.deltasolutions.dra.parser;

import com.deltasolutions.dra.base.Avp;
import com.deltasolutions.dra.base.AvpDataException;
import com.deltasolutions.dra.base.AvpSet;
import com.deltasolutions.dra.base.InternalException;

import java.net.InetAddress;
import java.util.Date;


public class AvpImpl implements Avp {

  private static final long serialVersionUID = 1L;
  private static final ElementParser parser = new ElementParser();
  int avpCode;
  long vendorID;

  boolean isMandatory = false;
  boolean isEncrypted = false;
  boolean isVendorSpecific = false;

  byte[] rawData = new byte[0];
  AvpSet groupedData;

  AvpImpl(int code, int flags, long vnd, byte[] data) {
    avpCode  = code;
    //
    isMandatory = (flags & 0x40) != 0;
    isEncrypted = (flags & 0x20) != 0;
    isVendorSpecific = (flags & 0x80) != 0;
    //
    vendorID = vnd;
    rawData  = data;

  }

  AvpImpl(Avp avp) {
    avpCode     = avp.getCode();
    vendorID    = avp.getVendorId();
    isMandatory = avp.isMandatory();
    isEncrypted = avp.isEncrypted();
    isVendorSpecific = avp.isVendorId();
    try {
      rawData = avp.getRaw();
      if (rawData == null || rawData.length == 0) {
        groupedData = avp.getGrouped();
      }
    }
    catch (AvpDataException e) {

    }
  }

  public AvpImpl(int newCode, Avp avp) {
    this(avp);
    avpCode = newCode;
  }

  public int getCode() {
    return avpCode;
  }

  public boolean isVendorId() {
    return isVendorSpecific;
  }

  public boolean isMandatory() {
    return isMandatory;
  }

  public boolean isEncrypted() {
    return isEncrypted;
  }

  public long getVendorId() {
    return vendorID;
  }

  public byte[] getRaw() throws AvpDataException {
    return rawData;
  }

  public byte[] getOctetString() throws AvpDataException {
    return rawData;
  }

  public String getUTF8String() throws AvpDataException {
    try {
      return parser.bytesToUtf8String(rawData);
    }
    catch (Exception e) {
      throw new AvpDataException(e, this);
    }
  }

  public int getInteger32() throws AvpDataException {
    try {
      return parser.bytesToInt(rawData);
    }
    catch (Exception e) {
      throw new AvpDataException(e, this);
    }
  }

  public long getInteger64() throws AvpDataException {
    try {
      return parser.bytesToLong(rawData);
    }
    catch (Exception e) {
      throw new AvpDataException(e, this);
    }
  }

  public long getUnsigned32() throws AvpDataException {
    try {
      byte[] u32ext = new byte[8];
      System.arraycopy(rawData, 0, u32ext, 4, 4);
      return parser.bytesToLong(u32ext);
    }
    catch (Exception e) {
      throw new AvpDataException(e, this);
    }
  }

  public long getUnsigned64() throws AvpDataException {
    try {
      return parser.bytesToLong(rawData);
    }
    catch (Exception e) {
      throw new AvpDataException(e, this);
    }
  }

  public float getFloat32() throws AvpDataException {
    try {
      return parser.bytesToFloat(rawData);
    }
    catch (Exception e) {
      throw new AvpDataException(e, this);
    }
  }

  public double getFloat64() throws AvpDataException {
    try {
      return parser.bytesToDouble(rawData);
    }
    catch (Exception e) {
      throw new AvpDataException(e, this);
    }
  }

  public InetAddress getAddress() throws AvpDataException {
    try {
      return parser.bytesToAddress(rawData);
    }
    catch (Exception e) {
      throw new AvpDataException(e, this);
    }
  }

  public Date getTime() throws AvpDataException {
    try {
      return parser.bytesToDate(rawData);
    }
    catch (Exception e) {
      throw new AvpDataException(e, this);
    }
  }

  public String getDiameterIdentity() throws AvpDataException {
    try {
      return parser.bytesToOctetString(rawData);
    }
    catch (Exception e) {
      throw new AvpDataException(e, this);
    }
  }


  public AvpSet getGrouped() throws AvpDataException {
    try {
      if (groupedData == null) {
        groupedData = parser.decodeAvpSet(rawData);
        rawData = new byte[0];
      }
      return groupedData;
    }
    catch (Exception e) {
      throw new AvpDataException(e, this);
    }
  }

  public boolean isWrapperFor(Class<?> aClass) throws InternalException {
    return false;
  }

  public <T> T unwrap(Class<T> aClass) throws InternalException {
    return null;  
  }

  public byte[] getRawData() {
    return (rawData == null || rawData.length == 0) ? parser.encodeAvpSet(groupedData) : rawData;
  }

  // Caching toString.. Avp shouldn't be modified once created.
  private String toString;

  @Override
  public String toString() {
    if(toString == null) {
      this.toString = new StringBuffer("AvpImpl [avpCode=").append(avpCode).append(", vendorID=").append(vendorID).append("]@").append(super.hashCode()).toString();
    }

    return this.toString;
  }
}
