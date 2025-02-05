package com.example.board.service;

import com.example.board.exception.follow.FollowAlreadyExistException;
import com.example.board.exception.follow.FollowNotFoundException;
import com.example.board.exception.follow.InvalidFollowException;
import com.example.board.exception.user.UserAlreadyExistException;
import com.example.board.exception.user.UserNotAllowedException;
import com.example.board.exception.user.UserNotFoundException;
import com.example.board.model.entity.FollowEntity;
import com.example.board.model.entity.UserEntity;
import com.example.board.model.user.User;
import com.example.board.model.user.UserAuthenticationResponse;
import com.example.board.model.user.UserPatchRequestBody;
import com.example.board.repository.FollowEntityRepository;
import com.example.board.repository.UserEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserEntityRepository userEntityRepository;
    @Autowired
    private FollowEntityRepository followEntityRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;

    // username 기반 user 찾아 전달 (user 검색하려면 repository 필요)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // optional 리턴되면 해당 유저가 존재하지 않는 다는 예외 던져줌
        return userEntityRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
    }

    public User signUp(String username, String password) {
        userEntityRepository
                .findByUsername(username)
                .ifPresent(
                        user -> {
                            throw new UserAlreadyExistException();
                        }
                );
        // 평문 pw 암호화 필요
        var userEntity = userEntityRepository.save(UserEntity.of(username, passwordEncoder.encode(password)));
        // userentity -> user record로 변환하여 리턴
        return User.from(userEntity);
    }

    public UserAuthenticationResponse login(String username, String password) {
        // 저장되어 있는 유저 찾기
        var userEntity = userEntityRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));


        // passwordEncoder 통해서 클라이언트로부터 전달받은 pw와 암호화되어 저장되어 있는 pw 간 비교 가능
        if (passwordEncoder.matches(password, userEntity.getPassword())) {
            // jwt 기반 액세스 토큰 생성
            var accessToken = jwtService.generateAccessToken(userEntity);
            return new UserAuthenticationResponse(accessToken);
        } else {
            // 클라이언트가 보내준 username, pw가 모두 일치하는게 db상에 존재하지 않을 경우
            throw new UserNotFoundException();
        }
    }

    public List<User> getUsers(String query, UserEntity currentUser) {
        List<UserEntity> userEntities;

        if (query != null && !query.isBlank()) {
            // TODO: query 검색어 기반, 해당 검색어가 username에 포함되어 있는 유저목록 가져오기
            userEntities = userEntityRepository.findByUsernameContaining(query);
        } else {
            userEntities = userEntityRepository.findAll();
        }
        return userEntities.stream().map(
                userEntity -> getUserWithFollowingStatus(currentUser, userEntity)).toList();
    }

    public User getUser(String username, UserEntity currentUser) {
        var userEntity = userEntityRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
        return getUserWithFollowingStatus(currentUser, userEntity);
    }

    /**
     * (공통 메서드)
     * API를 호출하고 있는 유저가 팔로잉하고 있는지 상태 체크 (팔로워: currentUser, 팔로잉: userEntity)
     * @param currentUser
     * @param userEntity
     * @return
     */
    private User getUserWithFollowingStatus(UserEntity currentUser, UserEntity userEntity) {
        boolean isFollowing = followEntityRepository.findByFollowerAndFollowing(currentUser, userEntity).isPresent();
        return User.from(userEntity, isFollowing);
    }

    public User updateUser(String username, UserPatchRequestBody requestBody, UserEntity currentUser) {
        // 업데이트 하는 대상 찾기
        var userEntity = userEntityRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        if (!userEntity.equals(currentUser)) {
            throw new UserNotAllowedException();
        }

        if (requestBody.description() != null) {
            userEntity.setDescription(requestBody.description());
        }
        return User.from(userEntityRepository.save(userEntity));
    }

    /**
     * follow 기능
     * @param username
     * @param currentUser
     * @return
     */
    @Transactional
    public User follow(String username, UserEntity currentUser) {
        // 팔로우 할 대상 찾기
        var following = userEntityRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
        // 본인 스스로를 팔로우 할 수 없음
        if (following.equals(currentUser)) {
            throw new InvalidFollowException("USER CANNOT FOLLOW THEMSELVES");
        }
        // follow 추가
        // 중복 follow 검증 (api를 호출하는 주체가 follower가 됨)
        followEntityRepository.findByFollowerAndFollowing(currentUser, following)
                .ifPresent(
                        follow -> {
                            throw new FollowAlreadyExistException(currentUser, following);
                        }
                );

        followEntityRepository.save(FollowEntity.of(currentUser, following));
        // 팔로우 당하는 사람의 팔로워 숫자 증가
        following.setFollowersCount(following.getFollowersCount() + 1);
        // 팔로우 하는 주체의 팔로우 숫자 증가
        currentUser.setFollowingsCount(following.getFollowingsCount() + 1);

        // UserEntity에 follow, follower 변경 갱신 저장
//        userEntityRepository.save(following);
//        userEntityRepository.save(currentUser);
        userEntityRepository.saveAll(List.of(currentUser, following));

        // 팔로우 처리 끝나면 username을 가지고 있는 user(following)을 유저 레코드로 변환하여 반환
        return User.from(following, true);
    }


    /**
     * unfollow 기능
     * @param username
     * @param currentUser
     * @return
     */
    @Transactional
    public User unfollow(String username, UserEntity currentUser) {
        // 팔로우 취소할 대상 찾기
        var following = userEntityRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
        // 본인 스스로를 언팔로우 할 수 없음
        if (following.equals(currentUser)) {
            throw new InvalidFollowException("USER CANNOT UNFOLLOW THEMSELVES");
        }

        // followentity가 없으면 예외 던짐
        var followEntity = followEntityRepository.findByFollowerAndFollowing(currentUser, following)
                .orElseThrow(() -> new FollowNotFoundException(currentUser, following));

        followEntityRepository.delete(followEntity);
        following.setFollowersCount(Math.max(0, following.getFollowersCount() - 1));
        currentUser.setFollowingsCount(Math.max(0, following.getFollowingsCount() - 1));

        userEntityRepository.saveAll(List.of(currentUser, following));
        return User.from(following, false);
    }

    /**
     * username의 팔로워 리스트
     * @param username
     * @return
     */
    public List<User> getFollowersByUser(String username, UserEntity currentUser) {
        // 해당 유저 존재하는지 확인
        var following = userEntityRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        // 이 안에 들어있는 모든 팔로워들은 모두 동일한 팔로잉을 가짐. 위 username을 팔로우하고 있는 모든 팔로워들을 가져올 수 있음
        var followEntities = followEntityRepository.findByFollowing(following);
        // follower 유저 리스트로 변환해서 반환
        return followEntities.stream().map(follow ->
            getUserWithFollowingStatus(currentUser, follow.getFollower())).toList();
    }

    /**
     * username이 팔로워 (username이 팔로잉하고 있는 리스트)
     * @param username
     * @return
     */
    public List<User> getFollowingsByUser(String username, UserEntity currentUser) {
        var follower = userEntityRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        // 이 안에 들어있는 모든 팔로워들은 모두 동일한 팔로잉을 가짐. 위 username을 팔로우하고 있는 모든 팔로워들을 가져올 수 있음
        var followEntities = followEntityRepository.findByFollowing(follower);
        // follower 유저 리스트로 변환해서 반환
//        return followEntities.stream().map(follow ->
//                User.from(follow.getFollowing())).toList();
        return followEntities.stream().map(follow ->
                getUserWithFollowingStatus(currentUser, follow.getFollowing())).toList();
    }
}
