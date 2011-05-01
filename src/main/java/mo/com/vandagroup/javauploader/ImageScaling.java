/**
 * 
 */
package mo.com.vandagroup.javauploader;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.mortennobel.imagescaling.ResampleOp;

/**
 * @author Chris
 *
 */
public class ImageScaling {
	public static void makeThumbnail(File sourceFile, File repository,
			String contentType, int size) throws IOException {
		System.setProperty("java.awt.headless", "true");
		if (!repository.isDirectory()) {
			return;
		}

		String imageOutput = "png";
		int height;
		int width;
		if ("image/png".equals(contentType))
			imageOutput = "png";
		if ("image/jpeg".equals(contentType))
			imageOutput = "jpg";
		if ("image/bmp".equals(contentType))
			imageOutput = "bmp";
		if ("image/gif".equals(contentType))
			imageOutput = "gif";
		
		RenderedImage bfImage = ImageIO.read(sourceFile);
		if (bfImage.getHeight() < bfImage.getWidth()) {
			width = size;
			height = width * bfImage.getHeight() / bfImage.getWidth();
		} else {
			height = size;
			width = height * bfImage.getWidth() / bfImage.getHeight();
		}
		
		ResampleOp resampleOp = new ResampleOp(width, height);
		BufferedImage scaledBI = resampleOp.filter((BufferedImage) bfImage, null);

		ImageIO.write(scaledBI, imageOutput,
				new File(repository, sourceFile.getName()));
	}
	
}
