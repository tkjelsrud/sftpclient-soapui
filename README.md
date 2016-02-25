# sftpclient-soapui
Simple SFTPclient to be used with SoapUI. Place .jar in &lt;soapui-dir>/bin/ext/

Example implementation in SoapUI Groovy script (teststeps):

import tk.svv.*;

s = new SFTPClient();

// host, port, user, pass
s.setEndpoint("sftp.host.com", 22, "USER", "PASSWORD"); 
	
s.setDirectory("/ftp/start/path/");

// UPLOAD
boolean res = s.uploadFile("path/to/local/file", "remote/path/filename");

// DOWNLOAD
boolean res = s.downloadFile("remote/path/filename", "path/to/local/newfile");

// GET FILE STATS (check if exists, date modified, size, etc)
FileStat fs = s.fileStat("remote/path/filename");

// FileStat has the following properties: name, size (long), date (string), time (int)
