package com.it.model;

import java.util.List;
import java.util.Map;

public class AllMember {
    public static AllMember instance = new AllMember();
    private Clusters clusters = new Clusters();
    private MemberInfos memberInfos = new MemberInfos();

    public static AllMember getInstance() {
        return instance;
    }

    public void addCluster(String[] clusterNames) {
        for (String clusterName : clusterNames) {
            addCluster(clusterName);
        }
    }

    public void addCluster(String clusterName) {
        clusters.add(clusterName);
    }

    public void addMember(String clusterName, Member member) {
        clusters.add(clusterName, member);
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

    public Member getMember(Member member) {
        return getMember(member.getHost(), member.getPort());
    }

    public Member getMember(String host, int port) {
        for (String clusterName : clusters.getClusterNames()) {
            Member member = clusters.getMemberListIn(clusterName).findMember(
                    host, port);
            if (member != null) {
                return member;
            }
        }

        return null;
    }

    public void setStatus(Member member, Status status) {
        clusters.setStatus(member, status);
    }

    public MemberInfos getMemberInfos() {
        return memberInfos;
    }

    @Override
    public String toString() {
        return clusters.toString();
    }
}