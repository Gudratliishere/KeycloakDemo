package com.gudratli.keycloak;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author : Dunay Gudratli
 * @mail : d.qudretli@gmail.com
 * @since : 3/13/2024
 **/
@Component
public class JwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter =
            new JwtGrantedAuthoritiesConverter();

    @Value("${jwt.auth.converter.principle-attribute}")
    private String principleAttribute;

    @Value("${jwt.auth.converter.resource-id}")
    private String resourceId;

    @Override
    public AbstractAuthenticationToken convert(@NonNull Jwt source) {
        Collection<GrantedAuthority> authorities = Stream.concat(
                        jwtGrantedAuthoritiesConverter.convert(source)
                                .stream(),
                        extractResourceRoles(source)
                                .stream())
                .collect(Collectors.toSet());
        return new JwtAuthenticationToken(source, authorities, getPrincipalClaimName(source));
    }

    private String getPrincipalClaimName(Jwt source) {
        String claimName = JwtClaimNames.SUB;
        if (principleAttribute != null)
            claimName = principleAttribute;

        return source.getClaim(claimName);
    }

    private Collection<? extends GrantedAuthority> extractResourceRoles(Jwt source) {
        Map<String, Map<String, Collection<String>>> resourceAccess = source.getClaim("resource_access");
        if (resourceAccess == null)
            return Collections.emptySet();

        Map<String, Collection<String>> resource = resourceAccess.get(resourceId);
        if (resource == null)
            return Collections.emptySet();

        Collection<String> resourceRoles = resource.get("roles");
        return resourceRoles
                .stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toSet());
    }
}
