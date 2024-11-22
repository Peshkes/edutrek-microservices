package com.telran.authenticationservice.config;

import com.telran.authenticationservice.persistence.AccountDocument;
import com.telran.authenticationservice.persistence.AccountRepository;
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

    private final AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AccountDocument user = accountRepository.findAccountDocumentByLogin(username).orElseThrow(() -> new UsernameNotFoundException(username));
        String password = user.getPassword();
        String[] roles = user.getRoles().stream().map(r -> "ROLE_" + r).toArray(String[]::new);
        return new User(username, password, AuthorityUtils.createAuthorityList(roles));
    }

}
