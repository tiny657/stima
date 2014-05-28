package com.it.model;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class MemberTest {
    @Test
    public void equals() {
        // Given
        Member member1 = new Member("host", 1001);
        member1.setStatus(Status.RUNNING);
        Member member2 = new Member("host", 1001);
        member1.setStatus(Status.SHUTDOWN);

        // When
        boolean equals = member1.equals(member2);

        // Then
        assertThat(equals, is(true));
    }

    @Test
    public void notEquals() {
        // Given
        Member member1 = new Member("host", 1002);
        Member member2 = new Member("host", 1001);

        // When
        boolean equals = member1.equals(member2);

        // Then
        assertThat(equals, is(false));
    }

    @Test
    public void comparePort() {
        // Given
        Member member1 = new Member("host", 1001);
        Member member2 = new Member("host", 1002);

        // When
        int compare1 = member1.compareTo(member2);
        int compare2 = member2.compareTo(member1);

        // Then
        assertThat(compare1, lessThan(0));
        assertThat(compare2, greaterThan(0));
    }

    @Test
    public void compareHost() {
        // Given
        Member member1 = new Member("host1", 1001);
        Member member2 = new Member("host2", 1001);

        // When
        int compare1 = member1.compareTo(member2);
        int compare2 = member2.compareTo(member1);

        // Then
        assertThat(compare1, lessThan(0));
        assertThat(compare2, greaterThan(0));
    }
}
