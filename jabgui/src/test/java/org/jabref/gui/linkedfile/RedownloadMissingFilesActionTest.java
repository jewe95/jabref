package org.jabref.gui.linkedfile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import org.jabref.gui.DialogService;
import org.jabref.gui.StateManager;
import org.jabref.gui.frame.ExternalApplicationsPreferences;
import org.jabref.logic.FilePreferences;
import org.jabref.logic.util.TaskExecutor;
import org.jabref.model.database.BibDatabaseContext;
import org.jabref.model.entry.BibEntry;
import org.jabref.model.entry.LinkedFile;
import org.jabref.logic.util.OptionalObjectProperty;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.parallel.ResourceLock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verifyNoInteractions;



@ResourceLock("Localization.lang")
class RedownloadMissingFilesActionTest {

    @TempDir
    Path tempDir;

    private final StateManager stateManager = mock(StateManager.class);
    private final DialogService dialogService = mock(DialogService.class);
    private final ExternalApplicationsPreferences externalApplicationsPreferences =
            mock(ExternalApplicationsPreferences.class);
    private final FilePreferences filePreferences = mock(FilePreferences.class);
    private final TaskExecutor taskExecutor = mock(TaskExecutor.class);
    private final BibDatabaseContext databaseContext = mock(BibDatabaseContext.class);

    private BibEntry entry;
    private RedownloadMissingFilesAction action;

    @BeforeEach
    void setUp() {
        entry = new BibEntry();

        when(stateManager.getActiveDatabase())
                .thenReturn(Optional.of(databaseContext));

        when(databaseContext.getEntries())
                .thenReturn(List.of(entry));

        when(databaseContext.getFirstExistingFileDir(filePreferences))
        .thenReturn(Optional.of(tempDir));

        when(dialogService.showConfirmationDialogAndWait(any(), any()))
                .thenReturn(true);

        OptionalObjectProperty<BibDatabaseContext> activeDatabaseProperty =
        OptionalObjectProperty.ofNullable(databaseContext);

        when(stateManager.activeDatabaseProperty())
                .thenReturn(activeDatabaseProperty);

        action = new RedownloadMissingFilesAction(
                stateManager,
                dialogService,
                externalApplicationsPreferences,
                filePreferences,
                taskExecutor
        );
    }

// TODO: Verify that the tests detect incorrect download attempts.
// A valid download directory is now configured, but a download
// may still be stopped before reaching TaskExecutor.

// TODO ENKLARE SVENSKA: Kontrollera att testerna verkligen upptäcker felaktiga nedladdningar.
// Vi har nu lagt till en giltig mapp där filer kan sparas.
// Men en nedladdning kan fortfarande stoppas av andra orsaker
// innan TaskExecutor används. Därför kan testerna bli godkända
// även om programmet försöker ladda ner en fil som det borde hoppa över.

    @Test
    void doesNotRedownloadExistingFile() throws IOException {
        // Arrange: Create a PDF that already exists
        Path existingPdf = tempDir.resolve("article.pdf");
        Files.createFile(existingPdf);

        LinkedFile linkedFile = new LinkedFile(
                "",
                existingPdf,
                "PDF",
                "https://example.com/article.pdf"
        );

        entry.addFile(linkedFile);

        // Act: Attempt to redownload missing files
        action.execute();

        // Assert: The existing file and its link remain unchanged
        assertEquals(List.of(linkedFile), entry.getFiles());
        assertTrue(Files.exists(existingPdf));

        verifyNoInteractions(taskExecutor);
    }

    @Test
    void doesNotRedownloadWhenSourceUrlIsMissing() {
        // Arrange: Create a link to a missing PDF without a source URL
        Path missingPdf = tempDir.resolve("missing.pdf");

        LinkedFile linkedFile = new LinkedFile(
                "",
                missingPdf,
                "PDF",
                ""
        );

        entry.addFile(linkedFile);

        // Act: Attempt to redownload missing files
        action.execute();

        // Assert: The file link remains unchanged and no download is started
        assertEquals(List.of(linkedFile), entry.getFiles());
        assertFalse(Files.exists(missingPdf));

        verifyNoInteractions(taskExecutor);
    }

    @Test
    void doesNotRedownloadWhenUserCancels() {
        // Arrange: A missing PDF with a valid source URL
        Path missingPdf = tempDir.resolve("missing.pdf");

        LinkedFile linkedFile = new LinkedFile(
                "",
                missingPdf,
                "PDF",
                "https://example.com/article.pdf"
        );

        entry.addFile(linkedFile);

        // User declines the confirmation dialog
        when(dialogService.showConfirmationDialogAndWait(any(), any()))
                .thenReturn(false);

        // Act
        action.execute();

        // Assert: No download task should be started
        verifyNoInteractions(taskExecutor);
        verify(databaseContext, never()).getEntries();
        }

        @Test
void doesNotRedownloadOnlineLink() {
    // Arrange: Create a linked file pointing to an online URL
    LinkedFile linkedFile = new LinkedFile(
        "",
        "https://example.com/article.pdf",
        "PDF",
        "https://example.com/article.pdf"
        );

    entry.addFile(linkedFile);

    // Act
    assertTrue(linkedFile.isOnlineLink());
    action.execute();

    // Assert: The online link remains unchanged
    assertEquals(List.of(linkedFile), entry.getFiles());
    verifyNoInteractions(taskExecutor);
}

@Test
void redownloadsMissingFileWithSourceUrl() {
    // Arrange: A missing PDF with a valid source URL
    Path missingPdf = tempDir.resolve("missing.pdf");

    LinkedFile linkedFile = new LinkedFile(
            "",
            missingPdf,
            "PDF",
            "https://example.com/article.pdf"
    );

    entry.addFile(linkedFile);

    // Prevent a real network request
    when(databaseContext.getFirstExistingFileDir(filePreferences))
        .thenReturn(Optional.empty());
        
        // Act
        action.execute();

    // Assert: JabRef reaches the download directory lookup
    verify(databaseContext).getFirstExistingFileDir(filePreferences);
}       

}