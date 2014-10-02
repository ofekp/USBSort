USBSort
=======

Now you can listen to sorted playlists while driving, in order!

v1.0 - Create physically ordered iTunes playlists on your USB.

This project is just something I needed to make sorted playlists for driving.
The USB you put in your car is most likely being read in physical order, this means that you
need to add the files to a folder in your USB one by one to make them keep their position in
the playlist. If each song is indexed with a number you can use CMD or Bash commands to do
that for you quite easily, but if you use iTunes for example your files are not named with
index according to their position in playlists :(

Currently this Java program will take an XML file generated by iTunes when exporting a playlist
and will parse it to create a physically ordered playlist in the destination folder.

This project may be extended to other programs that are being used for keeping track of songs
and playlists by adding more parsers.

I thank this code each time I'm driving, I hope you'll find this helpful too.