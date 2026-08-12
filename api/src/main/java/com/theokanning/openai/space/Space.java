package com.theokanning.openai.space;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Space {

    private String spaceCode;

    private String spaceName;

    private String ownerUid;
}
