// Java provides SSLSocket and SSLServerSocket classes, which are roughly 
// equivalent to Socket and ServerSocket:
//       SSLServerSocket listens on a port for incoming connections, like ServerSocket
//       SSLSocket connects to an SSLServerSocket, like Socket, and represents an individual 
//       connection accepted from an SSLServerSocket.
// To create a SSLSocket or SSLServerSocket, we must use "factories"

// Socket factories are a convenient way to set TLS parameters that will 
// apply to Sockets created from the factory, e.g:
//       Which TLS versions to support
//       Which Ciphers and Hashes to use
//       Which Keys to use and which Certificates to trust
// As you might guess by the names
//       SSLServerSocketFactory creates SSLServerSocket objects
//       SSLSocketFactory creates SSLSocket objects

// Java uses KeyStore objects to store Keys and Certificates
// A KeyStore object is used when encrypting and authenticating
// The files that contain Keys and Certificates are password protected

// THE CODE BELOW IS INCOMPLETE AND HAS PROBLEMS
// FOR EXAMPLE, IT IS MISSING THE NECESSARY EXCEPTION HANDLING

import java.io.BufferedReader;
import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.KeyStore;
import javax.net.ServerSocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

public class MyTLSFileServer {

   private static ServerSocketFactory getSSF()
   {
      try{
         // Get 
         //    an SSL Context that speaks some version of TLS, 
         //    a KeyManager that can hold certs in X.509 format,  
         //    and a JavaKeyStore (JKS) instance   
         SSLContext ctx = SSLContext.getInstance("TLS");
         KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
         KeyStore ks = KeyStore.getInstance("JKS");

         // Obtain the passphrase to unlock the JKS file.   
         Console console = System.console();
         char[] passphrase = console.readPassword("Enter passphrase: ");

         // Load the keystore file. The passphrase is   
         // an optional parameter to allow for integrity   
         // checking of the keystore. Could be null   
         ks.load(new FileInputStream("server.jks"), passphrase);

         // Init the KeyManagerFactory with a source   
         // of key material. The passphrase is necessary   
         // to unlock the private key contained.   
         kmf.init(ks, passphrase);

         // initialise the SSL context with the keys.   
         ctx.init(kmf.getKeyManagers(), null, null);

         // Get the factory we will use to create   
         // our SSLServerSocket   
         SSLServerSocketFactory ssf = ctx.getServerSocketFactory();
         return ssf;
      }
      catch(Exception e){
         System.out.println(e);
         System.out.println("Error creating ServerSocketFactory");
         return null;
      }
   }

   public static void sendFile(SSLSocket socket){
      try{
         System.out.println("Sending file");
         // Get the input and output streams so the 
         // server can read and write to the socket
         InputStream in = socket.getInputStream();
         OutputStream out = socket.getOutputStream();

         // Get the filename from the Client
         BufferedReader br = new BufferedReader(new InputStreamReader(in));
         String filename = br.readLine();
         File file = new File(filename);

         // Show the file requested
         System.out.println("File requested: " + filename);

         // Checks if file exists on the server
         if(!file.exists()){
            System.out.println("File does not exist");
            return;
         }

         // Open the file and read it into a byte array
         FileInputStream fileInputStream = new FileInputStream(filename);
         byte[] buffer = new byte[1024];
         int bytesRead = 0;

         // Write the file to the socket
         while ((bytesRead = fileInputStream.read(buffer)) != -1){
            out.write(buffer, 0, bytesRead);
         }

         //Confim file was sent
         System.out.println("File sent");

         // Close the file and the socket
         fileInputStream.close();
         socket.close();
      }
      catch(Exception e){
         System.out.println(e);
         System.out.println("Error sending file");
      }
   }

   public static void main(String args[]) 
   { 
      //Error message if port is not specified
      if(args.length != 1){
         System.out.println("MyTLSFileServer Incorrect: <port>");
         System.exit(1);
      }

      int port = Integer.parseInt(args[0]);

      // use the getSSF method to get a  SSLServerSocketFactory and 
      // create our  SSLServerSocket, bound to specified port  
      ServerSocketFactory ssf = getSSF(); 
      SSLServerSocket ss = null;

      try{
      ss = (SSLServerSocket) ssf.createServerSocket(port); 
      String EnabledProtocols[] = {"TLSv1.2", "TLSv1.3"}; 
      ss.setEnabledProtocols(EnabledProtocols); 
      
      //Wait for incoming connections
      while(true){
         SSLSocket s = (SSLSocket)ss.accept();

         //Show client has connected
         System.out.println("Client connected" + s.getInetAddress());

         //handshake
         s.startHandshake();

         //Send the file to the client
         sendFile(s);
      }

      } catch(Exception e){
         System.out.println(e);
         System.out.println("Error creating ServerSocket");
      }
   }
}


