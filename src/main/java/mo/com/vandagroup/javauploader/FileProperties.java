package mo.com.vandagroup.javauploader;

import java.util.regex.Matcher;

public class FileProperties {
	private String name;
	private long size;
	private String url;
	private String thumbnail;
	
	public FileProperties(){};
	
	public FileProperties(String name, long size, String url, String thumbnail){
		this.name = name;
		this.size = size;
		this.url = url;
		this.thumbnail = thumbnail;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public String toString() {
		String item = "{";
		item = item.concat("\"name\":\"" + this.name + "\"");
		item = item.concat(",\"size\":" + this.size );
		item = item.concat(",\"url\":\"" + this.url + "\"");
		item = item.concat(",\"thumbnail\":\"" + this.thumbnail + "\"");
		item = item.concat("}");
		return item.replaceAll("/", Matcher.quoteReplacement("\\/"));

	}
	
}
