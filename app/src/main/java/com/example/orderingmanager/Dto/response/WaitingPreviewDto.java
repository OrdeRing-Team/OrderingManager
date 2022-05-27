package com.example.orderingmanager.Dto.response;

import static lombok.AccessLevel.PROTECTED;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor(access =PROTECTED)
public class WaitingPreviewDto {

    private Long waitingId;
    private Integer waitingNumber;
    private Byte numOfTeamMembers;
    private String phoneNumber;
    private String waitingRegisterTime;
}
