package com.it.model;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Test;

public class MemberTest {
  @Test
  public void validatePort() {
    // When
    Member memberWithValidPort = new Member(1, "host", 1);
    Member memberWithInvalidPort1 = new Member(2, "host", 0);
    Member memberWithInvalidPort2 = new Member(3, "host", 70000);

    // Then
    assertThat(memberWithValidPort.getPort(), is(not(0)));
    assertThat(memberWithInvalidPort1.getPort(), is(0));
    assertThat(memberWithInvalidPort2.getPort(), is(0));
  }

  @Test
  public void isMe() {
    // When
    Member me = new Member(1, "host", 1, "host", 1);
    Member notMe1 = new Member(2, "host", 1, "host", 2);
    Member notMe2 = new Member(3, "host1", 2, "host2", 2);

    // Then
    assertThat(me.isMe(), is(true));
    assertThat(notMe1.isMe(), is(false));
    assertThat(notMe2.isMe(), is(false));
  }

  @Test
  public void isEarlier() throws InterruptedException {
    // Given
    Member member1 = new Member();
    member1.setBootupTime(new Date());
    Thread.sleep(10);
    Member member2 = new Member();
    member2.setBootupTime(new Date());

    // When
    boolean earlier1 = member1.isEarlier(member2);
    boolean earlier2 = member2.isEarlier(member1);
    boolean earlier3 = member1.isEarlier(member1);

    // Then
    assertThat(earlier1, is(true));
    assertThat(earlier2, is(false));
    assertThat(earlier3, is(false));
  }

  @Test
  public void selectedMasterAsPriorityWhenConnected()
      throws InterruptedException {
    // Given
    Member member1 = new Member();
    member1.setMasterPriority(1);
    member1.setBootupTime(new Date()); // ignore
    Thread.sleep(1);
    Member member2 = new Member();
    member2.setMasterPriority(2);
    member2.setBootupTime(new Date()); // ignore

    // When
    member1.calculatePriorityPointWhenConnect(member2);
    member2.calculatePriorityPointWhenConnect(member1);

    // Then
    assertThat(member1.isMaster(), is(true));
    assertThat(member2.isStandby(), is(true));
  }

  @Test
  public void selectedMasterAsPriorityWhenConnected2()
      throws InterruptedException {
    // Given
    Member member1 = new Member();
    member1.setMasterPriority(2);
    member1.setBootupTime(new Date()); // ignore
    Thread.sleep(1);
    Member member2 = new Member();
    member2.setMasterPriority(1);
    member2.setBootupTime(new Date()); // ignore

    // When
    member1.calculatePriorityPointWhenConnect(member2);
    member2.calculatePriorityPointWhenConnect(member1);

    // Then
    assertThat(member1.isStandby(), is(true));
    assertThat(member2.isMaster(), is(true));
  }

  @Test
  public void selectedMasterExceptPriority0WhenConnected()
      throws InterruptedException {
    // Given
    Member member1 = new Member();
    member1.setMasterPriority(0);
    member1.setBootupTime(new Date());
    Thread.sleep(1);
    Member member2 = new Member();
    member2.setMasterPriority(1);
    member2.setBootupTime(new Date());
    Thread.sleep(1);
    Member member3 = new Member();
    member3.setMasterPriority(0);
    member3.setBootupTime(new Date());

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
  public void selectedMasterAsBootupWhenConnected()
      throws InterruptedException {
    // Given
    Member member1 = new Member();
    member1.setMasterPriority(1);
    member1.setBootupTime(new Date());
    Thread.sleep(1);
    Member member2 = new Member();
    member2.setMasterPriority(1);
    member2.setBootupTime(new Date());

    // When
    member1.calculatePriorityPointWhenConnect(member2);
    member2.calculatePriorityPointWhenConnect(member1);

    // Then
    assertThat(member1.isMaster(), is(true));
    assertThat(member2.isStandby(), is(true));
  }

  @Test
  public void selectedMasterAsBootupExceptPriority0WhenConnected()
      throws InterruptedException {
    // Given
    Member member1 = new Member();
    member1.setMasterPriority(0);
    member1.setBootupTime(new Date());
    Thread.sleep(1);
    Member member2 = new Member();
    member2.setMasterPriority(1);
    member2.setBootupTime(new Date());

    // When
    member1.calculatePriorityPointWhenConnect(member2);
    member2.calculatePriorityPointWhenConnect(member1);

    // Then
    assertThat(member1.isStandby(), is(true));
    assertThat(member2.isMaster(), is(true));
  }

  @Test
  public void selectedMasterAsPriorityWhenDisconnected()
      throws InterruptedException {
    // Given
    Member member1 = new Member();
    member1.setMasterPriority(1);
    member1.setBootupTime(new Date()); // ignore
    Thread.sleep(1);
    Member member2 = new Member();
    member2.setMasterPriority(2);
    member2.setBootupTime(new Date()); // ignore

    member1.calculatePriorityPointWhenConnect(member2);
    member2.calculatePriorityPointWhenConnect(member1);

    // When
    // member1 is down
    member2.calculatePriorityPointWhenDisconnect(member1);

    // Then
    assertThat(member2.isMaster(), is(true));
  }

  @Test
  public void selectedMasterExceptPriority0WhenDisconnected()
      throws InterruptedException {
    // Given
    Member member1 = new Member();
    member1.setMasterPriority(1);
    member1.setBootupTime(new Date());
    Thread.sleep(1);
    Member member2 = new Member();
    member2.setMasterPriority(0);
    member2.setBootupTime(new Date());

    member1.calculatePriorityPointWhenConnect(member2);
    member2.calculatePriorityPointWhenConnect(member1);

    // When
    // case: member1 is down
    member2.calculatePriorityPointWhenDisconnect(member1);

    // Then
    assertThat(member2.isStandby(), is(true));
  }

  @Test
  public void selectedMasterExceptPriority0WhenDisconnected2()
      throws InterruptedException {
    // Given
    Member member1 = new Member();
    member1.setMasterPriority(1);
    member1.setBootupTime(new Date());
    Thread.sleep(1);
    Member member2 = new Member();
    member2.setMasterPriority(0);
    member2.setBootupTime(new Date());
    Thread.sleep(1);
    Member member3 = new Member();
    member3.setMasterPriority(2);
    member3.setBootupTime(new Date());

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
