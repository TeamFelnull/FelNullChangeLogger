package dev.felnull.fnchangelogger;

import java.util.ArrayList;
import java.util.List;

public class FNChangeLoggerExtension {
    private final List<String> majorChanges = new ArrayList<>();
    private String triggerType = "tag";
    private String trigger = "";
    private String githubToken = "";
    private String repository = "";
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
}
