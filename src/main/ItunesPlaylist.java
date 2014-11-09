package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

public class ItunesPlaylist implements Playlist {
	
	private static Hashtable<Integer, Song> songsHashTable = new Hashtable<Integer, Song>();
	private static List<Integer> keyList = new LinkedList<Integer>();
	
	private String name;
	private int songCount;
	private int numOfDigitsInSongNum;
	private String destPath;
	private boolean isPhysicalOrder;
	private int numOfProcessedSongs;
	
	public ItunesPlaylist(String name, String destPath, boolean isPhysicalOrder) {
		this.name = name;
		this.songCount = 0;
		this.numOfDigitsInSongNum = 0;
		this.destPath = destPath;
		this.isPhysicalOrder = isPhysicalOrder;
		this.numOfProcessedSongs = 0;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSongCount() {
		return songCount;
	}
	
	public void addSongDesc(ItunesSong song) {
		songsHashTable.put(song.getKey(), song);
		songCount++;
	}
	
	public void addSongToList(int songKey) {
		keyList.add(songKey);
	}

	@Override
	public int getNumOfProcessedSongs() {
		return numOfProcessedSongs;
	}

	@Override
	public int getProgress() {
		return (int) ((numOfProcessedSongs * 100) / this.getSongCount());
	}	
	
	public boolean createPlaylist() {
		
		if (songCount == 0)
			return true;
		
		numOfDigitsInSongNum = String.valueOf(songCount).length();
		
		if (isPhysicalOrder) {
			// Delete all files from the directory
			File dir = new File(destPath + name + "\\");
			File[] fileList = dir.listFiles();
			for (File file : fileList) {
				// DOTO: make sure deleting only music files
				file.delete();
			}
		}
		
		// Create the folder
		try {
			File tmpFile = new File(destPath + name + "\\");
			tmpFile.mkdir();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		// Change all song numbers to 0
		try {
			markAllSongs();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		// Check all files are available
		int songNum = 0;
		File songFile;
		Song song;
		boolean allFilesValid = true;
		Main.addUserInfoLine("Checking all song files...");
		try {
			for (Integer songKey : keyList) {
				songNum++;
				// Check if already exists, if so only rename this song with a
				// different song number
				song = songsHashTable.get(songKey);
				songFile = findSong(song.getName() + " - " + song.getArtist() + ".mp3");
				if (songFile == null) {
					allFilesValid &= validateSong(song);
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		if (!allFilesValid) {
			System.out.println("Aborting. Please fix all files first.");
			Main.addUserInfoLine("Please fix the above error.");
			return false;
		} else {
			Main.addUserInfoLine("Good to go!");
			Main.addUserInfoLine("Creating your playlist...");
		}
		
		// Actual copy of files to the folder
		numOfProcessedSongs = 0;
		songNum = 0;
		try {
			for (Integer songKey : keyList) {
				numOfProcessedSongs++;
				songNum++;
				// Check if already exists, if so only rename this song with a
				// different song number
				song = songsHashTable.get(songKey);
				songFile = findSong(song.getName() + " - " + song.getArtist() + ".mp3");
				if (songFile != null) {
					renameSong(songFile, songNum);
				} else {
					copySong(song, songNum);
				}
				
			}
			removeRestOfSongs();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return false;
		}
		
		Main.addUserInfoLine("Done.");
		return true;
	}

	private void copySong(Song song, int songNum) throws IOException {
		
		File srcSong = null;
		File dstSong = null;
		
		try {
			srcSong = new File(song.getPath());
		} catch (Exception e) {
			System.out.println("Could not locate the file " + song.getPath());
		}
		
		try {
			dstSong = new File(destPath + name + "\\(" + String.format("%0" + numOfDigitsInSongNum + "d", songNum) + ") " + song.getName() + " - " + song.getArtist() + ".mp3");
		    if(!dstSong.exists()) {
		    	dstSong.createNewFile();
		    }
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println("Could not locate the file + " + dstSong.getPath());
			throw new IOException();
		}
		
		if(!srcSong.canRead() || !dstSong.canWrite()) {
			System.out.println("No permissions");
			throw new IOException();
		}
		
		FileChannel src = null;
		FileChannel dst = null;
		
		try {
			src = new FileInputStream(srcSong).getChannel();
			dst = new FileOutputStream(dstSong).getChannel();
			//src.transferTo(0, src.size(), dst);
			dst.transferFrom(src, 0, src.size());
			System.out.println();
		} catch(Exception e) {
			System.out.println(e.getMessage());
			throw new IOException();
		} finally {
			if (src != null) {
				src.close();
			}
			if (dst != null) {
				dst.close();
			}
		}
	}
	
	private File findSong(final String songName) throws IOException {
		File destDir = new File(destPath + name + "\\");
		File[] matchingFiles = destDir.listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String fileName) {
				return fileName.endsWith(songName);
			}
		}
		);
		if (matchingFiles.length == 0)
			return null;
		else if (matchingFiles.length == 1)
			return matchingFiles[0];
		else
			throw new IOException("More than one song with the same name exist in the folder.");
	}
	
	private void renameSong(File songFile, int newSongNum) throws IOException {
		File newSongFile = new File(destPath + name + "\\(" + String.format("%0" + numOfDigitsInSongNum + "d", newSongNum) + ") " + songFile.getName().substring(songFile.getName().indexOf(") ") + 2));
		if (!newSongFile.exists())
			songFile.renameTo(newSongFile);
	}
	
	private void removeRestOfSongs() {
		File dir = new File(destPath + name + "\\");
		File[] fileList = dir.listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String fileName) {
				int num = 0;
				try {
					num = Integer.parseInt(fileName.substring(1, numOfDigitsInSongNum + 1));
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
				return num == 0;
			}
		});
		for (File file : fileList) {
			file.delete();
		}
	}
	
	private void markAllSongs() throws IOException {
		File dir = new File(destPath + name + "\\");
		File[] fileList = dir.listFiles();
		for (File file : fileList) {
			renameSong(file, 0);
		}
	}
	
	private boolean validateSong(Song song) {
		File srcSong = null;
		
		try {
			srcSong = new File(song.getPath());
		} catch (Exception e) {
			System.out.println("Could not open the file " + song.getPath());
			Main.addUserInfoLine("  Could not open the file " + song.getPath());
			return false;
		}
		
		if(!srcSong.canRead()) {
			System.out.println("No permissions for file " + song.getPath());
			Main.addUserInfoLine("  No permissions for file " + song.getPath());
			return false;
		}
		
		return true;
	}
}
