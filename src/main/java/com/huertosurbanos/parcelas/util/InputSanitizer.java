package com.huertosurbanos.parcelas.util;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class InputSanitizer {

    private static final Pattern SCRIPT_TAGS = Pattern.compile(
            "(?i)<script[^>]*>[\\s\\S]*?</script>"
    );
    private static final Pattern HTML_TAGS = Pattern.compile("(?i)<[^>]+>");
    private static final Pattern CONTROL_CHARS = Pattern.compile("\\p{Cntrl}");
    private static final Pattern MULTI_WHITESPACE = Pattern.compile("\\s+");

    public String sanitizeText(String value) {
        if (value == null) {
            return null;
        }
        String withoutScripts = SCRIPT_TAGS.matcher(value).replaceAll("");
        String withoutHtml = HTML_TAGS.matcher(withoutScripts).replaceAll("");
        String withoutControl = CONTROL_CHARS.matcher(withoutHtml).replaceAll("");
        return MULTI_WHITESPACE.matcher(withoutControl).replaceAll(" ").trim();
    }

    public String normalizeSoilType(String value) {
        if (value == null) {
            return null;
        }
        return value.trim().toUpperCase();
    }
}
