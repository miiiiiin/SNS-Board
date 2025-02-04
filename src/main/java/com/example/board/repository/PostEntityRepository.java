package com.example.board.repository;

import com.example.board.model.entity.PostEntity;
import com.example.board.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostEntityRepository extends JpaRepository<PostEntity, Long> {

    /**
     *  JPA 제공 기본 메서드 아닐 경우, 해당 엔티티 내에 정의되어 있는 필드가 있어야 내부적으로 찾아올 수 있음
     *
     * @ManyToOne
     *     @JoinColumn(name = "userid")
     *     private UserEntity user;
     */
    List<PostEntity> findByUser(UserEntity user);
}
