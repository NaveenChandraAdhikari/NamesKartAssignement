package org.example;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SocialMediaChecker {
    private static final String[] SOCIAL_MEDIA_URLS = {
            "https://www.linkedin.com/company/%s",
            "https://www.facebook.com/%s",
            "https://www.instagram.com/%s"
    };

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java SocialMediaChecker <domain>");
            return;
        }

        String domain = args[0];
        Map<String, Boolean> results = checkSocialMediaProfiles(domain);

        System.out.println("Results for " + domain + ":");
        for (Map.Entry<String, Boolean> entry : results.entrySet()) {
            System.out.println(entry.getKey() + ": " + (entry.getValue() ? "Active" : "Unable to determine"));
        }
    }

    private static Map<String, Boolean> checkSocialMediaProfiles(String domain) {
        Map<String, Boolean> results = new HashMap<>();
        String companyName = extractCompanyName(domain);

        for (String urlTemplate : SOCIAL_MEDIA_URLS) {
            String url = String.format(urlTemplate, companyName);
            boolean isActive = checkProfileActivity(url);
            results.put(url, isActive);
        }

        return results;
    }

    private static String extractCompanyName(String domain) {
        return domain.split("\\.")[0];
    }

    private static boolean checkProfileActivity(String url) {
        try {
            HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "Mozilla/5.0");

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // If we can access the page, assume it's active
                return true;
            } else if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
                // If page not found, it's likely inactive
                return false;
            } else {
                // For other response codes, we can't determine
                System.out.println("Received response code " + responseCode + " for " + url);
                return false;
            }
        } catch (Exception e) {
            System.err.println("Error checking " + url + ": " + e.getMessage());
            return false;
        }
    }
}