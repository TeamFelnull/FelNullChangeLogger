package dev.felnull.fnchangelogger.changelog;

import com.google.gson.JsonObject;
import org.kohsuke.github.GHCommit;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class CommitEntry {
    private static final String userCommitURL = "https://github.com/%s/commits?author=%s";
    private final int changedFile;
    private final int additionLine;
    private final int deletionLine;
    private final String name;
    private final String url;
    private final String author;
    private final String authorUrl;
    private final long date;

    public CommitEntry(int changedFile, int additionLine, int deletionLine, String name, String url, String author, String authorUrl, long date) {
        this.changedFile = changedFile;
        this.additionLine = additionLine;
        this.deletionLine = deletionLine;
        this.name = name;
        this.url = url;
        this.author = author;
        this.authorUrl = authorUrl;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public int getAdditionLine() {
        return additionLine;
    }

    public int getChangedFile() {
        return changedFile;
    }

    public int getDeletionLine() {
        return deletionLine;
    }

    public long getDate() {
        return date;
    }

    public String getAuthor() {
        return author;
    }

    public String getAuthorUrl() {
        return authorUrl;
    }

    public String getUrl() {
        return url;
    }

    public static CommitEntry createCommitEntry(String repository, GHCommit commit, GHCommit.ShortInfo info) throws IOException {
        String msg = info.getMessage();
        String[] spr = msg.split("\n");
        if (spr.length >= 3 && !spr[0].isEmpty() && spr[1].isEmpty()) {
            msg = spr[0];
        }

        URL uu = commit.getAuthor().getHtmlUrl();
        String userURL = uu != null ? uu.toString() : null;
        if (userURL != null) {
            try {
                String[] str = uu.toString().split("/");
                userURL = String.format(userCommitURL, repository, str[str.length - 1]);
            } catch (Exception ignored) {
            }
        }
        return new CommitEntry(commit.getFiles().size(), commit.getLinesAdded(), commit.getLinesDeleted(), msg, commit.getHtmlUrl().toString(), info.getAuthor().getName(), userURL, info.getCommitDate().getTime());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommitEntry that = (CommitEntry) o;
        return changedFile == that.changedFile && additionLine == that.additionLine && deletionLine == that.deletionLine && date == that.date && Objects.equals(name, that.name) && Objects.equals(url, that.url) && Objects.equals(author, that.author) && Objects.equals(authorUrl, that.authorUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(changedFile, additionLine, deletionLine, name, url, author, authorUrl, date);
    }

    @Override
    public String toString() {
        return "CommitEntry{" +
                "changedFile=" + changedFile +
                ", additionLine=" + additionLine +
                ", deletionLine=" + deletionLine +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", author='" + author + '\'' +
                ", authorUrl='" + authorUrl + '\'' +
                ", date=" + date +
                '}';
    }

    public JsonObject createJson() {
        JsonObject jo = new JsonObject();
        jo.addProperty("changed_file", changedFile);
        jo.addProperty("addition_line", additionLine);
        jo.addProperty("deletion_line", deletionLine);
        jo.addProperty("name", name);
        jo.addProperty("url", url);
        jo.addProperty("author", author);
        jo.addProperty("author_url", authorUrl);
        jo.addProperty("date", date);
        return jo;
    }
}
