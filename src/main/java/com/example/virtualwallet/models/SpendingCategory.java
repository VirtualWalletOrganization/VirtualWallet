package com.example.virtualwallet.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "spending_categories")
public class SpendingCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private int id;

    @Column(name = "category_name")
    private String name;

//    @JsonIgnore
//    @OneToMany(mappedBy = "spendingCategory", fetch = FetchType.EAGER)
//    private Set<Transfer> transfers;

    public SpendingCategory() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

//    public Set<Transfer> getTransfers() {
//        return transfers;
//    }
//
//    public void setTransfers(Set<Transfer> transfers) {
//        this.transfers = transfers;
//    }
}