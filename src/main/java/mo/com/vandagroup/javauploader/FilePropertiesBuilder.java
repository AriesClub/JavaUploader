/**
 * 
 */
package mo.com.vandagroup.javauploader;

/**
 * @author Chris
 *
 */
public class FilePropertiesBuilder implements Builder<FileProperties> {
	
	private FileProperties fp;
	
	FilePropertiesBuilder(String name, long size, String url){
		this.fp = new FileProperties(name, size, url);
	}
	
	public FilePropertiesBuilder thumbnail(String thumbnail){
		this.fp.thumbnail = thumbnail;
		return this;
	}
	@Override
	public FileProperties build() {
		return this.fp;
	}

}
