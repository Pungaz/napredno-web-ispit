package rs.edu.raf.nwp.ispit.entity.security;

import lombok.*;
import rs.edu.raf.nwp.ispit.entity.User;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class UserPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "permission_id", nullable = false)
    private Permission permission;

    @Column
    private String username;

    @Column
    private String permissionName;

}
