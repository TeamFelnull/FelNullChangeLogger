package dev.felnull.fnchangelogger;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;

public class FelNullChangeLoggerPlugin implements Plugin<Project> {
    private static final String GROUP_NAME = "change logger";

    @Override
    public void apply(Project project) {
        project.getLogger().debug("FelNull ChangeLogger: " + FelNullChangeLoggerPlugin.class.getPackage().getImplementationVersion());

        FNChangeLoggerExtension extension = project.getExtensions().create("changelog", FNChangeLoggerExtension.class);

        Task task = project.task("generateChangelog");
        task.setGroup(GROUP_NAME);
        task.doLast(tsk -> {
            if (extension.isDoSkip()) {
                project.getLogger().debug("skipped");
                return;
            }
            try {
                ChangelogGenerator.getInstance().start(project, extension.getTriggerType(), extension.getTrigger(), extension.getGithubToken(), extension.getRepository(), extension.getReleaseType(), extension.getMajorChanges());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
