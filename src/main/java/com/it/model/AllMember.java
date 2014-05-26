package com.it.model;

import java.util.List;

public class AllMember {
    public static AllMember instance = new AllMember();
    private Categories categories = new Categories();
    private MemberInfos memberInfos = new MemberInfos();

    public static AllMember getInstance() {
        return instance;
    }

    public void addCategory(String[] categoryNames) {
        for (String categoryName : categoryNames) {
            addCategory(categoryName);
        }
    }

    public void addCategory(String categoryName) {
        categories.add(categoryName);
    }

    public void addMember(String categoryName, Member member) {
        categories.add(categoryName, member);
    }

    public void removeCategory(String categoryName) {
        categories.remove(categoryName);
    }

    public void removeMember(String categoryName, Member member) {
        categories.remove(categoryName, member);
    }

    public Categories getCategories() {
        return categories;
    }

    public MemberList getMemberListIn(String category) {
        return categories.getMemberListIn(category);
    }

    public List<Member> getMembers(String category) {
        return categories.getMemberListIn(category).getMembers();
    }

    public Member getMember(Member member) {
        return getMember(member.getHost(), member.getPort());
    }

    public Member getMember(String host, int port) {
        for (String categoryName : categories.getCategoryNames()) {
            Member member = categories.getMemberListIn(categoryName)
                    .findMember(host, port);
            if (member != null) {
                return member;
            }
        }

        return null;
    }

    public void setStatus(Member member, boolean isRunning) {
        categories.setStatus(member, isRunning);
    }

    public MemberInfos getMemberInfos() {
        return memberInfos;
    }

    @Override
    public String toString() {
        return categories.toString();
    }
}