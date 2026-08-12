package com.theokanning.openai.file;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProgressUpdateRequest {

    private String status;

    private String message;

    private Integer percent;
}
