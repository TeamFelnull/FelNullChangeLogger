package dev.felnull.fnchangelogger.changelog;

import com.google.gson.JsonArray;
import org.kohsuke.github.GHCommit;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Commits {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
    private final List<CommitEntry> commitEntries;

    public Commits(List<CommitEntry> commitEntries) {
        this.commitEntries = commitEntries;
    }

    public static Commits generateCommits(String repository, List<GHCommit> commits, List<GHCommit.ShortInfo> infos) throws IOException {
        List<CommitEntry> coms = new ArrayList<>();
        for (int i = 0; i < commits.size(); i++) {
            coms.add(CommitEntry.createCommitEntry(repository, commits.get(i), infos.get(i)));
        }
        return new Commits(Collections.unmodifiableList(coms));
    }

    public List<CommitEntry> getCommitEntries() {
        return commitEntries;
    }

    private String getChangeAddDeleteText() {
        int change = 0;
        int add = 0;
        int del = 0;
        for (CommitEntry commitEntry : commitEntries) {
            change += commitEntry.getChangedFile();
            add += commitEntry.getAdditionLine();
            del += commitEntry.getDeletionLine();
        }
        return String.format("%s changed file with %s addition and %s deletion in total", change, add, del);
    }

    public String createMarkdown() {
        StringBuilder sb = new StringBuilder();
        sb.append("### New Commit").append("\n");
        sb.append(String.format("%s commits  ", commitEntries.size())).append("\n");
        sb.append(getChangeAddDeleteText()).append("\n");

        for (CommitEntry entry : commitEntries) {
            sb.append("\n");
            sb.append("- ");
            sb.append(String.format("[%s](%s)", entry.getName(), entry.getUrl()));
            sb.append(String.format(" by [%s](%s) %s **%s file%s +%s -%s**", entry.getAuthor(), entry.getAuthorUrl(), dateFormat.format(entry.getDate()), entry.getChangedFile(), entry.getChangedFile() > 1 ? "s" : "", entry.getAdditionLine(), entry.getDeletionLine()));
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return "Commits{" +
                "commitEntries=" + commitEntries +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Commits commits = (Commits) o;
        return Objects.equals(commitEntries, commits.commitEntries);
    }

    @Override
    public int hashCode() {
        return Objects.hash(commitEntries);
    }

    public JsonArray createJson() {
        JsonArray ja = new JsonArray();
        for (CommitEntry commitEntry : commitEntries) {
            ja.add(commitEntry.createJson());
        }
        return ja;
    }
}
