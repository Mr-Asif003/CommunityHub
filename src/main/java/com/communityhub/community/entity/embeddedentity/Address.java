package com.communityhub.community.entity.embeddedentity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {

    // =========================================
    // STATE
    // =========================================
    private String state;

    // =========================================
    // DISTRICT
    // =========================================
    private String district;

    // =========================================
    // CITY
    // =========================================
    private String city;

    // =========================================
    // WARD
    // =========================================
    private String wardNumber;

    // =========================================
    // COLONY
    // =========================================
    private String colonyName;
}