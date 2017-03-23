import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

@Service
public class DownloadService {

	private static final Logger logger = Logger.getLogger(DownloadService.class);

	private final Executor executor;

	@Autowired
	public DownloadService() {
		logger.info("");
		executor = Executors.newFixedThreadPool(3);
	}

	@PostConstruct
	public void init() {
		logger.info("ready");
	}

	public void download() {
		Runnable task = new FileDownloadTask("http://speedtest.tele2.net/100MB.zip", this::onDownloaded);
		executor.execute(task);
	}

	private void onDownloaded(String localFilename) {
		logger.info("File " + localFilename + " downloaded");
	}

	private static class FileDownloadTask implements Runnable {

		private final String fileURI;
		private final FinishedDownloadCallback callback;

		private FileDownloadTask(String fileURI, FinishedDownloadCallback callback) {
			this.fileURI = fileURI;
			this.callback = callback;
		}

		@Override
		public void run() {
			final URI u = URI.create(fileURI);
			final Path path = getAvailablePath("100MB", "zip");
			logger.info("Downloading to file " + path.toString());
			try (InputStream in = u.toURL().openStream()) {
				Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				logger.error(e.getMessage());
			}
			callback.accept(path.toString());
		}

		private Path getAvailablePath(String filename, String extension) {
			File file = new File(filename + "." + extension);
			int n = 0;
			while (file.exists()) {
				file = new File(filename + n + "." + extension);
				n++;
			}
			return file.toPath();
		}
	}

	@FunctionalInterface
	private interface FinishedDownloadCallback extends Consumer<String> {
	}
}
