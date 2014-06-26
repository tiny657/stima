package com.it.model;

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
    clusters.add("cluster1");
    clusters.add("cluster2");
    clusters.add("cluster3");

    // When
    Set<String> clusterNames = clusters.getClusterNames();

    // Then
    assertThat(clusterNames.size(), is(3));
  }

  @Test
  public void removeCluster() {
    // Given
    Clusters clusters = new Clusters();
    clusters.add("cluster1");
    clusters.add("cluster2");
    clusters.add("cluster3");
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
    clusters.add("cluster1", new Member(1, "host", 1001));
    clusters.add("cluster1", new Member(2, "host", 1002));
    clusters.add("cluster2");
    clusters.add("cluster2", new Member(3, "host", 1003));

    // When
    int size = clusters.getClusterNames().size();

    // Then
    assertThat(size, is(2));
  }

  @Test
  public void notEqualsToClusterCount() {
    // Given
    Clusters clusters1 = new Clusters();
    clusters1.add("cluster1", new Member(1, "host", 1001));
    clusters1.add("cluster1", new Member(2, "host", 1002));
    clusters1.add("cluster2", new Member(3, "host", 1003));
    Clusters clusters2 = new Clusters();
    clusters2.add("cluster1", new Member(4, "host", 1001));
    clusters2.add("cluster1", new Member(5, "host", 1002));

    // When
    boolean equals = clusters1.equals(clusters2);

    // Then
    assertThat(equals, is(false));
  }

  @Test
  public void notEqualsToMemberCount() {
    // Given
    Clusters clusters1 = new Clusters();
    clusters1.add("cluster1", new Member(1, "host", 1001));
    clusters1.add("cluster1", new Member(2, "host", 1002));
    clusters1.add("cluster1", new Member(3, "host", 1003));
    Clusters clusters2 = new Clusters();
    clusters2.add("cluster1", new Member(4, "host", 1001));
    clusters2.add("cluster1", new Member(5, "host", 1002));

    // When
    boolean equals = clusters1.equals(clusters2);

    // Then
    assertThat(equals, is(false));
  }

  @Test
  public void notEqualsToPort() {
    // Given
    Clusters clusters1 = new Clusters();
    clusters1.add("cluster1", new Member(1, "host", 1001));
    clusters1.add("cluster1", new Member(2, "host", 1002));
    clusters1.add("cluster1", new Member(3, "host", 1003));
    Clusters clusters2 = new Clusters();
    clusters2.add("cluster1", new Member(4, "host", 1001));
    clusters2.add("cluster1", new Member(5, "host", 1002));
    clusters2.add("cluster1", new Member(6, "host", 1004));

    // When
    boolean equals = clusters1.equals(clusters2);

    // Then
    assertThat(equals, is(false));
  }

  @Test
  public void notEqualsToHost() {
    // Given
    Clusters clusters1 = new Clusters();
    clusters1.add("cluster1", new Member(1, "host", 1001));
    clusters1.add("cluster1", new Member(2, "host", 1002));
    clusters1.add("cluster1", new Member(3, "host", 1003));
    Clusters clusters2 = new Clusters();
    clusters2.add("cluster1", new Member(4, "host", 1001));
    clusters2.add("cluster1", new Member(5, "host", 1002));
    clusters2.add("cluster1", new Member(6, "host2", 1003));

    // When
    boolean equals = clusters1.equals(clusters2);

    // Then
    assertThat(equals, is(false));
  }

  @Test
  public void equals() {
    // Given
    Clusters clusters1 = new Clusters();
    clusters1.add("cluster1", new Member(1, "host", 1001));
    clusters1.add("cluster1", new Member(2, "host", 1002));
    clusters1.add("cluster2", new Member(3, "host", 1003));
    Clusters clusters2 = new Clusters();
    clusters2.add("cluster1", new Member(4, "host", 1001));
    clusters2.add("cluster1", new Member(5, "host", 1002));
    clusters2.add("cluster2", new Member(6, "host", 1003));

    // When
    boolean equals = clusters1.equals(clusters2);

    // Then
    assertThat(equals, is(true));
  }

  @Test
  public void diffCluster() {
    // Given
    Clusters clusters1 = new Clusters();
    clusters1.add("cluster1", new Member(1, "host", 1001));
    clusters1.add("cluster1", new Member(2, "host", 1002));
    clusters1.add("cluster2", new Member(3, "host", 1003));

    Clusters clusters2 = new Clusters();
    clusters2.add("cluster1", new Member(4, "host", 1001));
    clusters2.add("cluster1", new Member(5, "host", 1002));

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
    clusters1.add("cluster1", new Member(1, "host", 1001));
    clusters1.add("cluster1", new Member(2, "host", 1002));

    Clusters clusters2 = new Clusters();
    clusters2.add("cluster1", new Member(3, "host", 1001));
    clusters2.add("cluster1", new Member(4, "host", 1002));
    clusters2.add("cluster1", new Member(5, "host", 1003));
    clusters2.add("cluster2", new Member(6, "host", 1004));

    // When
    Map<String, MemberList> diff = clusters1.diff(clusters2);

    // Then
    assertThat(diff.get("cluster1").getMembers().size(), is(0));
  }
}
