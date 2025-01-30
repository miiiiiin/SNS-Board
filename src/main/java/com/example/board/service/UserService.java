package com.example.board.service;

import com.example.board.exception.user.UserNotFoundException;
import com.example.board.repository.UserEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    UserEntityRepository userEntityRepository;

    // username 기반 user 찾아 전달 (user 검색하려면 repository 필요)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // optional 리턴되면 해당 유저가 존재하지 않는 다는 예외 던져줌
        return userEntityRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
    }
}
