package com.telran.securityservice.config;

import com.telran.securityservice.dto.AccountDocumentDto;
import com.telran.securityservice.feign.AuthenticationClient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Configuration
@RequiredArgsConstructor
public class UserConfig implements UserDetailsService {

    private final AuthenticationClient client;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AccountDocumentDto user = client.findByLogin(username);
        if (user == null) throw new UsernameNotFoundException(username);
        String password = user.getPassword();
        String[] roles = user.getRoles().stream().map(r -> "ROLE_" + r).toArray(String[]::new);
        return new User(username, password, AuthorityUtils.createAuthorityList(roles));
    }

}
