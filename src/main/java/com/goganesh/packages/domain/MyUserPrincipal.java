package com.goganesh.packages.domain;

import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@AllArgsConstructor
public class MyUserPrincipal implements UserDetails {
    private final User user;
    private final List<Authority> authorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (Objects.isNull(authorities)) {
            return Collections.emptySet();
        }
        Set<SimpleGrantedAuthority> grantedAuthorities = new HashSet<>();
        authorities.forEach(authority ->
                grantedAuthorities.add(new SimpleGrantedAuthority(authority.getAuthority())));

        return grantedAuthorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.isEnabled();
    }
}