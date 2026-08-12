package com.theokanning.openai.space;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member {

    private String spaceCode;

    private String roleCode;

    private String memberName;

    private String memberUid;
}
