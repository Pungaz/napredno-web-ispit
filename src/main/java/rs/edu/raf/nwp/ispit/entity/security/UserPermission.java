package rs.edu.raf.nwp.ispit.entity.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import rs.edu.raf.nwp.ispit.entity.User;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class UserPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    @ToString.Exclude
    private User user;

    @ManyToOne
    @JoinColumn(name = "permission_id", nullable = false)
    @JsonIgnore
    @ToString.Exclude
    private Permission permission;

    @Column(name = "username")
    private String username;

    @Column(name = "permission_name")
    private String permissionName;

}
