package main;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class Main {

	private static Display display;
	private static Shell shell;
	private static Button executeButton;
	
	private static boolean isPhysicalOrder;
	private static String xmlFilePath = null;
	private static String playlistDestPath = null;
	private static String playlistName = null;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		startGui();
		//ItunesPlaylist pl = new ItunesPlaylist("TryDisc1", "D:\\", false);
		//Parser parser = new ItunesXmlParser("C:\\Users\\user\\Desktop\\TryDisc5.xml", pl);
		//parser.parseSongs();
		//pl.createPlaylist();
	}
	
	private static void startGui() {
		display = new Display();
		shell = new Shell(display);
		
		shell.setSize(320, 180);
		shell.setText("USB Playlist Maker");
		
		GridLayout layout = new GridLayout(3, false);
		shell.setLayout(layout);
		
		// Playlist name
		final Label playlistNameLabel = new Label(shell, SWT.NONE);
		playlistNameLabel.setText("Playlist name:");
		final Text playlistNameTextbox = new Text(shell, SWT.BORDER);
		GridData tmp_gd = new GridData(SWT.FILL, SWT.LEFT, true, false);
		tmp_gd.horizontalSpan = 2;
		playlistNameTextbox.setLayoutData(tmp_gd);
		playlistNameTextbox.setTextLimit(80);
		playlistNameTextbox.setToolTipText("Choose the name of the playlist that will be created.");
		playlistNameTextbox.addKeyListener(new KeyListener() {
			
			@Override
			public void keyReleased(KeyEvent arg0) {
				// TODO Auto-generated method stub
				playlistName = playlistNameTextbox.getText();
				validateExecute();
			}
			
			@Override
			public void keyPressed(KeyEvent arg0) {
				// TODO Auto-generated method stub

			}
		});
		
		// XML
		final Text xmlFilePathTextbox = new Text(shell, SWT.BORDER);
		xmlFilePathTextbox.setLayoutData(new GridData(SWT.FILL, SWT.LEFT, true, false));
		xmlFilePathTextbox.addKeyListener(new KeyListener() {
			
			@Override
			public void keyReleased(KeyEvent arg0) {
				// TODO Auto-generated method stub
				xmlFilePath = xmlFilePathTextbox.getText();
				validateExecute();
			}
			
			@Override
			public void keyPressed(KeyEvent arg0) {
				// TODO Auto-generated method stub

			}
		});
		tmp_gd = new GridData(SWT.FILL, SWT.FILL, true, false);
		tmp_gd.verticalSpan = 1;
		tmp_gd.horizontalSpan = 2;
		tmp_gd.verticalAlignment = SWT.CENTER;
		xmlFilePathTextbox.setLayoutData(tmp_gd);
		xmlFilePathTextbox.setToolTipText("Choose an iTunes XML file that represent the playlist.");
		final Button xmlButton = new Button(shell, SWT.PUSH);
		xmlButton.setText("Playlist XML");
		tmp_gd = new GridData(SWT.FILL, SWT.FILL, false, false);
		xmlButton.setLayoutData(tmp_gd);
		xmlButton.addSelectionListener(new SelectionAdapter() {
		    @Override
		    public void widgetSelected(SelectionEvent e) {
		        // Handle the selection event
		        FileDialog open_fd = new FileDialog(shell, SWT.OPEN);
		        open_fd.setText("Select XML file");
		        open_fd.setFilterPath("C:/");
		        String[] extFilter = {"*.xml"};
		        open_fd.setFilterExtensions(extFilter);
		        String selected = open_fd.open();
		        if (selected != null) {
		        	xmlFilePathTextbox.setText(selected);
		        	xmlFilePath = selected;
		        }
		        validateExecute();
		    }
		});
		
		// Playlist folder
		final Text folderPathTextBox = new Text(shell, SWT.BORDER);
		folderPathTextBox.setLayoutData(new GridData(SWT.FILL, SWT.LEFT, true, false));
		folderPathTextBox.addKeyListener(new KeyListener() {
			@Override
			public void keyReleased(KeyEvent arg0) {
				// TODO Auto-generated method stub
				playlistDestPath = folderPathTextBox.getText();
				validateExecute();
			}
			
			@Override
			public void keyPressed(KeyEvent arg0) {
				// TODO Auto-generated method stub

			}
		});
		tmp_gd = new GridData(SWT.FILL, SWT.FILL, true, false);
		tmp_gd.verticalSpan = 1;
		tmp_gd.horizontalSpan = 2;
		tmp_gd.verticalAlignment = SWT.CENTER;
		xmlFilePathTextbox.setLayoutData(tmp_gd);
		folderPathTextBox.setLayoutData(tmp_gd);
		folderPathTextBox.setToolTipText("Choose the folder in which the playlist would be created.");
		final Button folderButton = new Button(shell, SWT.PUSH);
		folderButton.setText("Playlist Folder");
		folderButton.addSelectionListener(new SelectionAdapter() {
		    @Override
		    public void widgetSelected(SelectionEvent e) {
		        // Handle the selection event
		        DirectoryDialog open_fd = new DirectoryDialog(shell, SWT.OPEN);
		        open_fd.setText("Select XML file");
		        open_fd.setFilterPath("C:/");
		        String selected = open_fd.open();
		        if (selected != null) {
		        	folderPathTextBox.setText(selected);
		        	playlistDestPath = selected;
		        }
		        validateExecute();
		    }
		});
		
		final Button isPhysOrderButton = new Button(shell, SWT.CHECK);
		isPhysOrderButton.setText("Physical Order");
		tmp_gd = new GridData(SWT.FILL, SWT.LEFT, true, false);
		tmp_gd.horizontalSpan = 2;
		isPhysOrderButton.setLayoutData(tmp_gd);
		isPhysOrderButton.setToolTipText("This will cause the playlist to be deleted before it is rewritten.");
		isPhysOrderButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				isPhysicalOrder = isPhysOrderButton.getSelection();
	        }
		});
		
		new Label(shell, SWT.SINGLE);
		executeButton = new Button(shell, SWT.PUSH);
		executeButton.setEnabled(false);
		executeButton.setText("Create Playlist!");
		tmp_gd = new GridData(SWT.FILL, SWT.FILL, false, false);
		tmp_gd.verticalSpan = 1;
		tmp_gd.horizontalSpan = 3;
		tmp_gd.verticalAlignment = SWT.CENTER;
		executeButton.setLayoutData(tmp_gd);
		executeButton.addSelectionListener(new SelectionAdapter() {
		    @Override
		    public void widgetSelected(SelectionEvent e) {
		        // Handle the selection event
		        System.out.println("Executing: " + playlistName + ", " + playlistDestPath + ", " + isPhysicalOrder + ", " + xmlFilePath);
				final ItunesPlaylist pl = new ItunesPlaylist(playlistName, playlistDestPath, isPhysicalOrder);
				final Thread progressThread = new Thread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						while (!Thread.currentThread().isInterrupted()) {
							try {
								Thread.sleep(500);
								System.out.print(".");
							} catch (InterruptedException ie) {
								// TODO: handle exception
							}
							Display.getDefault().asyncExec(new Runnable() {

								@Override
								public void run() {
									// TODO Auto-generated method stub
									executeButton.setEnabled(false);
									executeButton.setText("Creating Playlist... " + pl.getProgress() + "%");
									if (pl.getProgress() == 100) {
										executeButton.setText("Create Playlist!");
										executeButton.setEnabled(true);
									}										
								}
							});
							if (pl.getProgress() == 100) {
								return;
							}	
						}
					}
					
				});
				progressThread.start();
				Parser parser = new ItunesXmlParser(xmlFilePath, pl);
				parser.parseSongs();
				Thread createPlaylistThread = new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						if (!pl.createPlaylist()) {
							progressThread.interrupt();
							return;
						}
						try {
							Thread.sleep(2000);
						} catch (InterruptedException ie) {
							// TODO: handle exception
						}						
						progressThread.interrupt();
					}
				});
				createPlaylistThread.start();
		    }
		});	
		
	    shell.open();

	    while (!shell.isDisposed()) {
	      if (!display.readAndDispatch())
	        display.sleep();
	    }
	    display.dispose();
	}
	
	private static void validateExecute() {
		if (xmlFilePath != null && playlistDestPath != null && playlistName != null && !playlistName.equals(""))
		{
			if (!playlistDestPath.endsWith("\\"))
				playlistDestPath += "\\";
			executeButton.setEnabled(true);
		}
		else
		{
			executeButton.setEnabled(false);			
		}
	}
}
