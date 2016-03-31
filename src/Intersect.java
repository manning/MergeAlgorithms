import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Christopher Manning
 */
public class Intersect {

  /** If true, loaded postings lists are printed; this just shows that they were loaded correctly. */
  static boolean DEBUG = false;

  /** Test cases */
  static final String[][] intersectTestCases = {
      { "1; 2; 4; 5; 7; 13",
        "1; 4; 5; 6; 8; 10; 13",
        "[1, 4, 5, 13]" },
      { "1; 5",
        "1; 5",
        "[1, 5]" },
      { "1:1,2,3,4,5,6,7",
        "1:1,2,3,4,5,6,7",
        "[1]" },
      { "1:11,92; 17:6,16; 21:103,113,114",
        "4:8; 5:2; 17:11; 21:3, 97,108",
        "[17, 21]" },
      { "5:4; 11:7,18; 12:1,17; 14:8,16; 15:363,367; 103:28",
        "3:2; 8:9; 11:17,25; 14:17,434; 15:101; 16:19; 18:42; 100:11; 103:24; 109:11",
        "[11, 14, 15, 103]" },
      { "1:1; 5:1; 11:1; 13:1; 19:1; 43:1",
        "2:1; 3:1; 5:1; 9:1; 11:1; 15:1; 19:1; 33:1; 45:1",
        "[5, 11, 19]" },
      { "1:1",
        "2:1; 189:10",
        "[]" },
      { "3;4;9;16;19;24;25;27;28;30;31;32;33;35;36;43;46;47;52;55;57;60;61;62;" +
              "64;65;66;77;78;80;83;86;91;98;99;100;101;102;103;104;106;108;112;113;116;" +
              "117;119;120;127;141;147;151;156;158;168;170;172;175;179;182;184;185;187;195;" +
              "197;199;202;206;207;208;209;210;213;221;225;227;228;233;238;249;252;255;256;" +
              "266;267;268;270;271;273;274;281;284;285;289;290;292;294;299;301;302;303;306;" +
              "308;312;320;321;322;325;326;328;329;332;334;335;337;341;342;344;345;347;349;" +
              "356;357;358;360;364;376;377;379;382;383;385;395;397;403;404;405;406;410;412;" +
              "417;418;423;430;431;432;433;434;437;440;441;445;446;452;453;454;461;464;466;" +
              "469;477;480;486;487;488;495;496;506;507;511;512;517;518;520;522;524;526;532;" +
              "535;540;543;549;550;558;562;563;564;571;574;581;586;587;592;597;598;604;607;" +
              "608;615;620;621;622;625;633;634;635;636;639;640;642;653;654;656;658;660;668;" +
              "671;676;680;681;683;686;687;689;694;697;702;703;708;710;711;714;722;723;729;" +
              "730;737;739;742;746;747;750;756;757;758;759;764;765;766;769;770;772;777;780;" +
              "782;783;784;791;795;798;801;807;812;815;816;822;823;824;825;828;830;833;835;" +
              "836;837;841;852;854;863;864;865;868;870;873;880;882;884;887;888;889;897;902;" +
              "906;912;914;918;922;924;925;928;929;932;933;934;938;939;941;943;944;947;948;" +
              "952;955;961;962;963;968;971;973;975;979;980;983;984;987;989;993;995;996;" +
              "999", // "faculty" in first 1000 documents of Stanford crawl
        "324;335;418;466;505;686",  // "anthropology" in first 1000 documents of Stanford crawl
        "[335, 418, 466, 686]" },
  };

  /** Stores the Posting for a single document: a docID and optionally a list of document positions. */
  private static class Posting {
    final int docID;
    final List<Integer> positions;
    public Posting(int docID, List<Integer> positions) {
      this.docID = docID;
      this.positions = positions;
    }
    public Iterator<Integer> positions() { return positions.iterator(); }
    public String toString() {
      return docID + ":" + positions;
    }
  }

  /** Returns the next item from the Iterator, or null if it is exhausted.
   *  (This is a more C-like method than idiomatic Java, but we use it so as
   *  to be more parallel to the pseudo-code in the textbook.)
   */
  static <X> X popNextOrNull(Iterator<X> p) {
    if (p.hasNext()) {
      return p.next();
    } else {
      return null;
    }
  }

  static List<Integer> intersect(Iterator<Posting> p1, Iterator<Posting> p2) {
    List<Integer> answer = new ArrayList<>();

    Posting pp1 = popNextOrNull(p1);
    Posting pp2 = popNextOrNull(p2);

    // WRITE ALGORITHM HERE

    return answer;
  }

  /** Load a single postings list: Information about where a single token
   *  appears in documents in the collection. This can load either a document
   *  level posting which is a list of integer docID separated by semicolons
   *  or a positional postings list, where each docID is followed by a colon
   *  and then
   *  @param postingsString A String representation of a postings list
   *  @return An Iterator over a {@code List<Posting>}
   */
  static Iterator<Posting> loadPostingsList(String postingsString) {
    List<Posting> postingsList = new ArrayList<>();
    String[] postingsArray = postingsString.split(";");
    for (String posting : postingsArray) {
      String[] bits = posting.split(":");
      String[] poses = {};
      if (bits.length > 1) {
        poses = bits[1].split(",");
      }
      int docID = Integer.valueOf(bits[0].trim());
      List<Integer> positions = new ArrayList<>();
      for (String pos : poses) {
        positions.add(Integer.valueOf(pos.trim()));
      }
      Posting post = new Posting(docID, positions);
      postingsList.add(post);
    }
    if (DEBUG) {
      System.err.println("Loaded postings list: " + postingsList);
    }
    return postingsList.iterator();
  }

  /** Main method. With no parameters, it runs some internal test cases.
   *  With two postings list arguments, it intersects the arguments given on the command line.
   *  Otherwise, it will print a usage message.
   *
   *  @param args Command-line arguments, as above.
   */
  public static void main(String[] args) {
    if (args.length == 0) {
      for (String[] test : intersectTestCases) {
        Iterator<Posting> pl1 = loadPostingsList(test[0]);
        Iterator<Posting> pl2 = loadPostingsList(test[1]);
        System.out.println("Intersection of " + test[0]);
        System.out.println("            and " + test[1] + ": ");
        List<Integer> ans = intersect(pl1, pl2);
        System.out.println("Answer:         " + ans);
        if ( ! ans.toString().equals(test[2])) {
          System.out.println("Should be:      " + test[2]);
          System.out.println("*** ERROR ***");
        }
        System.out.println();
      }
    } else if (args.length != 2) {
      System.err.println("Usage: java Intersect postingsList1 postingsList2");
      System.err.println("       postingsList format(s): '1:17,25; 4:17,191,291,430,434; 5:14,19,10'");
      System.err.println("                           or: '1; 4; 5'");
    } else {
      Iterator<Posting> pl1 = loadPostingsList(args[0]);
      Iterator<Posting> pl2 = loadPostingsList(args[1]);
      List<Integer> ans = intersect(pl1, pl2);
      System.out.println(ans);
    }
  }

}
