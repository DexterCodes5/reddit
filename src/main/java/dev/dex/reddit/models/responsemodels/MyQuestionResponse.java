package dev.dex.reddit.models.responsemodels;

public record MyQuestionResponse(
        int id,
        String title,
        int rating,
        int answers
) {
}
