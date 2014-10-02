package main;

public class ItunesSong extends Song {
	
	private int key;

	public ItunesSong(int key, String name, String artist, String path) {
		super(name, artist, path);
		this.key = key;
	}

	public int getKey() {
		return key;
	}
}
