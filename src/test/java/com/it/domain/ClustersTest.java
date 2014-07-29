package com.it.domain;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.Map;
import java.util.Set;

import org.junit.Test;

public class ClustersTest {
  @Test
  public void addCluster() {
    // Given
    Clusters clusters = new Clusters();
    clusters.createCluster("cluster1");
    clusters.createCluster("cluster2");
    clusters.createCluster("cluster3");

    // When
    Set<String> clusterNames = clusters.getClusterNames();

    // Then
    assertThat(clusterNames.size(), is(3));
  }

  @Test
  public void removeCluster() {
    // Given
    Clusters clusters = new Clusters();
    clusters.createCluster("cluster1");
    clusters.createCluster("cluster2");
    clusters.createCluster("cluster3");
    clusters.remove("cluster1");
    clusters.remove("cluster4");

    // When
    int size = clusters.getClusterNames().size();

    // Then
    assertThat(size, is(2));
  }

  @Test
  public void addMember() {
    // Given
    Clusters clusters = new Clusters();
    clusters.addMember("cluster1", new Member(1, "host", 1001, 2001));
    clusters.addMember("cluster1", new Member(2, "host", 1002, 2002));
    clusters.createCluster("cluster2");
    clusters.addMember("cluster2", new Member(3, "host", 1003, 2003));

    // When
    int size = clusters.getClusterNames().size();

    // Then
    assertThat(size, is(2));
  }

  @Test
  public void notEqualsToClusterCount() {
    // Given
    Clusters clusters1 = new Clusters();
    clusters1.addMember("cluster1", new Member(1, "host", 1001, 2001));
    clusters1.addMember("cluster1", new Member(2, "host", 1002, 2002));
    clusters1.addMember("cluster2", new Member(3, "host", 1003, 2003));
    Clusters clusters2 = new Clusters();
    clusters2.addMember("cluster1", new Member(4, "host", 1001, 2001));
    clusters2.addMember("cluster1", new Member(5, "host", 1002, 2002));

    // When
    boolean equals = clusters1.equals(clusters2);

    // Then
    assertThat(equals, is(false));
  }

  @Test
  public void notEqualsToMemberCount() {
    // Given
    Clusters clusters1 = new Clusters();
    clusters1.addMember("cluster1", new Member(1, "host", 1001, 2001));
    clusters1.addMember("cluster1", new Member(2, "host", 1002, 2002));
    clusters1.addMember("cluster1", new Member(3, "host", 1003, 2003));
    Clusters clusters2 = new Clusters();
    clusters2.addMember("cluster1", new Member(4, "host", 1001, 2001));
    clusters2.addMember("cluster1", new Member(5, "host", 1002, 2002));

    // When
    boolean equals = clusters1.equals(clusters2);

    // Then
    assertThat(equals, is(false));
  }

  @Test
  public void notEqualsToPort() {
    // Given
    Clusters clusters1 = new Clusters();
    clusters1.addMember("cluster1", new Member(1, "host", 1001, 2001));
    clusters1.addMember("cluster1", new Member(2, "host", 1002, 2002));
    clusters1.addMember("cluster1", new Member(3, "host", 1003, 2003));
    Clusters clusters2 = new Clusters();
    clusters2.addMember("cluster1", new Member(4, "host", 1001, 2001));
    clusters2.addMember("cluster1", new Member(5, "host", 1002, 2002));
    clusters2.addMember("cluster1", new Member(6, "host", 1004, 2004));

    // When
    boolean equals = clusters1.equals(clusters2);

    // Then
    assertThat(equals, is(false));
  }

  @Test
  public void notEqualsToHost() {
    // Given
    Clusters clusters1 = new Clusters();
    clusters1.addMember("cluster1", new Member(1, "host", 1001, 2001));
    clusters1.addMember("cluster1", new Member(2, "host", 1002, 2002));
    clusters1.addMember("cluster1", new Member(3, "host", 1003, 2003));
    Clusters clusters2 = new Clusters();
    clusters2.addMember("cluster1", new Member(4, "host", 1001, 2001));
    clusters2.addMember("cluster1", new Member(5, "host", 1002, 2002));
    clusters2.addMember("cluster1", new Member(6, "host2", 1003, 2003));

    // When
    boolean equals = clusters1.equals(clusters2);

    // Then
    assertThat(equals, is(false));
  }

  @Test
  public void equals() {
    // Given
    Clusters clusters1 = new Clusters();
    clusters1.addMember("cluster1", new Member(1, "host", 1001, 2001));
    clusters1.addMember("cluster1", new Member(2, "host", 1002, 2002));
    clusters1.addMember("cluster2", new Member(3, "host", 1003, 2003));
    Clusters clusters2 = new Clusters();
    clusters2.addMember("cluster1", new Member(4, "host", 1001, 2001));
    clusters2.addMember("cluster1", new Member(5, "host", 1002, 2002));
    clusters2.addMember("cluster2", new Member(6, "host", 1003, 2003));

    // When
    boolean equals = clusters1.equals(clusters2);

    // Then
    assertThat(equals, is(true));
  }

  @Test
  public void diffCluster() {
    // Given
    Clusters clusters1 = new Clusters();
    clusters1.addMember("cluster1", new Member(1, "host", 1001, 2001));
    clusters1.addMember("cluster1", new Member(2, "host", 1002, 2002));
    clusters1.addMember("cluster2", new Member(3, "host", 1003, 2003));

    Clusters clusters2 = new Clusters();
    clusters2.addMember("cluster1", new Member(4, "host", 1001, 2001));
    clusters2.addMember("cluster1", new Member(5, "host", 1002, 2002));

    // When
    Map<String, MemberList> diff = clusters1.diff(clusters2);

    // Then
    assertThat(diff.get("cluster1").getMembers().size(), is(0));
    assertThat(diff.get("cluster2").getMembers().size(), is(1));
  }

  @Test
  public void diffMember() {
    // Given
    Clusters clusters1 = new Clusters();
    clusters1.addMember("cluster1", new Member(1, "host", 1001, 2001));
    clusters1.addMember("cluster1", new Member(2, "host", 1002, 2002));

    Clusters clusters2 = new Clusters();
    clusters2.addMember("cluster1", new Member(3, "host", 1001, 2001));
    clusters2.addMember("cluster1", new Member(4, "host", 1002, 2002));
    clusters2.addMember("cluster1", new Member(5, "host", 1003, 2003));
    clusters2.addMember("cluster2", new Member(6, "host", 1004, 2004));

    // When
    Map<String, MemberList> diff = clusters1.diff(clusters2);

    // Then
    assertThat(diff.get("cluster1").getMembers().size(), is(0));
  }
}
