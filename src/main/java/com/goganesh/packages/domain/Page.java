package com.goganesh.packages.domain;

import lombok.*;

import javax.persistence.*;
import java.util.UUID;

@Data
@Builder
@Entity
@Table(name = "PAGES")
@NoArgsConstructor
@AllArgsConstructor
public class Page {
    @Id
    @GeneratedValue
    private UUID id;

    private String path;

    private int code;

    private String content;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "site_id", nullable = false)
    @EqualsAndHashCode.Exclude
    private Site site;

    @Override
    public String toString() {
        return "Page{" +
                "id=" + id +
                ", path='" + path + '\'' +
                ", code=" + code +
                '}';
    }


}
