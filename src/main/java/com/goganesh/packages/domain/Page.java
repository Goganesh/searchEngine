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
@Table(name = "PAGES")
@NoArgsConstructor
@AllArgsConstructor
public class Page implements Comparable<Page> {
    @Id
    @GeneratedValue
    private UUID id;

    private String path;

    private int code;

    private String content;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "site_id", nullable = false)
    private Site site;

    @Override
    public int compareTo(Page o) {
        return this.getPath().compareTo(o.getPath());
    }

    @Override
    public String toString() {
        return "Page{" +
                "id=" + id +
                ", path='" + path + '\'' +
                ", code=" + code +
                '}';
    }
}
