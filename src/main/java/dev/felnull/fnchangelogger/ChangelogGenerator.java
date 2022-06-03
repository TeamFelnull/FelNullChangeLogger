package dev.felnull.fnchangelogger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.felnull.fnchangelogger.changelog.ChangeLog;
import org.gradle.api.Project;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;

import java.io.*;
import java.nio.file.Files;
import java.util.List;

public class ChangelogGenerator {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final ChangelogGenerator INSTANCE = new ChangelogGenerator();

    public static ChangelogGenerator getInstance() {
        return INSTANCE;
    }

    public void start(Project project, String triggerType, String trigger, String githubToken, String repository, String releaseType, List<String> majorChanges) throws Exception {
        if ("tag".equals(triggerType)) {
            startTagType(project, trigger, githubToken, repository, releaseType, majorChanges);
            return;
        }
        throw new IllegalStateException("Not support trigger");
    }

    private void startTagType(Project project, String versionTag, String githubToken, String repository, String releaseType, List<String> majorChanges) throws Exception {
        if (versionTag.isEmpty())
            throw new IllegalStateException("Trigger version tag is empty");
        if (githubToken.isEmpty())
            throw new IllegalStateException("Github token is empty");
        if (repository.isEmpty())
            throw new IllegalStateException("Repository is empty");
        if (releaseType.isEmpty())
            throw new IllegalStateException("Release type is empty");

        GitHub github = new GitHubBuilder().withOAuthToken(githubToken).build();
        GHRepository rep = github.getRepository(repository);
        ChangeLog changeLog = ChangeLog.generateChangeLog(project.getLogger(), githubToken, rep, versionTag, releaseType, majorChanges);

        File clogFol = new File(project.getBuildDir(), "changelog");
        clogFol.mkdirs();

        project.getLogger().debug("Generate changelog markdown");
        File clogMd = new File(clogFol, "changelog.md");
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(clogMd.toPath())))) {
            writer.write(changeLog.createMarkdown());
        }

        project.getLogger().debug("Generate changelog json");
        File clogJson = new File(clogFol, "changelog.json");
        try (Writer writer = new OutputStreamWriter(new BufferedOutputStream(Files.newOutputStream(clogJson.toPath())))) {
            GSON.toJson(changeLog.createJson(), writer);
        }
    }
}
