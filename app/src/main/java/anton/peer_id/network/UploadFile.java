package anton.peer_id.network;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

public class UploadFile {
    private final String boundary, valueName;
    private final File file;

    UploadFile(File file, String boundary, String valueName) {
        this.boundary = boundary;
        this.valueName = valueName;
        this.file = file;
    }

    long getContentLength() {
        long length = file.length();
        length += getFileDescription().length();
        length += getBoundaryEnd().length();
        return length;
    }

    String getFileDescription() {
        return "\r\n--" + boundary + "\r\n" +
                "Content-Disposition: form-data; name=\"" + valueName + "\"; filename=\"" + file.getName() + "\"\r\n" +
                "Content-Type: %s\r\n\r\n";
    }

    private String getBoundaryEnd() {
        return String.format("\r\n--%s--\r\n", boundary);
    }

    void writeTo(OutputStream outputStream) throws IOException {
        outputStream.write(getFileDescription().getBytes("UTF-8"));
        FileInputStream reader = new FileInputStream(file);
        byte[] fileBuffer = new byte[2048];
        int bytesRead;
        while ((bytesRead = reader.read(fileBuffer)) != -1) {
            outputStream.write(fileBuffer, 0, bytesRead);
        }
        reader.close();
        outputStream.write(getBoundaryEnd().getBytes("UTF-8"));
    }
}
