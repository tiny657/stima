package com.it.domain;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import org.joda.time.DateTime;
import org.junit.Test;

import com.it.exception.InvalidMemberException;

public class MemberTest {

  @Test
  public void validatePort() {
    // When
    Member memberWithValidPort = new Member(1, "host", 1);
    Member memberWithInvalidPort2 = new Member(2, "host", 30000);
    Member memberWithInvalidPort3 = new Member(3, "host", 65535);
  }

  @Test(expected = InvalidMemberException.class)
  public void validatePort0() {
    // When
    Member memberWithValidPort = new Member(1, "host", 0);
  }

  @Test(expected = InvalidMemberException.class)
  public void validatePort65536() {
    // When
    Member memberWithValidPort = new Member(1, "host", 65536);
  }

  @Test
  public void isBefore() throws InterruptedException {
    // Given
    Member member1 = new Member();
    member1.setBootupTime(DateTime.now());
    Thread.sleep(10);
    Member member2 = new Member();
    member2.setBootupTime(DateTime.now());

    // When
    boolean before1 = member1.isBefore(member2);
    boolean before2 = member2.isBefore(member1);
    boolean before3 = member1.isBefore(member1);

    // Then
    assertThat(before1, is(true));
    assertThat(before2, is(false));
    assertThat(before3, is(false));
  }

  @Test
  public void selectedMasterAsPriorityWhenConnected() throws InterruptedException {
    // Given
    Member member1 = new Member();
    member1.setMasterPriority((short) 1);
    member1.setBootupTime(DateTime.now());
    Thread.sleep(1);
    Member member2 = new Member();
    member2.setMasterPriority((short) 2);
    member2.setBootupTime(DateTime.now());

    // When
    member1.calculatePriorityPointWhenConnect(member2);
    member2.calculatePriorityPointWhenConnect(member1);

    // Then
    assertThat(member1.isMaster(), is(true));
    assertThat(member2.isStandby(), is(true));
  }

  @Test
  public void selectedMasterAsPriorityWhenConnected2() throws InterruptedException {
    // Given
    Member member1 = new Member();
    member1.setMasterPriority((short) 2);
    member1.setBootupTime(DateTime.now());
    Thread.sleep(1);
    Member member2 = new Member();
    member2.setMasterPriority((short) 1);
    member2.setBootupTime(DateTime.now());

    // When
    member1.calculatePriorityPointWhenConnect(member2);
    member2.calculatePriorityPointWhenConnect(member1);

    // Then
    assertThat(member1.isStandby(), is(true));
    assertThat(member2.isMaster(), is(true));
  }

  @Test
  public void selectedMasterExceptPriority0WhenConnected() throws InterruptedException {
    // Given
    Member member1 = new Member();
    member1.setMasterPriority((short) 0);
    member1.setBootupTime(DateTime.now());
    Thread.sleep(1);
    Member member2 = new Member();
    member2.setMasterPriority((short) 1);
    member2.setBootupTime(DateTime.now());
    Thread.sleep(1);
    Member member3 = new Member();
    member3.setMasterPriority((short) 0);
    member3.setBootupTime(DateTime.now());

    // When
    member1.calculatePriorityPointWhenConnect(member2);
    member1.calculatePriorityPointWhenConnect(member3);
    member2.calculatePriorityPointWhenConnect(member1);
    member2.calculatePriorityPointWhenConnect(member3);
    member3.calculatePriorityPointWhenConnect(member1);
    member3.calculatePriorityPointWhenConnect(member2);

    // Then
    assertThat(member1.isStandby(), is(true));
    assertThat(member2.isMaster(), is(true));
    assertThat(member3.isStandby(), is(true));
  }

  @Test
  public void selectedMasterAsBootupWhenConnected() throws InterruptedException {
    // Given
    Member member1 = new Member();
    member1.setMasterPriority((short) 1);
    member1.setBootupTime(DateTime.now());
    Thread.sleep(1);
    Member member2 = new Member();
    member2.setMasterPriority((short) 1);
    member2.setBootupTime(DateTime.now());

    // When
    member1.calculatePriorityPointWhenConnect(member2);
    member2.calculatePriorityPointWhenConnect(member1);

    // Then
    assertThat(member1.isMaster(), is(true));
    assertThat(member2.isStandby(), is(true));
  }

