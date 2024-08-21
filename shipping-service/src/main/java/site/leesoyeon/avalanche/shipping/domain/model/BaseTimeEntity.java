package site.leesoyeon.avalanche.shipping.domain.model;


import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * * BaseTimeEntity는 엔티티 클래스에 공통적으로 사용되는
 * 생성 시간과 수정 시간을 자동으로 관리하기 위한
 * 기본 클래스를 제공합니다.
 *
 * <p>* 이 클래스를 상속받는 엔티티 클래스는
 * 해당 엔티티가 생성되거나 수정될 때
 * 자동으로 시간을 기록하게 됩니다.</p>
 *
 * <pre>{@code
 * //예시
 * @Entity
 * public class User extends BaseTimeEntity {
 *     // 사용자 필드들...
 * }
 * }</pre>
 */

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseTimeEntity {

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime modifiedDate;
}
