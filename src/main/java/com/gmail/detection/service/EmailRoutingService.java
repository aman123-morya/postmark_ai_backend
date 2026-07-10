package com.gmail.detection.service;

import com.gmail.detection.dto.AutoRouteSummaryDTO;
import com.gmail.detection.dto.RouteResultDTO;

public interface EmailRoutingService {

    /**
     * Runs full AI analysis on one email, persists the classification, and
     * either files it as spam or automatically creates a department
     * Assignment for the right team (e.g. an IT-tagged email gets routed
     * to whichever IT teammate currently has the lightest open load).
     */
    RouteResultDTO autoRoute(Long emailId);

    /**
     * Sweeps every email still in RECEIVED status (i.e. not yet triaged)
     * and auto-routes each one. This is what powers "sift my whole inbox
     * into departments" in one click.
     */
    AutoRouteSummaryDTO autoRouteAll();
}
