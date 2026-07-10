package com.gmail.detection.controller;

import com.gmail.detection.dto.AIResponseDTO;
import com.gmail.detection.dto.AutoRouteSummaryDTO;
import com.gmail.detection.dto.RouteResultDTO;
import com.gmail.detection.service.AIService;
import com.gmail.detection.service.EmailRoutingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
public class AIController {

    private final AIService aiService;
    private final EmailRoutingService emailRoutingService;

    public AIController(AIService aiService, EmailRoutingService emailRoutingService) {
        this.aiService = aiService;
        this.emailRoutingService = emailRoutingService;
    }

    @GetMapping("/classify/{id}")
    public AIResponseDTO classify(@PathVariable Long id) {
        return aiService.classifyEmail(id);
    }

    @GetMapping("/summary/{id}")
    public AIResponseDTO summary(@PathVariable Long id) {
        return aiService.summarizeEmail(id);
    }

    // tone: professional (default), friendly, formal, quick, one-click
    @GetMapping("/reply/{id}")
    public AIResponseDTO reply(@PathVariable Long id,
                                @RequestParam(defaultValue = "professional") String tone) {
        return aiService.generateSmartReply(id, tone);
    }

    // Full analysis: category, priority, department, sentiment, spam, summary,
    // smart reply, keywords, entities, language, urgency, confidence/importance
    // scores, meeting/deadline/task/action/risk detection, emotion, intent,
    // topic, label/folder suggestions, and archive/delete/follow-up/reminder
    // suggestions - all in one Gemini call.
    @GetMapping("/analyze/{id}")
    public AIResponseDTO analyze(@PathVariable Long id) {
        return aiService.analyzeCompleteEmail(id);
    }

    // Classifies one email and automatically files it: spam gets archived
    // into the spam log, everything else gets assigned to the right
    // department (HR/IT/SALES/...) and load-balanced to a teammate there.
    @PostMapping("/auto-route/{id}")
    public RouteResultDTO autoRoute(@PathVariable Long id) {
        return emailRoutingService.autoRoute(id);
    }

    // Sweeps every not-yet-triaged (RECEIVED) email and auto-routes each
    // one in a single call - this is the "sift my whole inbox" action.
    @PostMapping("/auto-route-all")
    public AutoRouteSummaryDTO autoRouteAll() {
        return emailRoutingService.autoRouteAll();
    }
}
