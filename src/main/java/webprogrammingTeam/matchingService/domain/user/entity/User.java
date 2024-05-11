package webprogrammingTeam.matchingService.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="user_name")
    private String userName;
    @Column(name="user_email")
    private String email;
    @Column(name="user_password")
    private String userPassword;

    @Enumerated(EnumType.STRING)
    @Column(name="role")
    private Role role;

    @Builder
    public User(String userName,String userPassword,Role role,String email){
        this.userName=userName;
        this.userPassword=userPassword;
        this.role=role;
        this.email=email;
    }



}
