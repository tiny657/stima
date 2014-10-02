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

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.it.config.MemberConfig;
import com.it.exception.InvalidMemberException;

public class MemberListTest {
  @Test
  public void nextRunningMember() {
    // Given
    MemberConfig.getInstance().setMyRegion("region1");
    int count = 3, basePort = 5000, controlBasePort = 6000;
    List<Member> members = Lists.newArrayList();
    MemberList memberList = new MemberList();
    for (int i = 0; i < count; i++) {
      members.add(new Member("region1", i, "host", basePort + i, controlBasePort + i));
      memberList.addMember(members.get(i));
      memberList.setStatus("host", basePort + i, Status.RUNNING);
    }
    for (int i = count; i < count * 2; i++) {
      members.add(new Member("region2", i, "host2", basePort + i, controlBasePort + i));
      memberList.addMember(members.get(i));
      memberList.setStatus("host2", basePort + i, Status.RUNNING);
    }

    // When
    MemberConfig.getInstance().setRegionSeparation(true);
    Set<Member> nextMembers = Sets.newHashSet();
    for (int i = 0; i < count * 100; i++) {
      Member next = memberList.nextRunningMember();
      nextMembers.add(next);
    }

    MemberConfig.getInstance().setRegionSeparation(false);
    Set<Member> nextMembers2 = Sets.newHashSet();
    for (int i = 0; i < count * 100; i++) {
      Member next = memberList.nextRunningMember();
      nextMembers2.add(next);
    }

    // Then
    assertThat(nextMembers.size(), is(3));
    assertThat(nextMembers2.size(), is(6));
  }

  @Test
  public void setStatusWhenNotExists() {
    // Given
    MemberList memberList = new MemberList();
    memberList.addMember(new Member(1, "host", 1, 1001));

    // When
    boolean status = memberList.setStatus("host", 2, Status.RUNNING);

    // Then
    assertThat(status, is(false));
    assertThat(memberList.getRunningMembers().size(), is(0));
  }

  @Test
  public void setStatusToRunning() {
    // Given
    MemberList memberList = new MemberList();
    memberList.addMember(new Member(1, "host", 1, 1001));

    // When
    boolean status = memberList.setStatus("host", 1, Status.RUNNING);

    // Then
    assertThat(status, is(true));
    assertThat(memberList.getRunningMembers().size(), is(1));
  }

  @Test
  public void setStatusToNotRunning() {
    // Given
    MemberList memberList = new MemberList();
    memberList.addMember(new Member(1, "host", 1, 1001));
    memberList.setStatus("host", 1, Status.RUNNING);

    // When
    boolean isChanged = memberList.setStatus("host", 1, Status.SHUTDOWN);

    // Then
    assertThat(isChanged, is(true));
    assertThat(memberList.getRunningMembers().size(), is(0));
  }

  @Test
  public void findMember() {
    // Given
    MemberList memberList = new MemberList();
    memberList.addMember(new Member(1, "host", 1, 1001));

    // When
    Member findMember = memberList.findMemberByDataPort("host", 1);
    Member findMember2 = memberList.findMemberByDataPort("host", 2);

    // Then
    assertThat(findMember, notNullValue());
    assertThat(findMember2, nullValue());
  }

  @Test(expected = InvalidMemberException.class)
  public void isDuplicatedId() {
    // Given
    MemberList memberList = new MemberList();
    memberList.addMember(new Member(1, "host", 1, 1001));
    memberList.addMember(new Member(2, "host", 2, 1002));

    // When
    memberList.addMember(new Member(1, "host", 3, 1003));
  }

  @Test
  public void contains() {
    // Given
    Member member = new Member(1, "host", 1, 1001);
    MemberList memberList = new MemberList();
    memberList.addMember(member);

    // When
    boolean contains1 = memberList.contains(member);
    Member testMember = new Member(2, "host", 2, 1002);
    boolean contains2 = memberList.contains(testMember);

    // Then
    assertThat(contains1, is(true));
    assertThat(contains2, is(false));
  }

  @Test
  public void diff() {
    // Given
    MemberList memberList1 = new MemberList();
    memberList1.addMember(new Member(1, "host", 1, 1001));
    memberList1.addMember(new Member(2, "host", 2, 1002));
    memberList1.addMember(new Member(3, "host", 3, 1003));

    MemberList memberList2 = new MemberList();
    memberList2.addMember(new Member(4, "host", 3, 1003));
    memberList2.addMember(new Member(5, "host", 4, 1004));

    MemberList memberList3 = new MemberList();

    // When
    MemberList diff1 = memberList1.diff(memberList2);
    MemberList diff2 = memberList2.diff(memberList1);
    MemberList diff3 = memberList3.diff(memberList1);
    MemberList diff4 = memberList1.diff(memberList3);
    MemberList diff5 = memberList1.diff(null);

    // Then
    assertThat(diff1.getMembers().size(), is(2));
    assertThat(diff2.getMembers().size(), is(1));
    assertThat(diff3.getMembers().size(), is(0));
    assertThat(diff4.getMembers().size(), is(3));
    assertThat(diff5.getMembers().size(), is(3));
  }
}
