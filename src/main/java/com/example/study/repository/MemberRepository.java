package com.example.study.repository;

import com.example.study.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member,Long>, MemberRepositoryCustom {
    // 이렇게 상속하면 이 MemberRepositroy 는 JPA Repository의 메서드도 사용할 수 있고, 
    // MemberRepositoryCustom 의 메서드도 사용할 수 있음
}
