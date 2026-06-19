package com.innovativesoftware.domsommelier_backend.auth_management;

import com.innovativesoftware.domsommelier_backend.customer_management.customer.entity.Customer;
import com.innovativesoftware.domsommelier_backend.shared.domain.Role;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Getter
public class AppUserDetails implements UserDetails {

    private final UUID id;
    private final String email;
    private final String password;
    private final Customer customer;

    public AppUserDetails(Customer customer) {
        this.id = customer.getId();
        this.email = customer.getEmail();
        this.customer = customer;
        this.password = "";
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Role role = customer.getRoleOrDefault();
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}