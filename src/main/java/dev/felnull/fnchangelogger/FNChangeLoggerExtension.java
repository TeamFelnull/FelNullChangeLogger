package dev.felnull.fnchangelogger;

import java.util.ArrayList;
import java.util.List;

public class FNChangeLoggerExtension {
    private final List<String> majorChanges = new ArrayList<>();
    private String triggerType = "tag";
    private String trigger = getDefaultTrigger();
    private String githubToken = "";
    private String repository = getDefaultRepository();
    private String releaseType = "release";
    private boolean doSkip;

    public String getTriggerType() {
        return triggerType;
    }

    public void setTriggerType(String triggerType) {
        this.triggerType = triggerType;
    }

    public String getTrigger() {
        return trigger;
    }

    public void setTrigger(String trigger) {
        this.trigger = trigger;
    }

    public String getGithubToken() {
        return githubToken;
    }

    public void setGithubToken(String githubToken) {
        this.githubToken = githubToken;
    }

    public String getRepository() {
        return repository;
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }

    public String getReleaseType() {
        return releaseType;
    }

    public void setReleaseType(String releaseType) {
        this.releaseType = releaseType;
    }

    public void addMajorChange(String text) {
        majorChanges.add(text);
    }

    public List<String> getMajorChanges() {
        return majorChanges;
    }

    public void skip() {
        doSkip = true;
    }

    public boolean isDoSkip() {
        return doSkip;
    }

    public String getDefaultTrigger() {
        String ref = System.getenv("GITHUB_REF");
        if (ref != null) {
            if (ref.startsWith("refs/")) {
                ref = ref.substring("refs/".length());
                String[] sps = ref.split("/");
                if (sps.length >= 1)
                    return ref.substring(sps[0].length() + 1);
            }
        }
        return "";
    }

    public String getDefaultRepository() {
        String rep = System.getenv("GITHUB_REPOSITORY");
        if (rep != null) return rep;
        return "";
    }
}
