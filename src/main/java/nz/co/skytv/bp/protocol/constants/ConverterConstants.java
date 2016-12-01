package nz.co.skytv.bp.protocol.constants;


public class ConverterConstants {


  public static final String HEARTBEAT = "H|";
  public static final String RESPONSE = "R|";
  public static final String START = "S|";
  public static final String CRLF = "\r\n";
  public static final String LF = "\n";


  public static final String HARRIS = "HARRIS";
  public static final String NDS = "NDS";
  public static final String PROBEL = "PROBEL";
  public static final int NDS_PADDED_SIZE = 20;

  public static final String CISCO_NULL = "%23NULLEPRM3%23";
  public static final String CISCO_KEY = "PROMO03";//in true cisco/NDS style, an embedded magic string
  public static final String CISCO_PLAY = "urn:nnds:trigger:itvts:dataset:play";
}
