/*
 * licensed to the apache software foundation (asf) under one or more contributor license
 * agreements. see the notice file distributed with this work for additional information regarding
 * copyright ownership. the asf licenses this file to you under the apache license, version 2.0 (the
 * "license"); you may not use this file except in compliance with the license. you may obtain a
 * copy of the license at
 *
 * http://www.apache.org/licenses/license-2.0
 *
 * unless required by applicable law or agreed to in writing, software distributed under the license
 * is distributed on an "as is" basis, without warranties or conditions of any kind, either express
 * or implied. see the license for the specific language governing permissions and limitations under
 * the license.
 */

package com.it.command;

import java.io.Serializable;

public class Command implements Serializable {
  private static final long serialVersionUID = 4258334791271723894L;

  private String myCluster;
  private int myId;

  public Command() {}

  public Command(String myCluster, int myId) {
    this.myCluster = myCluster;
    this.myId = myId;
  }

  public String getMyCluster() {
    return myCluster;
  }

  public int getMyId() {
    return myId;
  }

  @Override
  public String toString() {
    return myCluster + "." + myId;
  }
}
