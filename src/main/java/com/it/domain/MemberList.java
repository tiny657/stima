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
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import com.it.config.MemberConfig;
import com.it.exception.InvalidMemberException;

public class MemberList implements Serializable {
  private static final long serialVersionUID = -5384674645915944849L;

  private static final Logger logger = LoggerFactory.getLogger(MemberList.class);

  private List<Member> members = Lists.newArrayList();
  transient private int index = 0;

  public MemberList() {}

  public boolean hasMembers() {
    return members.size() > 0;
  }

  public int size() {
    return members.size();
  }

  public Member findMe() {
    for (Member member : members) {
      if (member.isMe()) {
        return member;
      }
    }
    return null;
  }

  public Member getMember(int id) {
    for (Member member : members) {
      if (member.getId() == id) {
        return member;
      }
    }
    return null;
  }

  public List<Member> getMembers() {
    return members;
  }

  @JsonIgnore
  public List<Member> getRunningMembers() {
    List<Member> runningMembers = Lists.newArrayList();
    for (Member member : members) {
      if (member.isRunning()) {
        runningMembers.add(member);
      }
    }
    return runningMembers;
  }

  public Member nextRunningMember() {
    Member result = null;
    for (int i = 0; i < members.size(); i++) {
      Member member = members.get(next());
      if (MemberConfig.getInstance().getRegionSeparation()) {
        if (member.isRunning() && !member.isMe()
            && StringUtils.equals(member.getRegion(), MemberConfig.getInstance().getMyRegion())) {
          result = member;
          break;
        }
      } else {
        if (member.isRunning() && !member.isMe()) {
          result = member;
          break;
        }
      }
    }
    return result;
  }

  public int next() {
    index = (index + 1) % members.size();
    return index;
  }

  public boolean setStatus(Member member, Status status) {
    member.setStatus(status);
    return true;
  }

  public boolean setStatus(String host, int port, Status status) {
    Member member = findMemberByDataPort(host, port);
    if (member == null) {
      return false;
    }
    member.setStatus(status);

    return true;
  }

  public boolean contains(Member member) {
    if (findMemberByDataPort(member.getHost(), member.getDataPort()) == null) {
      return false;
    }
    return true;
  }

  public Member findMemberByDataPort(String host, int dataPort) {
    for (Member member : members) {
      if (member.equalsByDataPort(host, dataPort)) {
        return member;
      }
    }
    return null;
  }

  public Member findMemberByControlPort(String host, int controlPort) {
    for (Member member : members) {
      if (member.equalsByDataPort(host, controlPort)) {
        return member;
      }
    }
    return null;
  }

  public MemberList diff(MemberList memberList) {
    MemberList result = new MemberList();
    for (Member member : getMembers()) {
      if (memberList == null || !memberList.contains(member)) {
        result.addMember(member);
      }
    }
    return result;
  }

  // TODO: change member2
  public boolean isDuplicatedId(Member member2) {
    for (Member member : members) {
      if (member.getId() == member2.getId()) {
        return true;
      }
    }
    return false;
  }

  public void addMember(Member member) {
    if (isDuplicatedId(member)) {
      logger.error("The id({}) is duplicated.", member.getId());
      throw new InvalidMemberException("The id is duplicated.");
    }
    members.add(member);
  }

  public void removeMember(Member member) {
    members.remove(member);
  }

  @Override
  public String toString() {
    return members.toString();
  }
}
