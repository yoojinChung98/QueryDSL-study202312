package com.example.study.repository;

import com.example.study.entity.Member;

import java.util.List;

// QueryDSL 용 레파지토리를 하나 만드는 것. (JPA용 레파지토리 아님.)
public interface MemberRepositoryCustom {

    List<Member> findByName(String name);
}
