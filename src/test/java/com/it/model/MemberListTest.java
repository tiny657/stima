package com.it.model;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class MemberListTest {
    @Test
    public void randomRunningMember() {
        // Given
        int count = 3;
        List<Member> members = Lists.newArrayList();
        MemberList memberList = new MemberList();
        for (int i = 0; i < count; i++) {
            members.add(new Member("host", i));
            memberList.addMember(members.get(i));
            memberList.setStatus("host", i, true);
        }
        
        // When
        Set<Member> nextMembers = Sets.newHashSet();
        for (int i = 0; i < count * 100; i++) {
            Member next = memberList.nextRunningMember();
            nextMembers.add(next);
        }

        // Then
        assertThat(members.size(), is(3));
    }
    
    @Test
    public void setStatusWhenNotExists() {
        // Given
        MemberList memberList = new MemberList();
        memberList.addMember(new Member("host", 1));
        
        // When
        boolean status = memberList.setStatus("host", 2, true);
        
        // Then
        assertThat(status, is(false));
        assertThat(memberList.getRunningMembers().size(), is(0));
    }

    @Test
    public void setStatusToRunning() {
        // Given
        MemberList memberList = new MemberList();
        memberList.addMember(new Member("host", 1));
        
        // When
        boolean status = memberList.setStatus("host", 1, true);
        
        // Then
        assertThat(status, is(true));
        assertThat(memberList.getRunningMembers().size(), is(1));
    }
    
    @Test
    public void setStatusToNotRunning() {
        // Given
        MemberList memberList = new MemberList();
        memberList.addMember(new Member("host", 1));
        memberList.setStatus("host", 1, true);
        
        // When
        boolean status = memberList.setStatus("host", 1, false);
        
        // Then
        assertThat(status, is(true));
        assertThat(memberList.getRunningMembers().size(), is(0));
    }

    @Test
    public void findMember() {
        // Given
        MemberList memberList = new MemberList();
        memberList.addMember(new Member("host", 1));
        
        // When
        Member findMember = memberList.findMember("host", 1);
        Member findMember2 = memberList.findMember("host", 2);

        // Then
        assertThat(findMember, notNullValue());
        assertThat(findMember2, nullValue());
    }

    @Test
    public void contains() {
        // Given
        Member member = new Member("host", 1);
        MemberList memberList = new MemberList();
        memberList.addMember(member);
        
        // When
        boolean contains1 = memberList.contains(member);
        Member testMember = new Member("host", 2);
        boolean contains2 = memberList.contains(testMember);

        // Then
        assertThat(contains1, is(true));
        assertThat(contains2, is(false));
    }
    
    @Test
    public void diff() {
        // Given
        MemberList memberList1 = new MemberList();
        memberList1.addMember(new Member("host", 1));
        memberList1.addMember(new Member("host", 2));
        memberList1.addMember(new Member("host", 3));

        MemberList memberList2 = new MemberList();
        memberList2.addMember(new Member("host", 3));
        memberList2.addMember(new Member("host", 4));
        
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