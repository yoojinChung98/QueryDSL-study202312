package com.example.study.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

// QueryDSL 문법을 사용하기 위한 필수 객체인 JPAQueryFactory의 빈 등록을 위한 클래스
// 나중에 여러 개의 repository에서 QueryDSL을 사용하기 위한 빈 등록.
@Configuration
public class QuerydslConfig {

    @PersistenceContext // JPA 라이브러리를 사용한다면 객체 주입 가능. (빈주입으로 해도 노상관)
    private EntityManager entityManager;

    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(entityManager);
    }

}
