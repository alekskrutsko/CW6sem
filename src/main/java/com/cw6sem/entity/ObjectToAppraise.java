package com.cw6sem.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name="objecttoappraise")
public class ObjectToAppraise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "objectType_id", referencedColumnName = "id", nullable = false)
    private ObjectType objectType;
    private String description;
}
