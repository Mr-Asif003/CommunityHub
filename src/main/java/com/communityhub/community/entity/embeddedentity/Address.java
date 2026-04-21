package com.communityhub.community.entity.embeddedentity;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {
    private String state;

    private String district;

    private String city;

    private String wardNumber;

    private String colonyName;
}
