package com.guenbon.siso.util;

public class PromptBuilder {
    private static final String SYSTEM_MESSAGE = String.join("\n",
            "Please summarize the following Korean legislative proposal in the following format.",
            "Respond in Korean only, without field names or labels.",
            "Use line breaks to separate the following 4 items in this exact order:",
            "1. Category of the bill (e.g., 환경, 교육, 복지, etc.)",
            "2. A brief one-line summary of the bill's content (ending with '이다.')",
            "3. A one-line explanation of the reason behind the proposal (ending with '되었다.' or '위해 발의되었다.')",
            "4. A one-line summary of the expected effect or impact (ending with '기대된다.')",
            "",
            "Return only these 4 lines, without labels or extra explanations. Keep Korean characters intact in UTF-8."
    );

    public static String getBillSummaryPrompt() {
        return SYSTEM_MESSAGE;
    }
}
