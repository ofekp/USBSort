package main;

public interface Playlist {

	public boolean createPlaylist();
	public int getSongCount();
	public int getNumOfProcessedSongs(); // Processed songs count
	public int getProgress(); // Percentage
}
