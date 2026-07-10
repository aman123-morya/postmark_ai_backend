package com.gmail.detection.dto;

import com.gmail.detection.enums.EmailStatus;
import lombok.Data;

/**
 * Partial update for the mutable inbox flags on an email - star, archive,
 * and workflow status (e.g. moving OPEN -> IN_PROGRESS -> RESOLVED). Fields
 * left null are left untouched.
 */
@Data
public class EmailUpdateRequest {

    private Boolean starred;

    private Boolean archived;

    private EmailStatus status;
}
