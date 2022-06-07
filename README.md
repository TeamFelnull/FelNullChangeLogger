# FelNullChangeLogger

The ikisugi change logger.  
Automatically generate change logs from github commit logs.

## Required code

Write the following code in build.gradle.

```groovy
changelog {
    //Requirement
    trigger = "v1.3" //The name (version) of the tag that triggered it
    githubToken = "ikisugithubtoken" //Github token
    repository = "TeamFelnull/IamMusicPlayer" //Target repository
    releaseType = "release" //Release type
    //Options
    skip() //Does not perform task even if log generation is executed
    triggerType = "tag" //It's a meaningless code for now
}
```

## How to Use

```
gradle generateChangelog
```

After execution, a change log will be generated in the changelog folder of the build directory.