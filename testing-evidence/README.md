# JabRef Testing Project

This repository contains the tests and results from our software testing project in the course DV033G.

## Repository Information

- Project: JabRef
- Repository URL: https://github.com/jewe95/jabref/tree/dv033g-test
- Final commit: ebc60bb5bf033c6a65b10ac1d5b42f1854e625be

## Tests

We focused on two classes:
- RedownloadMissingFilesAction
- AutoSetFileLinksUtil

### RedownloadMissingFilesAction

We added five JUnit tests covering the following scenarios:
- An existing local file should not be downloaded again.
- A missing source URL should prevent redownloading.
- Cancelling the action should stop the process.
- Online links should not be treated as missing local files.
- A missing local file with an available source URL should trigger a download attempt.

All five tests passed.

Test file: [RedownloadMissingFilesActionTest.java](../jabgui/src/test/java/org/jabref/gui/linkedfile/RedownloadMissingFilesActionTest.java)

### AutoSetFileLinksUtil

We added two tests to improve the existing test coverage and mutation testing results:
- No file is automatically linked when two candidates with the same filename
- Only the file with a unique name is automatically linked when duplicate candidates coexist with a unique candidate

Both tests passed.

Test file: [AutoSetFileLinksUtilTest.java](../jabgui/src/test/java/org/jabref/gui/externalfiles/AutoSetFileLinksUtilTest.java)

## Test Results

### RedownloadMissingFilesAction

#### PIT Mutation Testing

- Total mutants: 11
- Killed: 10
- Survived: 1
- Mutation score: 91%

### AutoSetFileLinksUtil

#### PIT Mutation Testing

- Total mutants: 41
- Killed: 34
- Test strength: 85%
- Line coverage: 96%

## How to Run the Tests

1. Clone the repository.
2. Check out the final commit.
3. Make sure Java 21 is installed.
4. Open a terminal in the project root directory.

### JUnit Tests

Run the tests for `RedownloadMissingFilesAction`:
```
.\gradlew.bat :jabgui:test --tests "org.jabref.gui.linkedfile.RedownloadMissingFilesActionTest"
```

Run the tests for `AutoSetFileLinksUtil`:
```
.\gradlew.bat :jabgui:test --tests "org.jabref.gui.externalfiles.AutoSetFileLinksUtilTest"
```

Both test classes were successfully executed using Gradle 9.7.1 and Java 21 on Windows.

### PIT Mutation Testing

Before running the PIT Mutation Testing, change the target class in build.gradle.kts depending on which of the classes is to be tested.
```
.\gradlew :jabgui:pitest
```

## Testing Evidence

### JUnit Tests
- [RedownloadMissingFilesActionTest.java](../jabgui/src/test/java/org/jabref/gui/linkedfile/RedownloadMissingFilesActionTest.java)
- [AutoSetFileLinksUtilTest.java](../jabgui/src/test/java/org/jabref/gui/externalfiles/AutoSetFileLinksUtilTest.java)

### Coverage Results

![Skärmbild 2026-10-26 153250.png](./Coverage%20Results.png)

### Mutation Testing Results

- [autoSetFileLinksUtil index.html](./autoSetFileLinksUtil/pitest/index.html)
- [redownload index.html](./redownload/pitest/index.html)

See the project report for our analysis, limitations and discussion of the results.
