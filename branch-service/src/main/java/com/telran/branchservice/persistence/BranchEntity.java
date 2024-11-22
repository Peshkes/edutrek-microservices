package com.telran.branchservice.persistence;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(schema = "current", name = "branches")
@Getter
@NoArgsConstructor
public class BranchEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "branch_id")
    private int branchId;
    @Setter
    @Column(name = "branch_name")
    private String branchName;
    @Setter
    @Column(name = "branch_address")
    private String branchAddress;


    public BranchEntity(String branchName, String branchAddress) {
        this.branchName = branchName;
        this.branchAddress = branchAddress;
    }
}
