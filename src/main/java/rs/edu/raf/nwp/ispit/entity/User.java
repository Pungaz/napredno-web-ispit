package rs.edu.raf.nwp.ispit.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import rs.edu.raf.nwp.ispit.entity.security.UserPermission;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "password", nullable = false)
    @NotBlank(message = "Password is mandatory")
    @JsonIgnore
    private String password;

    @Column(name = "username", unique = true, nullable = false)
    @NotBlank(message = "Email is mandatory")
    private String username;

    @Column(name = "first_name", nullable = false)
    @NotBlank(message = "First name is mandatory")
    private String firstName;

    @Column(name = "last_name", nullable = false)
    @NotBlank(message = "Last name is mandatory")
    private String lastName;

    @Column(name = "address")
    private String address;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<UserPermission> userPermissions;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private Set<Machine> machines;
}
