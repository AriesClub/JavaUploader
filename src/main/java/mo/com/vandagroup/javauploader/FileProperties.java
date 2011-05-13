package mo.com.vandagroup.javauploader;

import java.util.regex.Matcher;

public class FileProperties {
	private String name;
	private long size;
	private String url;
	private String thumbnail;
	
	private FileProperties(Builder build){
		this.name = build.name;
		this.size = build.size;
		this.url = build.url;
		this.thumbnail = build.thumbnail;
	}
	
	FileProperties(String name, long size, String url, String thumbnail){
		this.name = name;
		this.size = size;
		this.url = url;
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
	static class Builder{
		private String name;
		private long size;
		private String url;
		private String thumbnail;
		Builder(String name, long size, String url){
			this.name = name;
			this.size = size;
			this.url = url;
		}
		public Builder thumbnail(String thumbnail){
			this.thumbnail = thumbnail;
			return this;
		}
		public FileProperties build(){
			return new FileProperties(this);
		}
	}
}
