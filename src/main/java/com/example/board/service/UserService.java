package com.example.board.service;

import com.example.board.exception.user.UserAlreadyExistException;
import com.example.board.exception.user.UserNotFoundException;
import com.example.board.model.entity.UserEntity;
import com.example.board.model.user.User;
import com.example.board.repository.UserEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserEntityRepository userEntityRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

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
}
