package base;

//public static File filePathDialog() {
//        JFileChooser chooser = new JFileChooser();
//        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
//
//        }

import gui.Utils;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.*;

public class Filesystem {
    private static final int BUFFER_SIZE = 1024 * 10;
    
    /**
     * Read the specified File using NIO channels. Send the file to the specified socket.
     * @param file File object, the file to be read
     * @param socket Socket where the file will be sent
     */
    public void readFile(File file, Socket socket) {
        try (
                FileChannel inChannel = new FileInputStream(file).getChannel();
                SocketChannel outChannel = socket.getChannel()
        ) {
            transfer(inChannel, outChannel);
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + file.getAbsolutePath());
            Utils.showErrorDialog("File not found: " + file.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error while reading file: ");
            e.printStackTrace();
            Utils.showErrorDialog("Error while reading file.");
        }
    }
    
    /**
     * Read the specified Socket using NIO, and save the data to File.
     * @param socket Socket from where data will be read
     * @param file File object, destinationation of the data
     */
    public void writeFile(Socket socket, File file) {
        try (
                SocketChannel inChannel = socket.getChannel();
                FileChannel outChannel = new FileOutputStream(file).getChannel()
        ) {
            transfer(inChannel, outChannel);
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + file.getAbsolutePath());
            Utils.showErrorDialog("File not found: " + file.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error while writing file: ");
            e.printStackTrace();
            Utils.showErrorDialog("Error while writing file.");
        }
    }
    
    private void transfer(ReadableByteChannel in, WritableByteChannel out) throws IOException {
        int read = 0;
        while (read != -1) {
            // Read into buffer
            ByteBuffer fileBuffer = ByteBuffer.allocate(BUFFER_SIZE);
            read = in.read(fileBuffer);
            fileBuffer.flip();
    
            // Send from buffer to output
            while (fileBuffer.hasRemaining()) out.write(fileBuffer);
        }
    }
}
