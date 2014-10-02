package main;

public class Song {

	private String artist;
	private String Name;
	private int num = 0;
	private String path;
	
	public Song(String name, String artist, String path) {
		this.Name = name;
		this.artist = artist;
		this.path = path;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}
	
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	@Override
	public String toString() {
		return this.getName() + " - " + this.getArtist();
	}
}
