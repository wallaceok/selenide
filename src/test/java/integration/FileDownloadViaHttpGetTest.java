package integration;

import com.codeborne.selenide.Configuration;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static com.codeborne.selenide.Configuration.FileDownloadMode.HTTPGET;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.close;
import static com.codeborne.selenide.WebDriverRunner.isPhantomjs;
import static org.apache.commons.io.FileUtils.readFileToString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeFalse;

public class FileDownloadViaHttpGetTest extends IntegrationTest {
  File folder = new File(Configuration.reportsFolder);

  @Before
  public void setUp() {
    close();
    Configuration.fileDownload = HTTPGET;
    openFile("page_with_uploads.html");
  }

  @Test
  public void downloadsFiles() throws IOException {
    assumeFalse(isPhantomjs()); // Why it's not working? It's magic for me...
    
    File downloadedFile = $(byText("Download me")).download();

    assertEquals("hello_world.txt", downloadedFile.getName());
    assertEquals("Hello, WinRar!", readFileToString(downloadedFile, "UTF-8"));
    assertTrue(downloadedFile.getAbsolutePath().startsWith(folder.getAbsolutePath()));
  }

  @Test
  public void downloadsFileWithCyrillicName() throws IOException {
    assumeFalse(isPhantomjs()); // Why it's not working? It's magic for me...

    File downloadedFile = $(byText("Download file with cyrillic name")).download();

    assertEquals("файл-с-русским-названием.txt", downloadedFile.getName());
    assertEquals("Превед медвед!", readFileToString(downloadedFile, "UTF-8"));
    assertTrue(downloadedFile.getAbsolutePath().startsWith(folder.getAbsolutePath()));
  }

  @Test(expected = FileNotFoundException.class)
  public void downloadMissingFile() throws IOException {
    $(byText("Download missing file")).download();
  }
}
