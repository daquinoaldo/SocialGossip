package base;

import gui.Utils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.file.StandardOpenOption;

public class Filesystem {
    private static final int BUFFER_SIZE = 1024 * 10;
    
    /**
     * Read the specified File using NIO channels. Send the file to the specified socket.
     * @param file File object, the file to be read
     * @param outChannel Socket where the file will be sent
     */
    public static void readFile(File file, SocketChannel outChannel) {
        try (
                FileChannel inChannel = FileChannel.open(file.toPath(), StandardOpenOption.READ)
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
     * @param inChannel Socket from where data will be read
     * @param file File object, destinationation of the data
     */
    public static void writeFile(SocketChannel inChannel, File file) {
        try (
                FileChannel outputChannel = FileChannel.open(file.toPath(),
                        StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)
        ) {
            transfer(inChannel, outputChannel);
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + file.getAbsolutePath());
            Utils.showErrorDialog("File not found: " + file.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error while writing file: ");
            e.printStackTrace();
            Utils.showErrorDialog("Error while writing file.");
        }
    }
    
    private static void transfer(ReadableByteChannel in, WritableByteChannel out) throws IOException {
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
