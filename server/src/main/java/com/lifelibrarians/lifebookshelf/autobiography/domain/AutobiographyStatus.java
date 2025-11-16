package com.lifelibrarians.lifebookshelf.autobiography.domain;

import com.lifelibrarians.lifebookshelf.chapter.domain.Chapter;
import com.lifelibrarians.lifebookshelf.member.domain.Member;
import java.time.LocalDateTime;
import javax.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "autobiography_statuses")
@Getter
@ToString(callSuper = true, exclude = {"member", "currentChapter"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AutobiographyStatus {

    /* 고유 정보 { */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AutobiographyStatusType status;
    /* } 고유 정보 */

    /* 연관 정보 { */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    /**
     * 현재 진행 중인 챕터.
     * empty 상태 등에서는 null 일 수 있음.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_chapter_id")
    private Chapter currentChapter;
    /* } 연관 정보 */

    /* 생성자 { */
    protected AutobiographyStatus(AutobiographyStatusType status,
                                  Member member,
                                  Chapter currentChapter,
                                  LocalDateTime updatedAt) {
        this.status = status;
        this.member = member;
        this.currentChapter = currentChapter;
        this.updatedAt = updatedAt;
    }

    public static AutobiographyStatus of(AutobiographyStatusType status,
                                         Member member,
                                         Chapter currentChapter,
                                         LocalDateTime updatedAt) {
        return new AutobiographyStatus(status, member, currentChapter, updatedAt);
    }
    /* } 생성자 */

    /* 비즈니스 메서드 { */
    public void updateStatus(AutobiographyStatusType status,
                             Chapter currentChapter,
                             LocalDateTime now) {
        this.status = status;
        this.currentChapter = currentChapter;
        this.updatedAt = now;
    }
    /* } 비즈니스 메서드 */

    /* 상태 코드 enum { */
    public enum AutobiographyStatusType {
        // autobiography가 최초로 생성된, 아무 interview history가 없는 단계
        EMPTY,
        // 1개 이상의 interview가 존재하는 단계
        PROGRESSING,
        //자서전 생성 가능 조건(인터뷰 종료 조건)을 채운 단계, UI에서 확인 가능
        ENOUGH,
        // 자서전을 생성하고 있는 단계, 비동기로 자서전을 조각내 병렬 처리
        CREATING,
        //자서전 생성이 완료된 단계
        FINISH
    }
    /* } 상태 코드 enum */
}
