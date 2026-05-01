package com.communityhub.poll.dto;


import lombok.Data;

@Data
public class VoteRequest {
    private String pollId;

    private String selectedOption;
}
