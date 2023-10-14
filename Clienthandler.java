import java.io.*;
import java.net.*;
import java.nio.*;

public class Clienthandler implements Runnable {
    private final Socket clientSocket;
    private final String domain;
    private final SocketAddress IP;

    public Clienthandler(Socket clientSocket, String domain, SocketAddress IP) {
        this.clientSocket = clientSocket;
        this.domain = domain;
        this.IP = IP;
    }

    public void sendResponse(DNSResponse response) {
        try {
            OutputStream output = clientSocket.getOutputStream();
            ByteBuffer byteBuffer = response.getByteQuery();
            byte[] sendBuffer = byteBuffer.array();
            ByteBuffer size = ByteBuffer.allocate(2).putShort((short) sendBuffer.length);
            output.write(size.array());
            output.write(sendBuffer);
            System.out.println("Question (CL=" + IP + ", NAME=" + response.getQuery().get_QNAME() + ", TYPE="
                    + response.getQuery().get_QTYPE() + ") => " + Integer.parseInt(response.get_RCODE(), 2));
            output.flush();
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void receiveData() {
        try {
            clientSocket.setTcpNoDelay(true);
            clientSocket.setSoTimeout(5000);

            // retrieves the input stream associated with the 'clientSocket' socket object.
            InputStream inputstream = clientSocket.getInputStream();

            // Read the first 2 byte of DNS header to get the leangth of query
            byte[] tmp = new byte[2];
            int Length = inputstream.read(tmp);
            if (Length != 2) {
                System.out.println("Error reading : " + tmp);
                inputstream.close();
                clientSocket.close();
            }
            // int queryLength = (int) (tmp[0] << 8) + (int) (tmp[1]);
            int queryLength = ((tmp[0] & 0xFF) << 8) + (tmp[1] & 0xFF);

            byte[] queryBuffer = new byte[queryLength];

            int len = 0;
            while (len < queryLength) {
                len += inputstream.read(queryBuffer, len, queryLength - len);
                if (len == -1) {
                    System.out.println("Problem with read");
                    inputstream.close();
                    clientSocket.close();
                }
            }

            ByteArrayInputStream input = new ByteArrayInputStream(queryBuffer);
            DataInputStream dataInput = new DataInputStream(input);
            Query query = new Query(queryLength, dataInput);
            // query.printQuery();

            if (query == null){
                System.out.println("Query is null");
            }
            DNSResponse response = new DNSResponse(query);
            // response.printResponseFields();
            // System.out.println("Created response object: " + response);

            if (query.CheckFormat()) {
                response.handleFormatError();
                sendResponse(response);
            } else if (query.RequestRefused(domain)) {
                response.handleRefused();
                sendResponse(response);
            } else {
                HttpGet http = new HttpGet(query.decode());
                http.httpRequest(domain);
                byte[] HTTPresponse = http.SendData();
                // System.out.println("Received HTTP response: " + HTTPresponse);
                response.setData("TXT", HTTPresponse);
                // System.out.println("Set response data");
                sendResponse(response);
            }
            inputstream.close();
            clientSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // public void sendResponse()
    @Override
    public void run() {
        receiveData();
    }
}
