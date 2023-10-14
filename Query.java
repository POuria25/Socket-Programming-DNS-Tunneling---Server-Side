import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class Query extends Header {

   protected String QNAME;
   protected short QTYPE;
   protected short QCLASS;
   protected int queryLength;


   public String get_QNAME() {
      String[] tmp = QNAME.split("\\.");
      ByteBuffer bb = ByteBuffer.allocate(QNAME.length() + 2);
      for(int i = 0 ; i < tmp.length ; i++){
         bb.put((byte)tmp[i].length());
         bb.put(tmp[i].getBytes());
      }
      return new String(bb.array());
   }

   public void set_QNAME(String QNAME) {
      this.QNAME = QNAME;
   }

   public short get_QTYPE() {
      return this.QTYPE;
   }

   public void set_QTYPE(short QTYPE) {
      this.QTYPE = QTYPE;
   }

   public short get_QCLASS() {
      return this.QCLASS;
   }

   public void set_QCLASS(short QCLASS) {
      this.QCLASS = QCLASS;
   }

   public int get_QueryLength() {
      return this.queryLength;
   }


   public boolean RequestRefused(String Domain) {
      return (get_QDCOUNT() != 1) || (get_ANCOUNT() != 0) || (get_NSCOUNT() != 0) || (get_ARCOUNT() != 0)
            || (QTYPE != 16) || (!QNAME.endsWith(Domain));
   }

   public String decode() {
      String result = QNAME.split("\\.")[0];
      byte[] decodedUrl = Base32.decode(result);
      String url = new String(decodedUrl);

      return url;
   }

   public ByteBuffer getByteQuery() throws IOException {

      int questionlength = QNAME.getBytes().length + 2 + 2;
      ByteBuffer buffer = ByteBuffer.allocate(12 + questionlength);
      buffer.put(super.getByte());
      buffer.put(QNAME.getBytes());
      buffer.putShort(QTYPE);
      buffer.putShort(QCLASS);
      buffer.rewind();

      return buffer;
   }

   /*
    * Expected QR.equals(O) a query not a response
    * Expected RCODE: 0000 (query, not response).
    * Expected Z: 000 (per documentation).
    */
   public boolean CheckFormat() {
      if (!get_QR().equals("0") || !get_Z().equals("000") || !get_RCODE().equals("0000")) {
         return true;
      } else {
         return false;
      }
   }

   public void printQuery() { 
      System.out.println("QNAME: " + QNAME);
      System.out.println("QTYPE: " + QTYPE);
      System.out.println("QCLASS: " + QCLASS);
      System.out.println("Query Length: " + queryLength);
      System.out.println("QR: " + get_QR());
      System.out.println("Z: " + get_Z());
      System.out.println("RCODE: " + get_RCODE());
  }
  

   public Query(int length, DataInputStream input) throws IOException {
      super(length, input);

      StringBuilder qnameBuilder = new StringBuilder();
      byte lengthByte = input.readByte();

      while (lengthByte != 0) {
         byte[] label = new byte[lengthByte];
         input.readFully(label);

         if (qnameBuilder.length() > 0) {
            qnameBuilder.append(".");
         }
         qnameBuilder.append(new String(label, StandardCharsets.UTF_8));

         lengthByte = input.readByte();
      }

      QNAME = qnameBuilder.toString();
      QTYPE = input.readShort();
      QCLASS = input.readShort();

      this.queryLength = length - 12;
   }
}
