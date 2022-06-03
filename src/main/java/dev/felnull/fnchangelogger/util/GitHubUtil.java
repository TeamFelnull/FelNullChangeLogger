package dev.felnull.fnchangelogger.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.felnull.fnjl.util.FNURLUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GitHubUtil {
    private static final Gson GSON = new Gson();
    private static final String BRANCH_COMMITS_URL = "https://api.github.com/repos/%s/commits?sha=%s&page=%s&per_page=100";

    public static String getBranchByCommit(Map<String, Set<JsonObject>> bms, String commitSha) {
        for (Map.Entry<String, Set<JsonObject>> entry : bms.entrySet()) {
            if (hasInBranch(entry.getValue(), commitSha))
                return entry.getKey();
        }
        return null;
    }

    public static boolean hasInBranch(Set<JsonObject> bms, String commitSha) {
        for (JsonObject cm : bms) {
            if (cm.has("sha") && cm.get("sha").getAsString().equals(commitSha))
                return true;
        }
        return false;
    }

    public static Set<JsonObject> getBranchCommits(String token, String rep, String branch) throws IOException {
        Set<JsonObject> jos = new HashSet<>();
        int lastCt;
        int ct = 0;
        do {
            JsonArray ja = getBranchCommits(token, rep, branch, ++ct);
            lastCt = ja.size();
            for (JsonElement entry : ja) {
                JsonObject je = entry.getAsJsonObject();
                jos.add(je);
            }
        } while (lastCt > 0);
        return jos;
    }

    private static JsonArray getBranchCommits(String token, String rep, String branch, int page) throws IOException {
        HttpURLConnection con = FNURLUtil.getConnection(new URL(String.format(BRANCH_COMMITS_URL, rep, branch, page)));
        con.addRequestProperty("user-agent", FNURLUtil.getUserAgent());
        con.setRequestProperty("Authorization", "token " + token);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
            return GSON.fromJson(reader, JsonArray.class);
        }
    }
}
