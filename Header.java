import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class Header {

   private short ID;
   private String QR;
   private String OPCODE;
   private String AA;
   private String TC;
   private String RD;
   private String RA;
   private String Z;
   protected String RCODE;
   private short QDCOUNT;
   private short ANCOUNT;
   private short NSCOUNT;
   private short ARCOUNT;
   protected int length;

   public Header(short ID, String QR, String OPCODE, String AA, String TC, String RD, String RA, String Z, String RCODE,
         short QDCOUNT, short ANCOUNT, short NSCOUNT, short ARCOUNT) {

      this.ID = ID;
      this.QR = QR;
      this.OPCODE = OPCODE;
      this.AA = AA;
      this.TC = TC;
      this.RD = RD;
      this.RA = RA;
      this.Z = Z;
      this.RCODE = RCODE;
      this.QDCOUNT = QDCOUNT;
      this.ANCOUNT = ANCOUNT;
      this.NSCOUNT = NSCOUNT;
      this.ARCOUNT = ARCOUNT;

   }

   public Header() {

      length = 12;
      TC = "0";
      RD = "1";
      RA = "0";
      Z = "000";
      RCODE = "0000";
      QDCOUNT = 0;
      ANCOUNT = 0;
      NSCOUNT = 0;
      ARCOUNT = 0;
   }

   public Header(int Length, DataInputStream input) throws IOException {
      this.length = Length;
      ID = input.readShort();

      short flags = input.readShort();
      char[] binaryFlags = String.format("%016d", Integer.parseInt(Integer.toBinaryString(0xFFFF & flags)))
            .toCharArray();

      this.QR = "" + binaryFlags[0];
      this.OPCODE = "" + binaryFlags[1] + binaryFlags[2] + binaryFlags[3] + binaryFlags[4];
      this.AA = "" + binaryFlags[5];
      this.TC = "" + binaryFlags[6];
      this.RD = "" + binaryFlags[7];
      this.RA = "" + binaryFlags[8];
      this.Z = "" + binaryFlags[9] + binaryFlags[10] + binaryFlags[11];
      this.RCODE = "" + binaryFlags[12] + binaryFlags[13] + binaryFlags[14] + binaryFlags[15];

      this.QDCOUNT = input.readShort();
      this.ANCOUNT = input.readShort();
      this.NSCOUNT = input.readShort();
      this.ARCOUNT = input.readShort();
   }

   public Header(short ID, short QDCOUNT, short ANCOUNT, short NSCOUNT, short ARCOUNT) {
      // Constructor for the response header

      this.ID = ID;
      this.QDCOUNT = QDCOUNT;
      this.ANCOUNT = ANCOUNT;
      this.NSCOUNT = NSCOUNT;
      this.ARCOUNT = ARCOUNT;
   }

   public ByteBuffer getByte() throws IOException {

      int headerLength = 12;

      ByteBuffer buffer = ByteBuffer.allocate(headerLength);
      buffer.putShort(ID); 
      buffer.putShort(Integer.valueOf(QR + OPCODE + AA + TC + RD + RA + Z + RCODE, 2).shortValue());
      buffer.putShort(QDCOUNT);
      buffer.putShort(ANCOUNT);
      buffer.putShort(NSCOUNT);
      buffer.putShort(ARCOUNT);

      buffer.rewind();

      return buffer;
   }

   public short get_ID() {
      return this.ID;
   }

   public void set_ID(short ID) {
      this.ID = ID;
   }

   public String get_QR() {
      return this.QR;
   }

   public void set_QR(String QR) {
      this.QR = QR;
   }

   public String get_OPCODE() {
      return this.OPCODE;
   }

   public void set_OPCODE(String OPCODE) {
      this.OPCODE = OPCODE;
   }

   public String get_AA() {
      return this.AA;
   }

   public void set_AA(String AA) {
      this.AA = AA;
   }

   public String get_TC() {
      return this.TC;
   }

   public void set_TC(String TC) {
      this.TC = TC;
   }

   public String get_RD() {
      return this.RD;
   }

   public void set_RD(String RD) {
      this.RD = RD;
   }

   public String get_RA() {
      return this.RA;
   }

   public void set_RA(String RA) {
      this.RA = RA;
   }

   public String get_Z() {
      return this.Z;
   }

   public void set_Z(String Z) {
      this.Z = Z;
   }

   public String get_RCODE() {
      return this.RCODE;
   }

   public void set_RCODE(String RCODE) {
      this.RCODE = RCODE;
   }

   public short get_QDCOUNT() {
      return this.QDCOUNT;
   }

   public void set_QDCOUNT(short QDCOUNT) {
      this.QDCOUNT = QDCOUNT;
   }

   public short get_ANCOUNT() {
      return this.ANCOUNT;
   }

   public void set_ANCOUNT(short i) {
      this.ANCOUNT = i;
   }

   public short get_NSCOUNT() {
      return this.NSCOUNT;
   }

   public void set_NSCOUNT(short NSCOUNT) {
      this.NSCOUNT = NSCOUNT;
   }

   public short get_ARCOUNT() {
      return this.ARCOUNT;
   }

   public void set_ARCOUNT(short ARCOUNT) {
      this.ARCOUNT = ARCOUNT;
   }

   public byte[] get_ByteQDCOUNT() {
      return new byte[] { (byte) ((QDCOUNT >> 8) & 0xFF), (byte) (QDCOUNT & 0xFF) };
   }

   public byte[] get_ByteANCOUNT() {
      return new byte[] { (byte) ((this.ANCOUNT >> 8) & 0xFF), (byte) (this.ANCOUNT & 0xFF) };
   }

   public byte[] get_ByteNSCOUNT() {
      return new byte[] { (byte) ((this.NSCOUNT >> 8) & 0xFF), (byte) (this.NSCOUNT & 0xFF) };
   }

   public byte[] get_ByteARCOUNT() {
      return new byte[] { (byte) ((this.ARCOUNT >> 8) & 0xFF), (byte) (this.ARCOUNT & 0xFF) };
   }

   public int get_Length() {
      return this.length;
   }

}
