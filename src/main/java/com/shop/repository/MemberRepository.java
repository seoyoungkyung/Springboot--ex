package com.shop.repository;

import com.shop.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

   //회원 가입 시 중복된 회원이 있는지 검사하기 위해서 email로 검사할 수 있는 메소드
   Member findByEmail(String email);
}
