package dev.felnull.fnchangelogger.changelog;

import com.google.gson.JsonArray;
import org.kohsuke.github.GHCommit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class MajorChange {
    private final List<String> changes;

    public MajorChange(List<String> changes) {
        this.changes = changes;
    }

    public List<String> getChanges() {
        return changes;
    }

    public static MajorChange generateMajorChange(List<GHCommit.ShortInfo> commitInfos, List<String> majorChanges) {
        List<String> changes = new ArrayList<>(majorChanges);
        for (GHCommit.ShortInfo commitInfo : commitInfos) {
            String msg = commitInfo.getMessage();
            if (msg != null) {
                String[] spr = msg.split("\n");
                if (spr.length >= 3 && !spr[0].isEmpty() && spr[1].isEmpty()) {
                    for (int i = 2; i < spr.length; i++) {
                        if (spr[i].startsWith("- ")) changes.add(spr[i].substring("- ".length()));
                    }
                }
            }
        }
        return new MajorChange(Collections.unmodifiableList(changes));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MajorChange that = (MajorChange) o;
        return Objects.equals(changes, that.changes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(changes);
    }

    @Override
    public String toString() {
        return "MajorChange{" + "changes=" + changes + '}';
    }

    public String createMarkdown(String preVersion) {
        StringBuilder sb = new StringBuilder();
        sb.append("### ");

        if (preVersion == null) {
            sb.append("First release");
        } else {
            sb.append(String.format("Major changes from %s", preVersion)).append("\n");
            if (changes.isEmpty())
                sb.append("No major changes");
            else
                sb.append(String.format("%s major changes", changes.size())).append("\n");
        }

        if (!changes.isEmpty()) {
            for (String change : changes) {
                sb.append("\n");
                sb.append("- ").append(change);
            }
        }

        return sb.toString();
    }

    public JsonArray createJson() {
        JsonArray ja = new JsonArray();
        for (String change : changes) {
            ja.add(change);
        }
        return ja;
    }
}
