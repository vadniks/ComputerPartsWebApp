package some.cursov_templates.entity;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import static some.cursov_templates.Constants.ROLE;
import static some.cursov_templates.Constants.TABLE_USERS;

@NoArgsConstructor
@RequiredArgsConstructor
@Table(name = TABLE_USERS)
@Entity
public class User implements Serializable, UserDetails {

    @Nullable
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id = null;

    @NonNull
    public String  name;

    @NonNull
    public Role role;

    @NonNull
    public String password; // hash

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(
            new SimpleGrantedAuthority("%s%d".formatted(ROLE, role.ROLE)));
    }

    @NonNull
    @Override
    public String getPassword() { return password; }

    @Override
    public String getUsername() { return name; }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }

    @RequiredArgsConstructor
    public enum Role {
        USER  (0),
        ADMIN (1);

        public final Integer ROLE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;
        return Objects.equals(id, user.id) && name.equals(user.name) &&
            role == user.role && password.equals(user.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, role, password);
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public Object clone() {
        val a = new User(name, role, password);
        a.id = id;
        return a;
    }
}
