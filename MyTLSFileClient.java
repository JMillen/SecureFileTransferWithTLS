// The client is usually much more straight forward
// Defaults will load Java’s set of Trusted Certificates
// Java will validate there is a path to a trusted CA
// By default, Java will NOT do hostname validation,
// but the more secure thing to do is to check!

// THE CODE BELOW IS INCOMPLETE AND HAS PROBLEMS
// FOR EXAMPLE, IT IS MISSING THE NECESSARY EXCEPTION HANDLING


import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.cert.X509Certificate;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;

public class MyTLSFileClient {
  public static void main(String args[])
  {
    if(args.length != 3){
      System.out.println("MyTLSFileClient Incorrect: <host> <port> <filename>");
      System.exit(1);
    }

    String host = args[0];
    int port = Integer.parseInt(args[1]);
    String filename = args[2];

    SSLSocketFactory factory = (SSLSocketFactory)SSLSocketFactory.getDefault();
    SSLSocket socket = null;

    try{
      socket = (SSLSocket)factory.createSocket(host, port);

      // set HTTPS-style checking of HostName _before_ 
      // the handshake
      SSLParameters params = new SSLParameters();
      params.setEndpointIdentificationAlgorithm("HTTPS");
      socket.setSSLParameters(params);

      socket.startHandshake(); // explicitly starting the TLS handshake

      // at this point, can use getInputStream and 
      // getOutputStream methods as you would in a regular Socket
      // Send the file request to the server
      sendRequest(socket, filename);

      // get the X509Certificate for this session
      SSLSession session = socket.getSession();
      X509Certificate cert = (X509Certificate) session.getPeerCertificates()[0];

      // extract the CommonName, and then compare
      MyTLSFileClient client = new MyTLSFileClient();
      client.getCommonName(cert);
    }
    catch(Exception e){
      System.out.println(e);
      System.out.println("Error creating socket");
    }
  }

  String getCommonName(X509Certificate cert)
  {
    String name = cert.getSubjectX500Principal().getName();
    LdapName ln = null;
    String cn = null;

    try{
      ln = new LdapName(name);
    } catch(Exception e){
      System.out.println(e);
      System.out.println("Error parsing name");
    }
    
    // Rdn: Relative Distinguished Name
    for(Rdn rdn : ln.getRdns()) 
      if("CN".equalsIgnoreCase(rdn.getType()))
        cn = rdn.getValue().toString();
    return cn;
  }

  public static void sendRequest(SSLSocket socket, String filename)
  {
    try{
      // Send the request to the server
      OutputStream outputStream = socket.getOutputStream();
      outputStream.write((filename + "\n").getBytes());
      outputStream.flush();
      
      // Confim request was sent
      System.out.println("Request sent: " + filename);

      // Receive the file from the server
      InputStream inputStream = socket.getInputStream();
      FileOutputStream fileOutputStream = new FileOutputStream("recv_" + filename);

      byte[] buffer = new byte[1024];
      int bytesRead = 0;

      while((bytesRead = inputStream.read(buffer)) != -1){
        fileOutputStream.write(buffer, 0, bytesRead);
      }

      //Confim file was received
      System.out.println("File received: " + filename);

      // Close stream
      fileOutputStream.close();
      inputStream.close();
      outputStream.close();
      socket.close();
    } catch(Exception e){
      System.out.println(e);
      System.out.println("Error sending request");
    }
  }
}
