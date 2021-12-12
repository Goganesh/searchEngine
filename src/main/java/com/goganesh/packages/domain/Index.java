package com.goganesh.packages.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.UUID;

@Data
@Builder
@Entity
@Table(name = "INDEXES")
@NoArgsConstructor
@AllArgsConstructor
public class Index {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "page_id")
    private Page page;

    private String lemma;

    private float rank;
}
