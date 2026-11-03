package com.hassan.ems.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LeaveReviewRequest {

    @Size(max = 500, message = "Comment must not exceed 500 characters")
    private String comment;
}
