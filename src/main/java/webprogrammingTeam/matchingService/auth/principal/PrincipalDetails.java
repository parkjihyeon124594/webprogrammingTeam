package webprogrammingTeam.matchingService.auth.principal;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import webprogrammingTeam.matchingService.domain.user.entity.User;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@Getter
public class PrincipalDetails implements OAuth2User{

    private User user;
    private Map<String, Object> attributes;

    /**
     * 자체 로그인
     */
    public PrincipalDetails(User user) {
        this.user = user;
    }
    /**
     * OAuth2 로그인
     */
    public PrincipalDetails(User user,Map<String ,Object> attributes) {
        this.user = user;
        this.attributes=attributes;
    }



    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Collection<GrantedAuthority> collection =new ArrayList<>();

        collection.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return String.valueOf(user.getRole());
            }
        });
        return collection;
    }
/*

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return user.getUserName();
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
        return true;
    }
*/

    // OAuth2

    @Override
    public String getName() {
        return user.getUserName();
    }
    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    public String getEmail() {
        return user.getEmail();
    }
}
