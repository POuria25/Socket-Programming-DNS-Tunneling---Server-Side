import java.io.*;
import java.net.*;

public class Server {
   private int PORT;
   private String domain;

   public Server(int PORT, String domain) {
      this.PORT = PORT;
      this.domain = domain;
   }

   public int get_port() {
      return this.PORT;
   }

   public void start() {
      try (ServerSocket server = new ServerSocket(this.PORT)) {
         while (true) {
            Socket clientSocket = server.accept();
            SocketAddress address = clientSocket.getRemoteSocketAddress();
            Clienthandler client = new Clienthandler(clientSocket, domain, address);            
            Thread thread = new Thread(client);
            thread.start();
         }
      } catch (IOException e) {
         System.out.println(e.getMessage());
         System.exit(-1);
      }
   }

   public static void main(String[] args) {

      if (args.length != 1) {
         System.out.println("Command should be : java Server <owned domain name>");
         System.out.println("for example");
         System.out.println("java Server example.com");
         System.exit(0);
      }

      Server server = new Server(1030, args[0]);
      server.start();
      System.out.println("Server is now running...");

   }
}
