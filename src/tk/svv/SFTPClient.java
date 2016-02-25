package tk.svv;
import com.jcraft.jsch.*;
import java.io.*;

public class SFTPClient {
    private String pHost = "";
    private String pUser = "";
    private String pPass = "";
    private String pDir = "";
    private int pPort = 21;
    public String lastErr = "";

    public void setEndpoint(String host, int port, String user, String pass) {
        pHost = host;
        pPort = port;
        pUser = user;
        pPass = pass;
    }

    public void setDirectory(String dir) {
        pDir = dir;
    }

    public boolean hasError() {
        return !lastErr.equals("");
    }

    public boolean downloadFile(String remoteFile, String localFile) throws Exception {
        JSch jsch = new JSch();

        Channel channel;
        ChannelSftp channelSftp = null;
        Session session = null;
        boolean ok = true;
        lastErr = "";

        try {
            session = jsch.getSession(pUser, pHost, pPort);
            session.setPassword(pPass);
            session.setConfig("StrictHostKeyChecking", "no");

            session.connect();

            channel = session.openChannel("sftp");
            channel.connect();
            channelSftp = (ChannelSftp) channel;

            if(pDir != null && !pDir.isEmpty())
                channelSftp.cd(pDir);

            OutputStream output = new FileOutputStream(localFile);

            channelSftp.get(remoteFile, output);

            output.close();
        }
        catch(Exception e) {
            lastErr = e.getMessage();
            ok = false;
        }
        finally {
            if (channelSftp != null)
                channelSftp.disconnect();
            if(session != null)
                session.disconnect();
            return ok;
        }
    }

    public boolean uploadFile(String localFile, String remoteFile) throws Exception {
        JSch jsch = new JSch();

        Channel channel;
        ChannelSftp channelSftp = null;
        Session session = null;
        boolean ok = true;
        lastErr = "";

        try {
            session = jsch.getSession(pUser, pHost, pPort);
            session.setPassword(pPass);
            session.setConfig("StrictHostKeyChecking", "no");

            session.connect();

            channel = session.openChannel("sftp");
            channel.connect();
            channelSftp = (ChannelSftp) channel;

            if(pDir != null && !pDir.isEmpty())
                channelSftp.cd(pDir);

            File f1 = new File(localFile);
            long localSize = f1.length();

            channelSftp.put(new FileInputStream(f1), remoteFile);

            SftpATTRS s = channelSftp.stat(remoteFile);
            if(s.getSize() < localSize)
                throw new IOException("Uploaded file (" + remoteFile + ") size was less than local size (" + localFile + ")");
        }
        catch(Exception e) {
            lastErr = e.getMessage();
            ok = false;
        }
        finally {
            if (channelSftp != null)
                channelSftp.disconnect();
            if(session != null)
                session.disconnect();
            return ok;
        }
    }

    public FileStat fileStat(String remoteFile) throws Exception {
        JSch jsch = new JSch();

        Channel channel;
        ChannelSftp channelSftp = null;
        Session session = null;

        try {
            session = jsch.getSession(pUser, pHost, pPort);
            session.setPassword(pPass);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            channel = session.openChannel("sftp");
            channel.connect();
            channelSftp = (ChannelSftp) channel;

            if (pDir != null && !pDir.isEmpty())
                channelSftp.cd(pDir);

            FileStat fs = new FileStat();

            SftpATTRS s = channelSftp.stat(remoteFile);
            fs.name = remoteFile;
            fs.size = s.getSize();
            fs.date = s.getMtimeString();
            fs.time = s.getMTime();

            return fs;
        }
        catch (Exception e) {
            throw e;
        }
        finally {
            if(channelSftp != null)
                channelSftp.disconnect();
            if(session != null)
                session.disconnect();
        }
    }
}
