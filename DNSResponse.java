import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class DNSResponse extends Header {

   private final Query query;
   private String NAME;
   private short TYPE;
   private short CLASS;
   private int TTL;
   private short RDLENGTH;
   private byte[] RDATA;
   private int recordLength;

   public DNSResponse(Query query) {

      this.query = query;

      // recordLength = (query.get_QueryLength() - 4) + 10;
      // length = query.get_Length() + (query.get_Length() - 4) + 10;

      int lenName = query.get_QueryLength() - 2 * 2;
      this.recordLength = lenName + 2 * 5;
      length = query.get_Length() + recordLength;

      set_ID(query.get_ID());
      set_ANCOUNT((short) 1);
      set_QDCOUNT((short) 1);
      set_QR("1");
      set_OPCODE(query.get_OPCODE());
      set_AA("1");
      this.NAME = query.get_QNAME();
      this.CLASS = 1;
      this.TTL = 5;
      this.RDLENGTH = 0;
   }

   // handling No Error condition (RCODE = 0)
   public void handleNoError() {
      // Handle the No Error condition
      RCODE = "0000";
   }

   // handling Format Error (RCODE = 1)
   public void handleFormatError() {
      // Handle the Format Error
      RCODE = "0001";
   }

   // handling Server Failure (RCODE = 2)
   public void handleServerFailure() {
      // Handle the Server Failure
      RCODE = "0010";
   }

   // handling Name Error (RCODE = 3)
   public void handleNameError() {
      // Handle the Name Error
      RCODE = "0011";
   }

   // handling Not Implemented (RCODE = 4)
   public void handleNotImplemented() {
      // Handle the Not Implemented error
      RCODE = "0100";
   }

   // handling Refused (RCODE = 5)
   public void handleRefused() {
      // Handle the Refused error
      RCODE = "0101";
   }

   public void setData(String type, byte[] RData) {
      //System.out.println("RData : " + RData + "type : " + type);
      if (type.equals("TXT")) {
         set_ANCOUNT((short) 1);
         this.TYPE = 16;
         this.RDATA = RData;
         if (RDATA.length > 60000) {
            RDLENGTH = (short) Integer.parseInt(Integer.toBinaryString(60000), 2);
            handleNameError();
         } else {
            RDLENGTH = (short) Integer.parseInt(Integer.toBinaryString(RDATA.length), 2);
            handleNoError();
         }
      } else {
         handleFormatError();
      }
      recordLength += RDLENGTH;
      length = query.get_Length() + recordLength;
   }

   public ByteBuffer getByteQuery() throws IOException {

      int questionlength = NAME.getBytes().length + 2 + 2 + 4 + 2 + RDATA.length; 
      ByteBuffer buffer = ByteBuffer.allocate(12 + query.get_QNAME().getBytes().length + 2 + 2 + questionlength + recordLength); // j'ai une question 
      buffer.put(super.getByte());
      buffer.put(query.get_QNAME().getBytes()); 
      buffer.putShort(query.get_QTYPE());
      buffer.putShort(query.get_QCLASS());
      buffer.put(NAME.getBytes());
      buffer.putShort(TYPE);
      buffer.putShort(CLASS);
      buffer.putInt(TTL);
      buffer.putShort(RDLENGTH);
      buffer.put(RDATA);
      buffer.rewind();

      return buffer;
   }

   public Query getQuery() {
      return this.query;
   }

   public void printResponseFields() {
      System.out.println("QNAME: " + this.NAME);
      System.out.println("QTYPE: " + this.TYPE);
      System.out.println("QCLASS: " + this.CLASS);
      System.out.println("Query Length: " + this.query.get_Length());
      System.out.println("QR: " + this.get_QR());
      System.out.println("Z: " + this.get_Z());
      System.out.println("RCODE: " + this.get_RCODE());
      System.out.println("RDLENGTH: " + this.RDLENGTH);
      System.out.println("RDATA: " + (this.RDATA != null ? new String(this.RDATA, StandardCharsets.UTF_8) : "null"));
      System.out.println("Record Length: " + this.recordLength);
   }

}
