package com.it.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.Maps;

public class Categories implements Serializable {
    private static final long serialVersionUID = 3688785685416855060L;

    private Date bootupTime = new Date();
    private Map<String, MemberList> memberListMap = Maps.newHashMap();
    transient private static final MemberList EMPTY_MEMBERLIST = new MemberList();

    public Date getBootupTime() {
        return bootupTime;
    }

    public void add(String categoryName) {
        if (!memberListMap.containsKey(categoryName)) {
            memberListMap.put(categoryName, new MemberList());
        }
    }

    public void remove(String categoryName) {
        memberListMap.remove(categoryName);
    }

    public void add(String categoryName, Member member) {
        MemberList memberList = getMemberListIn(categoryName);
        if (memberList == EMPTY_MEMBERLIST) {
            add(categoryName);
            memberList = getMemberListIn(categoryName);
        }
        memberList.addMember(member);
    }

    public void remove(String categoryName, Member member) {
        MemberList memberList = getMemberListIn(categoryName);
        if (memberList.hasMembers()) {
            memberList.removeMember(member);
        }
    }

    public Set<String> getCategoryNames() {
        return memberListMap.keySet();
    }

    public MemberList getMemberListIn(String category) {
        MemberList memberList = memberListMap.get(category);
        if (memberList == null) {
            return EMPTY_MEMBERLIST;
        }
        return memberList;
    }

    public Member nextRunningMember(String category) {
        MemberList memberList = getMemberListIn(category);
        return memberList.nextRunningMember();
    }

    public boolean setStatus(Member member, boolean isRunning) {
        for (Entry<String, MemberList> entry : memberListMap.entrySet()) {
            MemberList memberList = entry.getValue();
            if (memberList.setStatus(member.getHost(), member.getPort(),
                    isRunning)) {
                return true;
            }
        }
        return false;
    }

    public boolean equals(Categories categories) {
        if (memberListMap.size() != categories.getCategoryNames().size()) {
            return false;
        }

        for (String category : getCategoryNames()) {
            if (getMemberListIn(category).getMembers().size() != categories
                    .getMemberListIn(category).getMembers().size()) {
                return false;
            }

            Iterator<Member> iterator = getMemberListIn(category).getMembers()
                    .iterator();
            Iterator<Member> iterator2 = categories.getMemberListIn(category)
                    .getMembers().iterator();
            while (iterator.hasNext()) {
                Member member = iterator.next();
                Member member2 = iterator2.next();
                if (!member.equals(member2)) {
                    return false;
                }
            }
        }
        return true;
    }

    public Map<String, MemberList> diff(Categories categories) {
        Map<String, MemberList> result = Maps.newHashMap();
        for (String category : getCategoryNames()) {
            MemberList memberList = getMemberListIn(category);
            if (memberList != EMPTY_MEMBERLIST) {
                result.put(category,
                        memberList.diff(categories.getMemberListIn(category)));
            }
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuffer result = new StringBuffer("\n* member list\n");
        for (String category : memberListMap.keySet()) {
            result.append("category: ").append(category).append(", members: ")
                    .append(memberListMap.get(category).toString())
                    .append("\n");
        }

        return result.toString();
    }
}