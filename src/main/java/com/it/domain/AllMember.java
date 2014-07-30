/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.it.domain;

import java.util.List;
import java.util.Map;

public class AllMember {
  public static AllMember instance = new AllMember();
  private Member me = null;
  private Clusters clusters = new Clusters();
  private MemberInfos memberInfos = new MemberInfos();

  public static AllMember getInstance() {
    return instance;
  }

  public Member me() {
    if (me == null) {
      me = getClusters().findMe();
    }
    return me;
  }

  public void addClusters(String[] clusterNames) {
    for (String clusterName : clusterNames) {
      addCluster(clusterName);
    }
  }

  public void addCluster(String clusterName) {
    clusters.createCluster(clusterName);
  }

  public void addMember(String clusterName, Member member) {
    clusters.addMember(clusterName, member);
  }

  public void removeCluster(String clusterName) {
    clusters.remove(clusterName);
  }

  public void removeMember(String clusterName, Member member) {
    clusters.remove(clusterName, member);
  }

  public Clusters getClusters() {
    return clusters;
  }

  public Map<String, MemberList> getMemberListMap() {
    return clusters.getMemberListMap();
  }

  public MemberList getMemberListIn(String cluster) {
    return clusters.getMemberListIn(cluster);
  }

  public List<Member> getMembers(String cluster) {
    return clusters.getMemberListIn(cluster).getMembers();
  }

  public Member getMemberByDataPort(String host, int dataPort) {
    for (String clusterName : clusters.getClusterNames()) {
      Member member = clusters.getMemberListIn(clusterName).findMemberByDataPort(host, dataPort);
      if (member != null) {
        return member;
      }
    }
    return null;
  }

  public Member getMemberByClusterAndId(String cluster, int id) {
    MemberList memberList = clusters.getMemberListIn(cluster);
    if (memberList == null) {
      return null;
    }

    return memberList.getMember(id);
  }

  public MemberInfos getMemberInfos() {
    return memberInfos;
  }

  @Override
  public String toString() {
    return clusters.toString();
  }
}
