package com.goganesh.packages.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@Entity
@Table(name = "SITES")
@NoArgsConstructor
@AllArgsConstructor
public class Site {

    @Id
    @GeneratedValue
    private UUID id;

    private String url;

    private String name;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "status_time")
    private LocalDateTime statusTime;

    @Column(name = "last_error")
    private String lastError;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "site")
    @EqualsAndHashCode.Exclude
    private List<Page> pages;

    public enum Status {
        NEW,
        INDEXING,
        INDEXED,
        FAILED
    }

    @Override
    public String toString() {
        return "Site{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", statusTime=" + statusTime +
                ", lastError='" + lastError + '\'' +
                '}';
    }
}
