package com.communityhub.community.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CommunityType {

    COLONY("Colony"),
    WARD("Ward"),
    OFFICIAL("Officials"),
    EDUCATION("Education"),
    PERSONAL("Personal"),
    MUNICIPALITY("Municipality");

    private final String displayName;

    CommunityType(String displayName) {
        this.displayName = displayName;
    }

    // =========================================
    // RESPONSE VALUE
    // =========================================
    @JsonValue
    public String getDisplayName() {
        return displayName;
    }

    // =========================================
    // REQUEST VALUE PARSER
    // =========================================
    @JsonCreator
    public static CommunityType fromValue(
            String value
    ) {

        for (CommunityType type : CommunityType.values()) {

            if (
                    type.name().equalsIgnoreCase(value)
                            ||
                            type.displayName.equalsIgnoreCase(value)
            ) {
                return type;
            }
        }

        throw new IllegalArgumentException(
                "Invalid CommunityType: " + value
        );
    }
}