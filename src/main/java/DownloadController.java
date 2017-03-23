import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
public class DownloadController {

	private final DownloadService downloadService;

	@Autowired
	public DownloadController(DownloadService downloadService) {
		this.downloadService = downloadService;
	}

	@ResponseStatus(HttpStatus.OK)
	@GetMapping("/download")
	public void download() {
		downloadService.download();
	}
}
