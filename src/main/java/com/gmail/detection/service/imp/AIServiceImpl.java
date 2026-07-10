package com.gmail.detection.service.imp;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gmail.detection.dto.AIResponseDTO;
import com.gmail.detection.entity.Email;
import com.gmail.detection.enums.DepartmentType;
import com.gmail.detection.enums.EmailCategory;
import com.gmail.detection.enums.Priority;
import com.gmail.detection.enums.Sentiment;
import com.gmail.detection.exception.ResourceNotFoundException;
import com.gmail.detection.repository.EmailRepository;
import com.gmail.detection.service.AIService;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
@Service
public class AIServiceImpl implements AIService {

    private final EmailRepository emailRepository;

    private final Client client;

    private final ObjectMapper objectMapper;

    public AIServiceImpl(EmailRepository emailRepository,
                          @Value("${gemini.api.key}") String geminiApiKey) {
        this.emailRepository = emailRepository;
        this.client = Client.builder()
                .apiKey(geminiApiKey)
                .build();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    // ---------------------------
    // ADD THE METHOD HERE
    // ---------------------------



    @Override
    public AIResponseDTO classifyEmail(Long emailId) {

        Email email = emailRepository.findById(emailId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Email not found : " + emailId));

        String prompt = """
                You are an Enterprise AI Email Classifier.

                Analyze the email and provide:

                Category must be ONLY one of these values:
                
                WORK
                PERSONAL
                FINANCE
                PROMOTION
                SOCIAL
                SUPPORT
                OTHER
                
                Do NOT generate any other category.
                Department:
                Priority:
                Spam:
                Department:
                Choose ONLY ONE of these values:
                
                HR
                IT
                SALES
                MARKETING
                FINANCE
                SUPPORT
                
                Never invent any other department.
                Return only one of the above.
                Reason:

                Subject:
                %s

                Body:
                %s
                """
                .formatted(
                        email.getSubject(),
                        email.getBody()
                );

        String aiResponse = askGemini(prompt);

        AIResponseDTO dto = new AIResponseDTO();
        dto.setResponse(aiResponse);



        return dto;
    }

    @Override
    public AIResponseDTO summarizeEmail(Long emailId) {

        Email email = emailRepository.findById(emailId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Email not found : " + emailId));

        String prompt = """
            You are an AI Email Assistant.

            Read the following email carefully and generate a concise summary.

            Rules:
            - Maximum 5 bullet points.
            - Keep the important information only.
            - Ignore greetings.
            - Ignore signatures.
            - Professional language.

            Subject:
            %s

            Body:
            %s
            """
                .formatted(
                        email.getSubject(),
                        email.getBody()
                );

        String aiSummary = askGemini(prompt);

        AIResponseDTO dto = new AIResponseDTO();

        dto.setSummary(aiSummary);
        dto.setResponse(aiSummary);

        return dto;
    }

    @Override
    public AIResponseDTO generateSmartReply(Long emailId) {
        return generateSmartReply(emailId, "professional");
    }

    @Override
    public AIResponseDTO generateSmartReply(Long emailId, String tone) {

        Email email = emailRepository.findById(emailId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Email not found : " + emailId));

        String toneInstructions = toneInstructions(tone);

        String prompt = """
            You are an Enterprise AI Email Assistant.

            Generate an email reply for the following email.

            Rules:
            %s
            - Keep the reply between 60-150 words.
            - Do not invent information that isn't in the original email.
            - End with an appropriate closing for the requested tone.

            Subject:
            %s

            Email Body:
            %s
            """
                .formatted(
                        toneInstructions,
                        email.getSubject(),
                        email.getBody()
                );

        String smartReply = askGemini(prompt);

        AIResponseDTO dto = new AIResponseDTO();

        dto.setSmartReply(smartReply);
        dto.setResponse(smartReply);

        return dto;
    }

    // Maps a requested tone keyword to concrete writing instructions for the prompt.
    // Falls back to "professional" for anything unrecognized.
    private String toneInstructions(String tone) {

        String normalized = tone == null ? "professional" : tone.trim().toLowerCase();

        return switch (normalized) {
            case "friendly" -> "- Be warm, friendly, and conversational.\n"
                    + "- Use a casual but respectful tone, like writing to a colleague you know well.";
            case "formal" -> "- Be highly formal and businesslike.\n"
                    + "- Avoid contractions and casual phrasing entirely.";
            case "quick" -> "- Be extremely brief - 2-3 sentences maximum.\n"
                    + "- Get straight to the point with no filler.";
            case "one-click", "oneclick" -> "- Provide the shortest possible acknowledgement reply (1 sentence).\n"
                    + "- Suitable for a single tap \"send\" action with no editing needed.";
            default -> "- Be polite and professional.\n"
                    + "- Reply in business English.\n"
                    + "- Thank the sender.";
        };
    }

    @Override
    public AIResponseDTO analyzeCompleteEmail(Long emailId) {

        Email email = emailRepository.findById(emailId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Email not found : " + emailId));
        String prompt = """
You are an Enterprise AI Email Classifier.

Analyze this email carefully.

Return ONLY valid JSON.

Do not use markdown.

Do not explain anything.

The response MUST exactly match this JSON shape (keywords and entities are JSON arrays of strings; scores are integers 0-100; every boolean field must be true or false, never null):

{
  "category":"WORK",
  "priority":"LOW",
  "department":"IT",
  "sentiment":"POSITIVE",
  "spam":false,
  "summary":"summary",
  "smartReply":"reply",
  "reason":"reason",
  "response":"Analysis completed.",
  "keywords":["keyword1","keyword2"],
  "entities":["Entity One","Entity Two"],
  "language":"English",
  "urgent":false,
  "confidenceScore":90,
  "importanceScore":70,
  "meetingDetected":false,
  "deadlineDetected":false,
  "taskDetected":false,
  "actionRequired":false,
  "riskDetected":false,
  "emotion":"neutral",
  "intent":"inform",
  "topic":"short topic phrase",
  "suggestedLabel":"short label",
  "suggestedFolder":"Inbox",
  "autoArchiveSuggested":false,
  "autoDeleteSuggested":false,
  "followUpSuggested":false,
  "reminderSuggested":false
}

Allowed category values:

WORK
PERSONAL
FINANCE
PROMOTION
SOCIAL
SUPPORT
OTHER

Allowed priority values:

LOW
MEDIUM
HIGH

Allowed department values:

HR
IT
FINANCE
SALES
MARKETING
SUPPORT
GENERAL

Allowed sentiment values:

POSITIVE
NEGATIVE
NEUTRAL

Field guidance:
- keywords: 3-8 important words/phrases from the email, most significant first.
- entities: named people, companies, products, or places mentioned (not generic words).
- language: the human language the email is written in (e.g. "English").
- urgent: true only if the email demands attention within hours, not days.
- confidenceScore: how confident you are in this overall classification (0-100).
- importanceScore: how important this email is for the recipient's work (0-100).
- meetingDetected: true if the email proposes, confirms, or discusses a meeting/call.
- deadlineDetected: true if the email mentions a specific date/time deadline.
- taskDetected: true if the email asks the recipient to do something.
- actionRequired: true if a reply or action is genuinely expected from the recipient.
- riskDetected: true if the email contains signs of fraud, phishing, or urgent financial risk.
- emotion: the sender's apparent emotion in one word (e.g. "frustrated", "happy", "neutral").
- intent: the sender's primary goal in one short phrase (e.g. "request approval").
- topic: a 2-5 word topic label for this email.
- suggestedLabel: a short Gmail-style label this email should be tagged with.
- suggestedFolder: one of Inbox, Archive, Spam, or a department name, whichever fits best.
- autoArchiveSuggested: true if this email is safe to auto-archive (e.g. an FYI notice).
- autoDeleteSuggested: true only for obvious spam/promotional junk.
- followUpSuggested: true if the recipient should follow up if no reply is received.
- reminderSuggested: true if a calendar/task reminder should be created for this email.

Subject:
%s

Body:
%s
"""
                .formatted(
                        email.getSubject(),
                        email.getBody()
                );

        String json = "";

        try {

            json = askGemini(prompt);

            json = json.replace("```json", "")
                    .replace("```", "")
                    .trim();

            System.out.println("=========== GEMINI RESPONSE ===========");
            System.out.println(json);
            System.out.println("=======================================");

            AIResponseDTO dto =
                    objectMapper.readValue(json, AIResponseDTO.class);

            applyAnalysisToEmail(email, dto);
            emailRepository.save(email);

            return dto;

        } catch (Exception e) {

            e.printStackTrace();

            System.out.println("=========== JSON PARSE FAILED ==========");
            System.out.println(json);
            System.out.println("========================================");

            AIResponseDTO dto = new AIResponseDTO();
            dto.setResponse("Parsing Failed");

            return dto;
        }
    }
    private String askGemini(String prompt) {

        GenerateContentResponse response =
                client.models.generateContent(
                        "gemini-2.5-flash",
                        prompt,
                        null
                );

        return response.text();
    }

    // Writes a completed AI analysis onto the Email row itself, so
    // classification actually sticks instead of being recomputed (and
    // discarded) on every read. This is also what auto-routing relies on to
    // decide which department an email should land in.
    private void applyAnalysisToEmail(Email email, AIResponseDTO dto) {

        if (dto.getCategory() != null) {
            enumOf(EmailCategory.class, dto.getCategory()).ifPresent(email::setCategory);
        }
        if (dto.getPriority() != null) {
            enumOf(Priority.class, dto.getPriority()).ifPresent(email::setPriority);
        }
        if (dto.getDepartment() != null) {
            enumOf(DepartmentType.class, dto.getDepartment()).ifPresent(email::setDepartment);
        }
        if (dto.getSentiment() != null) {
            enumOf(Sentiment.class, dto.getSentiment()).ifPresent(email::setSentiment);
        }
        if (dto.getSpam() != null) {
            email.setSpam(dto.getSpam());
        }

        email.setSummary(dto.getSummary());
        email.setSmartReply(dto.getSmartReply());
        email.setReason(dto.getReason());
        email.setKeywords(joinOrNull(dto.getKeywords()));
        email.setEntities(joinOrNull(dto.getEntities()));
        email.setLanguage(dto.getLanguage());
        email.setUrgent(dto.getUrgent());
        email.setConfidenceScore(dto.getConfidenceScore());
        email.setImportanceScore(dto.getImportanceScore());
        email.setMeetingDetected(dto.getMeetingDetected());
        email.setDeadlineDetected(dto.getDeadlineDetected());
        email.setTaskDetected(dto.getTaskDetected());
        email.setActionRequired(dto.getActionRequired());
        email.setRiskDetected(dto.getRiskDetected());
        email.setEmotion(dto.getEmotion());
        email.setIntent(dto.getIntent());
        email.setTopic(dto.getTopic());
        email.setSuggestedLabel(dto.getSuggestedLabel());
        email.setSuggestedFolder(dto.getSuggestedFolder());
        email.setAutoArchiveSuggested(dto.getAutoArchiveSuggested());
        email.setAutoDeleteSuggested(dto.getAutoDeleteSuggested());
        email.setFollowUpSuggested(dto.getFollowUpSuggested());
        email.setReminderSuggested(dto.getReminderSuggested());
        email.setUpdatedTime(LocalDateTime.now());
    }

    private <E extends Enum<E>> java.util.Optional<E> enumOf(Class<E> type, String raw) {
        try {
            return java.util.Optional.of(Enum.valueOf(type, raw.trim().toUpperCase()));
        } catch (Exception e) {
            return java.util.Optional.empty();
        }
    }

    private String joinOrNull(List<String> values) {
        return (values == null || values.isEmpty()) ? null : String.join(", ", values);
    }
}