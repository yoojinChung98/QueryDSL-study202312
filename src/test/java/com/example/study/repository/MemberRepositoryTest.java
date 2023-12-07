package com.example.study.repository;

import com.example.study.entity.Member;
import com.example.study.entity.QMember;
import com.example.study.entity.Team;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.List;

import static com.example.study.entity.QMember.member;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    TeamRepository teamRepository;

    // jpa 와 queryDSL 을 비교해보려고 합니당

    @Autowired
    EntityManager em; // jpa 관리 핵심 객체

    JPAQueryFactory factory; // QueryDSL로 쿼리문을 작성하기 위한 핵심 객체

    @BeforeEach
    void settingObject() {
        factory = new JPAQueryFactory(em);
    }

    @Test
    // 더미더미 데이터
    void testInsertData() {

        Team teamA = Team.builder()
                .name("teamA")
                .build();
        Team teamB = Team.builder()
                .name("teamB")
                .build();

        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = Member.builder()
                .userName("member1")
                .age(10)
                .team(teamA)
                .build();
        Member member2 = Member.builder()
                .userName("member2")
                .age(20)
                .team(teamA)
                .build();
        Member member3 = Member.builder()
                .userName("member3")
                .age(30)
                .team(teamB)
                .build();
        Member member4 = Member.builder()
                .userName("member4")
                .age(40)
                .team(teamB)
                .build();

        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);
        memberRepository.save(member4);
    }

    @Test
    @DisplayName("testJPA")
    void testJPA() {
        List<Member> members = memberRepository.findAll();

        members.forEach(System.out::println);
    }

    @Test
    @DisplayName("testJPQL")
    void testJPQL() {
        //given
        String jpqlQuery = "SELECT m FROM Member m WHERE m.userName = :userName";

        //when
        // EntityManager를 활용하여 직접 jpql을 작성하고, 파라미터를 설정할 수 있음.
        Member foundMember = em.createQuery(jpqlQuery, Member.class)
                .setParameter("userName", "member2") // 이부분이 WHERE인가? user_name 이 member2인 애.
                .getSingleResult();

        //then
        assertEquals("teamA", foundMember.getTeam().getName());

        System.out.println("\n\n\n");
        System.out.println("foundMember = " + foundMember);
        System.out.println("foundMember.getTeam() = " + foundMember.getTeam());
        System.out.println("\n\n\n");
    }

    @Test
    @DisplayName("TestQueryDSL")
    void testQueryDSL() {
        //given
        QMember m = member; // 빌드 과정에서 객체가 미리 생성되어 있기 때문에 new 로 생성하지 않고 객체사용 가능

        //when
        Member findMember = factory.select(m) // (SELECT m )QMember의 모든 값을 조회하겠다 = m
                .from(m) // FROM Member m
                .where(m.userName.eq("member1")) // WHERE m.userName = :userName
                .fetchOne();// 그냥 fetch 면 리스트가 옴. 우리는 어차피 하나값인걸 아니까 fetchone 해서 Member객체로 받음.

        //then
        assertEquals(findMember.getUserName(), "member1");
    }
    
    @Test
    @DisplayName("search")
    void search() {
        //given
        String searchName = "member2";
        int searchAge = 20;
        // QMember m = QMember.member; 인데 import static 했음.(추천)

        //when
        Member foundMember = factory.selectFrom(member) // select 와 from 에 동일한 객체를 주겠다면,,,!!
                .where(member.userName.eq(searchName)
//                        .and(member.age.eq(searchAge))
                        , member.age.eq(searchAge) // 이렇게 코마로 연결도 가능~!(or는 안됨)
                ) // ne : 같지않다? / eq: 같다?
                .fetchOne();

        //then
        assertNotNull(foundMember);
        assertEquals("teamA", foundMember.getTeam().getName());


    }

    /*
         JPAQueryFactory를 이용해서 쿼리문을 조립한 후 반환 인자를 결정합니다.
         - fetchOne(): 단일 건 조회. 여러 건(조회 행수가 2개 이상) 조회시 예외 발생.
         - fetchFirst(): 단일 건 조회. 여러 개가 조회돼도 첫 번째 값만 반환
         - fetch(): List 형태로 반환
         * JPQL이 제공하는 모든 검색 조건을 queryDsl에서도 사용 가능
         *
         * JPQL // 쿼리문
         * member.userName.eq("member1") // userName = 'member1'
         * member.userName.nq("member1") // userName != 'member1'
         * member.userName.eq("member1").not() // userName != 'member1'
         * member.userName.isNotNull() // 이름이 is not null
         * member.age.in(10, 20) // age in (10,20)
         * member.age.notIn(10, 20) // age not in (10,20)
         * member.age.between(10, 30) // age between 10, 30
         * member.age.goe(30) // age >= 30 (greater or equals)
         * member.age.gt(30) // age > 30  (greater than)
         * member.age.loe(30) // age <= 30 (less or equals)
         * member.age.lt(30) // age < 30 (less than)
         * member.userName.like("_김%") // userName LIKE '_김%'
         * member.userName.contains("김") // userName LIKE '%김%'
         * member.userName.startsWith("김") // userName LIKE '김%'
         * member.userName.endsWith("김") // userName LIKE '%김'
         */

    @Test
    @DisplayName("결과 반환하기")
    void testFetchResult() {
        // fetch
        List<Member> fetch1 = factory.selectFrom(member)
                .fetch();
        System.out.println("\n\n ========== fetch ==========");
        fetch1.forEach(System.out::println);

        // fetchOne : 결과가 unique(1개)하지 않으면 예외 발생
        Member fetch2 = factory.selectFrom(member)
                .where(member.id.eq(3L))
                .fetchOne();
        System.out.println("\n\n ========== fetch ==========");
        System.out.println("fetch2 = " + fetch2);

        // fetchFirst : 무조건 하나만 가져옴 (쿼리문에 limit 1 이 걸림)
        Member fetch3 = factory.selectFrom(member)
                .fetchFirst();
        System.out.println("\n\n ========== fetch ==========");
        System.out.println("fetch3 = " + fetch3);

    }

    @Test
    @DisplayName("QueryDSL custom 설정 확인")
    void queryDslCustom() {
        //given
        String name = "member4";

        //when
        // 이 memberRepository는 MemberRepository 인터페이스 타입인데,
        // 걔가 MemberRepositoryCustom 클래스를 상속받기 때문에 커스텀에 설정한 메서드도 사용 가능.
        List<Member> result = memberRepository.findByName(name);

        //then
        assertEquals(1, result.size());
        assertEquals("teamB", result.get(0).getTeam().getName());
    }



}