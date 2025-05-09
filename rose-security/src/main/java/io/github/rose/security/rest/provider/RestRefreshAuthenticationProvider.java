/*
 * Copyright © 2025 rose-group.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.rose.security.rest.provider;

import io.github.rose.security.rest.token.RestRefreshAuthenticationToken;
import io.github.rose.security.support.TokenFactory;
import io.github.rose.security.util.SecurityUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@RequiredArgsConstructor
public class RestRefreshAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;

    private final TokenFactory tokenFactory;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Assert.notNull(authentication, "No authentication data provided");
        String jwtToken = (String) authentication.getCredentials();
        SecurityUser unsafeUser = tokenFactory.parseRefreshToken(jwtToken);
        SecurityUser securityUser = authenticateByUserId(unsafeUser.getUsername());
        return new RestRefreshAuthenticationToken(securityUser);
    }

    private SecurityUser authenticateByUserId(String username) {
        UserDetails user = userDetailsService.loadUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found by refresh token");
        }

        return new SecurityUser(user.getUsername(), user.getPassword(), user.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (RestRefreshAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
