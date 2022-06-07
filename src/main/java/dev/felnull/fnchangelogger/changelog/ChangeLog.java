package dev.felnull.fnchangelogger.changelog;

import com.google.gson.JsonObject;
import dev.felnull.fnchangelogger.util.GitHubUtil;
import org.apache.commons.lang3.StringUtils;
import org.gradle.api.logging.Logger;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHTag;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class ChangeLog {
    private final String releaseType;
    private final String version;
    private final String preVersion;
    private final MajorChange majorChange;
    private final Commits commits;

    public ChangeLog(String releaseType, String version, String preVersion, MajorChange majorChange, Commits commits) {
        this.releaseType = releaseType;
        this.version = version;
        this.preVersion = preVersion;
        this.majorChange = majorChange;
        this.commits = commits;
    }

    public String getReleaseType() {
        return releaseType;
    }

    public String getVersion() {
        return version;
    }

    public MajorChange getMajorChange() {
        return majorChange;
    }

    public Commits getCommits() {
        return commits;
    }

    public static ChangeLog generateChangeLog(Logger logger, String githubToken, GHRepository repository, String tag, String releaseType, List<String> majorChanges) throws IOException {
        logger.info("Generate change log...");

        List<GHTag> tags = repository.listTags().toList();
        Optional<GHTag> targetTag = tags.stream().filter(n -> tag.equals(n.getName())).findFirst();
        if (!targetTag.isPresent())
            throw new IllegalStateException("The tag cannot be found");

        GHCommit commit = targetTag.get().getCommit();
        Date commitDate = commit.getCommitDate();
        GHTag preTag = null;
        Date preTagDate = null;

        Map<String, Set<JsonObject>> branchByCommits = new HashMap<>();
        for (String branch : repository.getBranches().keySet()) {
            branchByCommits.put(branch, GitHubUtil.getBranchCommits(githubToken, repository.getFullName(), branch));
        }

        String branch = GitHubUtil.getBranchByCommit(branchByCommits, commit.getSHA1());

        logger.info("Find previous tag...");

        for (GHTag tagEntry : tags) {
            GHCommit tc = tagEntry.getCommit();

            boolean branchFlg = true;
            if (branch != null)
                branchFlg = branch.equals(GitHubUtil.getBranchByCommit(branchByCommits, tc.getSHA1()));

            if (!branchFlg)
                continue;

            Date tcd = tc.getCommitDate();
            if (preTag == null) {
                if (commitDate.compareTo(tcd) >= 1) {
                    preTag = tagEntry;
                    preTagDate = tcd;
                }
            } else {
                if (commitDate.compareTo(tcd) >= 1 && preTagDate.compareTo(tcd) <= -1) {
                    preTag = tagEntry;
                    preTagDate = tcd;
                }
            }
        }

        List<GHCommit> commits = new ArrayList<>();

        for (GHCommit commitEntry : repository.listCommits().toList()) {
            boolean branchFlg = true;
            if (branch != null)
                branchFlg = branch.equals(GitHubUtil.getBranchByCommit(branchByCommits, commitEntry.getSHA1()));

            if (!branchFlg)
                continue;


            Date gdt = commitEntry.getCommitDate();
            boolean preFlg = true;

            if (preTagDate != null)
                preFlg = gdt.compareTo(preTagDate) > 0;

            if (gdt.compareTo(commitDate) <= 0 && preFlg) {
                commits.add(commitEntry);
            }
        }

        if (commits.stream().noneMatch(n -> n.getSHA1().equals(commit.getSHA1())))
            commits.add(commit);

        Comparator<GHCommit> g = Comparator.comparingLong(n -> {
            try {
                return n.getCommitDate().getTime();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        commits = commits.stream().sorted(g.reversed()).collect(Collectors.toList());

        List<GHCommit.ShortInfo> infos = commits.stream().map(n -> {
            try {
                return n.getCommitShortInfo();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());

        logger.info("Create info...");

        return new ChangeLog(releaseType, tag, preTag != null ? preTag.getName() : null, MajorChange.generateMajorChange(infos, majorChanges), Commits.generateCommits(repository.getFullName(), commits, infos));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChangeLog changeLog = (ChangeLog) o;
        return Objects.equals(releaseType, changeLog.releaseType) && Objects.equals(version, changeLog.version) && Objects.equals(preVersion, changeLog.preVersion) && Objects.equals(majorChange, changeLog.majorChange) && Objects.equals(commits, changeLog.commits);
    }

    @Override
    public int hashCode() {
        return Objects.hash(releaseType, version, preVersion, majorChange, commits);
    }

    @Override
    public String toString() {
        return "ChangeLog{" +
                "releaseType='" + releaseType + '\'' +
                ", version='" + version + '\'' +
                ", preVersion='" + preVersion + '\'' +
                ", majorChange=" + majorChange +
                ", commits=" + commits +
                '}';
    }

    private String getVersion(String versionTag) {
        if (versionTag == null)
            return null;
        if (versionTag.startsWith("v"))
            return versionTag.substring(1);
        return versionTag;
    }


    public String createMarkdown() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("## %s **%s**", StringUtils.capitalize(releaseType), getVersion(version))).append("\n\n");
        sb.append(majorChange.createMarkdown(getVersion(preVersion))).append("\n\n");
        sb.append(commits.createMarkdown());
        return sb.toString();
    }

    public JsonObject createJson() {
        JsonObject jo = new JsonObject();
        jo.addProperty("release_type", releaseType);
        jo.addProperty("version", getVersion(version));
        jo.addProperty("pre_version", getVersion(preVersion));
        jo.add("major_change", majorChange.createJson());
        jo.add("commits", commits.createJson());
        jo.addProperty("markdown", createMarkdown());
        return jo;
    }
}
