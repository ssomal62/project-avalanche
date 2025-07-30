package site.leesoyeon.avalanche.user.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.UuidGenerator;
import site.leesoyeon.avalanche.user.shared.enums.UserRole;
import site.leesoyeon.avalanche.user.shared.enums.UserStatus;

import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Entity
@Table(name = "users")
public class User extends BaseTimeEntity {

    @Id
    @UuidGenerator
    @Column(name = "user_id", updatable = false, nullable = false)
    private UUID userId;

    @Column(unique = true, nullable = false)
    @ColumnTransformer(
            read = "pgp_sym_decrypt(email::bytea, current_setting('app.encryption_key'))",
            write = "pgp_sym_encrypt(?, current_setting('app.encryption_key'))"
    )
    private String email;

    @Setter
    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @ColumnTransformer(
            read = "pgp_sym_decrypt(name::bytea, current_setting('app.encryption_key'))",
            write = "pgp_sym_encrypt(?, current_setting('app.encryption_key'))"
    )
    private String name;

    @Column(nullable = false)
//    @ColumnTransformer(
//            read = "pgp_sym_decrypt(nickname::bytea, current_setting('app.encryption_key'))",
//            write = "pgp_sym_encrypt(?, current_setting('app.encryption_key'))"
//    )
    private String nickname;

    @Column
    @ColumnTransformer(
            read = "pgp_sym_decrypt(phone::bytea, current_setting('app.encryption_key'))",
            write = "pgp_sym_encrypt(?, current_setting('app.encryption_key'))"
    )
    private String phone;

    @ColumnTransformer(
            read = "pgp_sym_decrypt(address::bytea, current_setting('app.encryption_key'))",
            write = "pgp_sym_encrypt(?, current_setting('app.encryption_key'))"
    )
    private String address;

    @Column(name = "detailed_address")
    @ColumnTransformer(
            read = "pgp_sym_decrypt(detailed_address::bytea, current_setting('app.encryption_key'))",
            write = "pgp_sym_encrypt(?, current_setting('app.encryption_key'))"
    )
    private String detailedAddress;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role = UserRole.ROLE_CUSTOMER;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status = UserStatus.ACTIVE;

    private boolean emailVerified;

    public void updatePassword(String password) {
        this.password = password;
    }

    public User updateNickname(String nickname) {
        this.nickname = nickname;
        return this;
    }

    public User updatePhone(String phone) {
        this.phone = phone;
        return this;
    }

    public User updateAddress(String address, String detailedAddress) {
        this.address = address;
        this.detailedAddress = detailedAddress;
        return this;
    }

    public User updateRole(UserRole role) {
        this.role = role;
        return this;
    }

    public User updateStatus(UserStatus status) {
        this.status = status;
        return this;
    }

    public User updateEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
        return this;
    }

}