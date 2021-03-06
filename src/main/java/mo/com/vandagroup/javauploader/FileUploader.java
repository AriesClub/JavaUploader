package mo.com.vandagroup.javauploader;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gson.Gson;

/**
 * Servlet implementation class FileUploader
 */
public class FileUploader extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Log LOG = LogFactory.getLog(FileUploader.class);
	private final int SIZE_THRESHOLD = DiskFileItemFactory.DEFAULT_SIZE_THRESHOLD;
	private final File TEMP_DIR = new File(System.getProperty("java.io.tmpdir"));
	private final int MAX_THUMBNAILS_SIZE = 50;
	private File uploadDir;
	private String uploadUrl;
	private File thumbnailsDir;
	private String thumbnailsUrl;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public FileUploader() {
		super();
	}

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		if (!this.TEMP_DIR.isDirectory())
			throw new ServletException(this.TEMP_DIR.getPath()
					+ " is not a directory");

		this.uploadDir = new File(this.getServletContext().getRealPath(
				this.getInitParameter("upload_dir")));
		if (!this.uploadDir.isDirectory())
			throw new ServletException(this.uploadDir.getAbsolutePath()
					+ " is not a directory");
		this.uploadUrl = this.getInitParameter("upload_url");

		this.thumbnailsDir = new File(this.getServletContext().getRealPath(
				this.getInitParameter("thumbnails_dir")));
		if (!this.thumbnailsDir.isDirectory())
			throw new ServletException(this.thumbnailsDir.getAbsolutePath()
					+ " is not a directory");
		this.thumbnailsUrl = this.getInitParameter("thumbnails_url");

		LOG.info("TEMP_DIR:\t\t" + this.TEMP_DIR.getAbsolutePath());
		LOG.info("UPLOAD_DIR:\t" + this.uploadDir.getAbsolutePath());
		LOG.info("THUMBNAIL_DIR:\t" + this.thumbnailsDir.getAbsolutePath());
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setHeader("Cache-Control", "no-cache, must-revalidate");
		String content = request.getHeader("x-requested-with");
		if (content != null && content.equals("XMLHttpRequest"))
			response.setHeader("Content-type", "application/json");
		else
			response.setHeader("Content-type", "text/html");
		/**
		 * When file parameter is not set, return init json that content all
		 * expected files
		 * 
		 * When file parameter is set. return that file properties if exist,
		 * null if not exist
		 * 
		 */
		List<FileProperties> fps = new ArrayList<FileProperties>();
		if (request.getParameter("file") == null) {

			// example-1
			FileFilter select = new FileFilter() {

				@Override
				public boolean accept(File pathname) {
					// Accept jpg file
					// return pathname.getName().endsWith("jpg");

					// Accept all file
					return true;
				}

			};
			for (File f : this.uploadDir.listFiles(select)) {
				fps.add(new FilePropertiesBuilder(f.getName(), f.length(), this.uploadUrl+f.getName())
						.thumbnail(this.thumbnailsUrl+"attach_image.png")
						.build());
			}
			response.getWriter().printf(new Gson().toJson(fps));
			// end example-1
		} else {
			File findFile = new File(this.uploadDir,
					request.getParameter("file"));

			if (findFile.isFile()) {
				// file parameter is existing, return that file's properties
				fps.add(new FilePropertiesBuilder(findFile.getName(), findFile.length(), this.uploadUrl+findFile.getName())
						.thumbnail(this.thumbnailsUrl+findFile.getName())
						.build());
				response.getWriter().printf(fps.toString());

			} else {
				// file parameter is new file
				response.getWriter().printf("null");
			}
		}
		response.getWriter().close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setHeader("Content-type", "application/json");

		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		String fileName = null;
		String contentType = null;
		File uploadFile = null;
		FileProperties pf = null;
		
		if (!isMultipart) {
			fileName = request.getHeader("X-File-Name");
			long fileSize = Long.valueOf(request.getHeader("X-File-Size"));
			contentType = request.getHeader("X-File-Type");

			uploadFile = new File(this.uploadDir, fileName);
			OutputStream os = new FileOutputStream(uploadFile);
			int br;
			byte bytes[] = new byte[1024];
			while ((br = request.getInputStream().read(bytes)) > 0) {
				os.write(bytes, 0, br);
			}
			os.close();
			request.getInputStream().close();
			pf = new FilePropertiesBuilder(fileName, fileSize, uploadUrl + fileName).thumbnail(
					thumbnailsUrl + fileName).build();

		} else {

			// Create a factory for disk-bask file items
			FileItemFactory factory = new DiskFileItemFactory(
					this.SIZE_THRESHOLD, this.TEMP_DIR);

			// Create a new file upload handler
			ServletFileUpload upload = new ServletFileUpload(factory);

			try {
				List<?> items = upload.parseRequest(request);
				Iterator<?> iter = items.iterator();
				while (iter.hasNext()) {
					FileItem item = (FileItem) iter.next();

					if (item.isFormField()) {
						// Process a regular form field
					} else {
						// Process a file upload
						fileName = item.getName();
						contentType = item.getContentType();
						if (fileName != null && !"".equals(fileName)) {
							fileName = FilenameUtils.getName(fileName);
							uploadFile = new File(this.uploadDir, fileName);

							item.write(uploadFile);
						}
						pf = new FilePropertiesBuilder(fileName, item.getSize(),
								this.uploadUrl + fileName).thumbnail(this.thumbnailsUrl
										+ fileName).build();

					}
					// Only 1 file
					break;
				}
			} catch (FileUploadException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		response.getWriter().print(new Gson().toJson(pf));
		response.getWriter().close();
		
		if (contentType.equals("image/png") || contentType.equals("image/jpeg")
				|| contentType.equals("image/gif")) {
			// Generate thumbnails image
			ImageScaling.makeThumbnail(uploadFile, this.thumbnailsDir,
					contentType, this.MAX_THUMBNAILS_SIZE);
		} else {
			FileUtils.copyFile(
					new File(this.thumbnailsDir, "attach_image.png"), new File(
							this.thumbnailsDir, fileName));
		}
	}

	/**
	 * @see HttpServlet#doDelete(HttpServletRequest, HttpServletResponse)
	 */
	protected void doDelete(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setHeader("Content-type", "application/json");
		File deleteFile = new File(this.uploadDir, request.getParameter("file"));
		File thumbnailFile = new File(this.thumbnailsDir,
				request.getParameter("file"));
		if (deleteFile.isFile()) {
			deleteFile.delete();
		}
		if (thumbnailFile.isFile()) {
			thumbnailFile.delete();
		}
		response.getWriter().printf("true").close();
	}
}
