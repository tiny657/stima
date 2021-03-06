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

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Maps;

public class Clusters implements Serializable {
  private static final long serialVersionUID = -8267158344891748076L;

  private Map<String, MemberList> memberListMap = Maps.newTreeMap();  // clusterName, Members
  transient private static final MemberList EMPTY_MEMBERLIST = new MemberList();

  public Member findMe() {
    for (Entry<String, MemberList> entry : memberListMap.entrySet()) {
      Member member = entry.getValue().findMe();
      if (member != null) {
        return member;
      }
    }
    return null;
  }

  public Member findMember(String host, int port) {
    for (Entry<String, MemberList> entry : memberListMap.entrySet()) {
      Member member = entry.getValue().findMemberByDataPort(host, port);
      if (member != null) {
        return member;
      }
    }
    return null;
  }

  public void createCluster(String clusterName) {
    if (!memberListMap.containsKey(clusterName)) {
      memberListMap.put(clusterName, new MemberList());
    }
  }

  public void remove(String clusterName) {
    memberListMap.remove(clusterName);
  }

  public void addMember(String clusterName, Member member) {
    MemberList memberList = getMemberListIn(clusterName);
    if (memberList == EMPTY_MEMBERLIST) {
      createCluster(clusterName);
      memberList = getMemberListIn(clusterName);
    }
    memberList.addMember(member);
  }

  public void remove(String clusterName, Member member) {
    MemberList memberList = getMemberListIn(clusterName);
    if (memberList.hasMembers()) {
      memberList.removeMember(member);
    }
  }

  @JsonIgnore
  public Set<String> getClusterNames() {
    return memberListMap.keySet();
  }

  public Map<String, MemberList> getMemberListMap() {
    return memberListMap;
  }

  public MemberList getMemberListIn(String cluster) {
    MemberList memberList = memberListMap.get(cluster);
    if (memberList == null) {
      return EMPTY_MEMBERLIST;
    }
    return memberList;
  }

  public Member nextRunningMember(String cluster) {
    MemberList memberList = getMemberListIn(cluster);
    return memberList.nextRunningMember();
  }

  public void setStatus(Member member, Status status) {
    member.setStatus(status);
  }

  public boolean equals(Clusters clusters) {
    if (memberListMap.size() != clusters.getClusterNames().size()) {
      return false;
    }

    for (String cluster : getClusterNames()) {
      if (getMemberListIn(cluster).getMembers().size() != clusters.getMemberListIn(cluster)
          .getMembers().size()) {
        return false;
      }

      Iterator<Member> iterator = getMemberListIn(cluster).getMembers().iterator();
      Iterator<Member> iterator2 = clusters.getMemberListIn(cluster).getMembers().iterator();
      while (iterator.hasNext()) {
        Member member = iterator.next();
        Member member2 = iterator2.next();
        if (!member.equals(member2)) {
          return false;
        }
      }
    }
    return true;
  }

  public Map<String, MemberList> diff(Clusters clusters) {
    Map<String, MemberList> result = Maps.newHashMap();
    for (String cluster : getClusterNames()) {
      MemberList memberList = getMemberListIn(cluster);
      if (memberList != EMPTY_MEMBERLIST) {
        result.put(cluster, memberList.diff(clusters.getMemberListIn(cluster)));
      }
    }
    return result;
  }

  @Override
  public String toString() {
    StringBuffer result = new StringBuffer("\n* member list\n");
    for (String cluster : memberListMap.keySet()) {
      result.append("cluster: ").append(cluster).append(", members: ")
          .append(memberListMap.get(cluster).toString()).append("\n");
    }

    return result.toString();
  }
}
