package com.lifelibrarians.lifebookshelf.classification.domain;

import javax.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "materials")
@Getter
@ToString(callSuper = true, exclude = {"chunk"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Material {

    /* 고유 정보 { */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long id;

    @Column(nullable = false, name = "\"order\"")
    private Integer order;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private Integer example;

    @Column(nullable = false, name = "similar_event")
    private Integer similarEvent;

    @Column(nullable = false)
    private Integer count;

    @Lob
    @Column(nullable = false)
    private String principle;
    /* } 고유 정보 */

    /* 연관 정보 { */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chunk_id", nullable = false)
    private Chunk chunk;
    /* } 연관 정보 */

    /* 생성자 { */
    protected Material(Integer order, String name,
                       Integer example, Integer similarEvent,
                       Integer count, String principle,
                       Chunk chunk) {
        this.order = order;
        this.name = name;
        this.example = example;
        this.similarEvent = similarEvent;
        this.count = count;
        this.principle = principle;
        this.chunk = chunk;
    }

    public static Material of(Integer order, String name,
                              Integer example, Integer similarEvent,
                              Integer count, String principle,
                              Chunk chunk) {
        return new Material(order, name, example, similarEvent, count, principle, chunk);
    }
    /* } 생성자 */
}