  @Test
  public void selectedMasterAsBootupExceptPriority0WhenConnected() throws InterruptedException {
    // Given
    Member member1 = new Member();
    member1.setMasterPriority((short) 0);
    member1.setBootupTime(DateTime.now());
    Thread.sleep(1);
    Member member2 = new Member();
    member2.setMasterPriority((short) 1);
    member2.setBootupTime(DateTime.now());

    // When
    member1.calculatePriorityPointWhenConnect(member2);
    member2.calculatePriorityPointWhenConnect(member1);

    // Then
    assertThat(member1.isStandby(), is(true));
    assertThat(member2.isMaster(), is(true));
  }

  @Test
  public void selectedMasterAsPriorityWhenDisconnected() throws InterruptedException {
    // Given
    Member member1 = new Member();
    member1.setMasterPriority((short) 1);
    member1.setBootupTime(DateTime.now());
    Thread.sleep(1);
    Member member2 = new Member();
    member2.setMasterPriority((short) 2);
    member2.setBootupTime(DateTime.now());

    member1.calculatePriorityPointWhenConnect(member2);
    member2.calculatePriorityPointWhenConnect(member1);

    // When
    // member1 is down
    member2.calculatePriorityPointWhenDisconnect(member1);

    // Then
    assertThat(member2.isMaster(), is(true));
  }

  @Test
  public void selectedMasterExceptPriority0WhenDisconnected() throws InterruptedException {
    // Given
    Member member1 = new Member();
    member1.setMasterPriority((short) 1);
    member1.setBootupTime(DateTime.now());
    Thread.sleep(1);
    Member member2 = new Member();
    member2.setMasterPriority((short) 0);
    member2.setBootupTime(DateTime.now());

    member1.calculatePriorityPointWhenConnect(member2);
    member2.calculatePriorityPointWhenConnect(member1);

    // When
    // case: member1 is down
    member2.calculatePriorityPointWhenDisconnect(member1);

    // Then
    assertThat(member2.isStandby(), is(true));
  }

  @Test
  public void selectedMasterExceptPriority0WhenDisconnected2() throws InterruptedException {
    // Given
    Member member1 = new Member();
    member1.setMasterPriority((short) 1);
    member1.setBootupTime(DateTime.now());
    Thread.sleep(1);
    Member member2 = new Member();
    member2.setMasterPriority((short) 0);
    member2.setBootupTime(DateTime.now());
    Thread.sleep(1);
    Member member3 = new Member();
    member3.setMasterPriority((short) 2);
    member3.setBootupTime(DateTime.now());

    member1.calculatePriorityPointWhenConnect(member2);
    member1.calculatePriorityPointWhenConnect(member3);
    member2.calculatePriorityPointWhenConnect(member1);
    member2.calculatePriorityPointWhenConnect(member3);
    member3.calculatePriorityPointWhenConnect(member1);
    member3.calculatePriorityPointWhenConnect(member2);

    // When
    // case: member1 is down
    member2.calculatePriorityPointWhenDisconnect(member1);
    member3.calculatePriorityPointWhenDisconnect(member1);

    // Then
    assertThat(member2.isStandby(), is(true));
    assertThat(member3.isMaster(), is(true));
  }

  @Test
  public void equals() {
    // Given
    Member member1 = new Member(1, "host", 1001);
    member1.setStatus(Status.RUNNING);
    Member member2 = new Member(2, "host", 1001);
    member1.setStatus(Status.SHUTDOWN);
    Member member3 = new Member(3, "host1", 1001);
    Member member4 = new Member(4, "host", 1002);

    // When
    boolean same = member1.equals(member2);
    boolean differentHost = member1.equals(member3);
    boolean differentPort = member1.equals(member4);

    // Then
    assertThat(same, is(true));
    assertThat(differentHost, is(false));
    assertThat(differentPort, is(false));
  }

  @Test
  public void compareToHost() {
    // Given
    Member member1 = new Member(1, "host1", 1001);
    Member member2 = new Member(2, "host2", 1001);

    // When
    int compare1 = member1.compareTo(member2);
    int compare2 = member2.compareTo(member1);

    // Then
    assertThat(compare1, lessThan(0));
    assertThat(compare2, greaterThan(0));
  }

  @Test
  public void compareToPort() {
    // Given
    Member member1 = new Member(1, "host", 1001);
    Member member2 = new Member(2, "host", 1002);

    // When
    int compare1 = member1.compareTo(member2);
    int compare2 = member2.compareTo(member1);

    // Then
    assertThat(compare1, lessThan(0));
    assertThat(compare2, greaterThan(0));
  }
}
