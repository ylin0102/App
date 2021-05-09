package com.harrisburg.app.domain;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetMessageFromThreadRequest {

    private List<Integer> userIds;
}
