package main;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ItunesXmlParser implements Parser {
	
	private Document doc;
	private ItunesPlaylist playlist;
	
	private File xmlFile;
	private NodeList nodeList;

	public ItunesXmlParser(String xmlPath, ItunesPlaylist playlist) {
		this.playlist = playlist;
		
		try {
			xmlFile = new File(xmlPath);
			DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			doc = dBuilder.parse(xmlFile);
			//doc.normalize();
			
			if (doc.hasChildNodes()) {
				nodeList = doc.getChildNodes();
			}
			
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}		
	}

	public void parseSongs() {
		System.out.println("> Parsing file: " + xmlFile.getName());
		parseSongs(nodeList);
	}
	
	private void parseSongs(NodeList nodeList) {
		NodeList tmpNodeList, mainList, songsList, playList, palyListSongEntry;
		Node songEntry;
		Node tempNode = nodeList.item(0);
		int i;
		
		for (i = 0; i < nodeList.getLength(); i++) {
			tempNode = nodeList.item(i);
			if (tempNode.getNodeType() == Node.ELEMENT_NODE && tempNode.getNodeName() == "plist") {
				break;
			}
		}
		// TODO: assert
		
		tmpNodeList = tempNode.getChildNodes();
		for (i = 0; i < tmpNodeList.getLength(); i++) {
			tempNode = tmpNodeList.item(i);
			if (tempNode.getNodeType() == Node.ELEMENT_NODE && tempNode.getNodeName() == "dict") {
				break;
			}
		}
		// TODO: assert
		
		// search for the second <dict> tag
		tmpNodeList = tempNode.getChildNodes();
		mainList = tmpNodeList;
		for (i = 0; i < tmpNodeList.getLength(); i++) {
			tempNode = tmpNodeList.item(i);
			if (tempNode.getNodeType() == Node.ELEMENT_NODE && tempNode.getNodeName() == "dict") {
				break;
			}
		}
		// TODO: assert
		
		// Get the song description list
		songsList = tempNode.getChildNodes();
			
		// Main loop over all songs
		int currKey_d = 0;
		String currKey;
		String currArtist;
		String currName, currPath;
		NodeList songAttributesList;
		// Loop over all songs seperated by <dict> tags
		for (i = 0; i < songsList.getLength(); i++) {
			tempNode = songsList.item(i);
			if (tempNode.getNodeType() == Node.TEXT_NODE)
				continue;
			if (tempNode.getNodeName() == "key") {
				currKey = tempNode.getTextContent();
				while (i <= songsList.getLength() && tempNode.getNodeName() != "dict") {
					tempNode = songsList.item(++i);
				}
				// <dict> tag found, currKey should be an integer
				try {
					currKey_d = Integer.parseInt(currKey);
				} catch (Exception e) {
					// end of song list
					break;
				}
				songAttributesList = tempNode.getChildNodes();
				// Get song name and artist
				currName = getSongAttributeValue(songAttributesList, "Name");
				currArtist = getSongAttributeValue(songAttributesList, "Artist");
				currPath = getSongAttributeValue(songAttributesList, "Location");
				currPath = currPath.replace("file://localhost/", "");
				currPath = currPath.replace("%20", " ");
				// Put the song in the table
				playlist.addSongDesc(new ItunesSong(currKey_d, currName, currArtist, currPath));
			}
		}

		// Get the playlist
		for (i = 0; i < mainList.getLength(); i++) {
			tempNode = mainList.item(i);
			if (tempNode.getNodeName() == "key" && tempNode.getTextContent().replaceAll("\\t", "").replaceAll("\\n", "").trim().equals("Playlists")) {
				break;
			}
		}
		// TODO: assert
		
		for (; i < mainList.getLength(); i++) {
			tempNode = mainList.item(i);
			if (tempNode.getNodeName().equals("array")) {
				break;
			}
		}
		// TODO: assert
		
		playList = tempNode.getChildNodes();
		
		for (i = 0; i < playList.getLength(); i++) {
			tempNode = playList.item(i);
			if (tempNode.getNodeName().equals("dict")) {
				break;
			}
		}
		// TODO: assert
		
		playList = tempNode.getChildNodes();
		
		for (i = 0; i < playList.getLength(); i++) {
			tempNode = playList.item(i);
			if (tempNode.getNodeName().equals("array")) {
				break;
			}
		}
		// TODO: assert
		
		playList = tempNode.getChildNodes();
		
		int tmp_key;
		for (i = 0; i < playList.getLength(); i++) {
			// Skip text nodes
			tempNode = playList.item(i);
			if (tempNode.getNodeType() == Node.TEXT_NODE)
				continue;
			
			if (tempNode.getNodeName().equals("dict")) {
				palyListSongEntry = tempNode.getChildNodes();
				for (int j = 0; j < palyListSongEntry.getLength(); j++) {
					songEntry = palyListSongEntry.item(j);
					if (songEntry.getNodeType() == Node.TEXT_NODE)
						continue;	
					if (songEntry.getNodeName().equals("integer")) {
						try {
							tmp_key = Integer.parseInt(songEntry.getTextContent());
						} catch(Exception e) {
							break;
						}
						playlist.addSongToList(tmp_key);
					}
				}
			}			
		}
	}
	
	private static String getSongAttributeValue(NodeList attributesList, String attributeName) {
		int listLen = attributesList.getLength();
		int attrIndex = 0;
		Node tmpNode = attributesList.item(attrIndex);
		while (attrIndex < listLen && (tmpNode.getNodeName() != "key" || !tmpNode.getTextContent().equals(attributeName))) {
			tmpNode = attributesList.item(++attrIndex);
		}
		if (attrIndex >= listLen) {
			// Attribute not found
			return "";
		}
		if (tmpNode.getNodeName() != "key" || !tmpNode.getTextContent().equals(attributeName))
			return null;
		tmpNode = attributesList.item(attrIndex + 1);
		if (tmpNode == null)
			return null;
		return tmpNode.getTextContent();
	}
	
}
