package rs.edu.raf.nwp.ispit.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Machine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    private LocalDate dateCreated;

    @Enumerated(EnumType.STRING)
    private Status status;

    private boolean active;

    @Version
    private Integer version;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    private User user;
}