package com.server.crews.environ.service;

import com.server.crews.auth.domain.Member;
import com.server.crews.auth.domain.Role;
import com.server.crews.recruitment.domain.Recruitment;

public class TestMember {
    private final ServiceTestEnviron environ;
    private Member member;

    public TestMember(ServiceTestEnviron environ) {
        this.environ = environ;
    }

    public TestMember create(String email, String password, Role role, Recruitment recruitment) {
        Member member = new Member(email, password, role, recruitment);
        this.member = environ.memberRepository().save(member);
        return this;
    }

    public Member member() {
        return member;
    }
}
