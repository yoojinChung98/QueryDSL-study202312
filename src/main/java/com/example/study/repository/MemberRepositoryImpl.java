package com.example.study.repository;

import com.example.study.entity.Member;
import com.example.study.entity.QMember;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.example.study.entity.QMember.member;

// 반드시 반드시 유의할 것!!
// QueryDSL용 인터페이스의 구현체는 반드시 클래스 이름의 끝이 Impl 로 끝나야 자동으로 인식되어
// 원본 인터페이스 타입(MemberRepositroy)의 객체로도 사용이 가능. (해당 레파지토리를 이 클래스의 자식클래스로 등록할거거든)
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    // 테스트에서 쿼리문을 직접 조합할 필요가 없어짐.
    @Override
    public List<Member> findByName(String name) {
        return queryFactory
                .selectFrom(member)
                .where(member.userName.eq(name))
                .fetch();
    }
}
