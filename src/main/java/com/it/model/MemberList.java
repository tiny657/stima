package com.it.model;

import java.io.Serializable;
import java.util.List;

import com.google.common.collect.Lists;

public class MemberList implements Serializable {
    private static final long serialVersionUID = -4214424024062575985L;

    private List<Member> members = Lists.newArrayList();
    transient private int index = 0;

    public MemberList() {
    }

    public boolean hasMembers() {
        return members.size() > 0;
    }

    public int size() {
        return members.size();
    }

    public Member findMe() {
        for (Member member : members) {
            if (member.isMe()) {
                return member;
            }
        }
        return null;
    }

    public List<Member> getMembers() {
        return members;
    }

    public List<Member> getRunningMembers() {
        List<Member> runningMembers = Lists.newArrayList();
        for (Member member : members) {
            if (member.isRunning()) {
                runningMembers.add(member);
            }
        }
        return runningMembers;
    }

    public Member nextRunningMember() {
        Member result = null;
        for (int i = 0; i < members.size(); i++) {
            Member member = members.get(next());
            if (member.isRunning()) {
                result = member;
                break;
            }
        }
        return result;
    }

    public int next() {
        index = (index + 1) % members.size();
        return index;
    }

    public boolean setStatus(String host, int port, Status status) {
        Member member = findMember(host, port);
        if (member == null) {
            return false;
        }
        member.setStatus(status);

        return true;
    }

    public boolean contains(Member member) {
        if (findMember(member.getHost(), member.getPort()) == null) {
            return false;
        }

        return true;
    }

    public Member findMember(String host, int port) {
        for (Member member : members) {
            if (member.equals(host, port)) {
                return member;
            }
        }

        return null;
    }

    public MemberList diff(MemberList memberList) {
        MemberList result = new MemberList();
        for (Member member : getMembers()) {
            if (memberList == null || !memberList.contains(member)) {
                result.addMember(member);
            }
        }
        return result;
    }

    public void addMember(Member member) {
        members.add(member);
    }

    public void removeMember(Member member) {
        members.remove(member);
    }

    @Override
    public String toString() {
        return members.toString();
    }
}