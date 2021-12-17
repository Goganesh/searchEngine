package com.goganesh.packages.service.implementation;

import com.goganesh.packages.domain.Authority;
import com.goganesh.packages.domain.MyUserPrincipal;
import com.goganesh.packages.domain.User;
import com.goganesh.packages.repository.AuthorityRepository;
import com.goganesh.packages.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;

    @Override
    public MyUserPrincipal loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }

        List<Authority> authorities = authorityRepository.findByUser(user);

        return new MyUserPrincipal(user, authorities);
    }
}
