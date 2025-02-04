package com.example.board.repository;

import com.example.board.model.entity.LikeEntity;
import com.example.board.model.entity.PostEntity;
import com.example.board.model.entity.ReplyEntity;
import com.example.board.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeEntityRepository extends JpaRepository<LikeEntity, Long> {

    /**
     *  JPA 제공 기본 메서드 아닐 경우, 해당 엔티티 내에 정의되어 있는 필드가 있어야 내부적으로 찾아올 수 있음
     *
     * @ManyToOne
     *     @JoinColumn(name = "userid")
     *     private UserEntity user;
     */
    List<LikeEntity> findByUser(UserEntity user);
    List<LikeEntity> findByPost(PostEntity post);

    /**
     * 여러 개 필드 탐색 시 And 붙여서 해야 JPA에서 인식 가능
     * unique로 설정했기 때문에 한 개 있거나 없거나 두 가지 경우만 있음. 그래서 optional 처리
     * @param user
     * @param post
     * @return
     */
    Optional<LikeEntity> findByUserAndPost(UserEntity user, PostEntity post);
}
