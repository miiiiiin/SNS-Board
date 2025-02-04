package com.example.board.repository;

import com.example.board.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// primary key인 userid 기반으로 findbyid 메서드 제공
@Repository
public interface UserEntityRepository extends JpaRepository<UserEntity, Long> {

    // userservice와 같이 loaduser...의 기능은 제공하지 않음
    // username 검색해서 찾는 메서드 따로 만들어줘야 함
    Optional<UserEntity> findByUsername(String username);

    // query 내용이 부분적으로 포함된 경우
    List<UserEntity> findByUsernameContaining(String username);
}

