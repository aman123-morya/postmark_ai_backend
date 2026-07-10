package com.gmail.detection.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class AutoRouteSummaryDTO {

    private int processed;

    private int spamCaught;

    private Map<String, Long> routedByDepartment;

    private List<RouteResultDTO> results;
}
