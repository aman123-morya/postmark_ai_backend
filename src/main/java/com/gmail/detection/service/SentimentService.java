package com.gmail.detection.service;

public interface SentimentService {

    String analyzeSentiment(Long emailId);

    String analyzeText(String text);

}