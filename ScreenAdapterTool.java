package screenTools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

public class ScreenAdapterTool {

	private int baseDimen;

	private String dirStr = "./res";

	private final static String DeminTemplate = "<dimen name=\"size{0}\">{1}dp</dimen>\n";

	/**
	 * {0}-HEIGHT
	 */
	private final static String VALUE_TEMPLATE = "values-sw{0}dp";

//	private static final String SUPPORT_DIMESION = "320,480;480,800;480,854;540,960;600,1024;720,1184;720,1196;720,1280;768,1024;800,1280;1080,1812;1080,1920;1440,2560;";
	private static final String SUPPORT_DIMESION = "340;360;400;420;480;520;600;640;720;800;";

	private String supportStr = SUPPORT_DIMESION;

	public ScreenAdapterTool(int baseDimen, String supportStr) {
		this.baseDimen = baseDimen;

		if (!this.supportStr.contains(baseDimen + "")) {
			this.supportStr += baseDimen + ";";
		}

		this.supportStr += validateInput(supportStr);

		System.out.println(supportStr);

		File dir = new File(dirStr);
		if (!dir.exists()) {
			dir.mkdir();

		}
		System.out.println(dir.getAbsoluteFile());

	}

	/**
	 * @param supportStr w,h_...w,h;
	 * @return
	 */
	private String validateInput(String supportStr) {
		StringBuffer sb = new StringBuffer();
		String[] vals = supportStr.split(";");
		int dimen = -1;
		for (String val : vals) {
			try {
				if (val == null || val.trim().length() == 0)
					continue;

				dimen = Integer.parseInt(vals[0]);
			} catch (Exception e) {
				System.out.println("skip invalidate params : w,h = " + val);
				continue;
			}
			sb.append(dimen + ";");
		}

		return sb.toString();
	}

	public void generate() {
		String[] vals = supportStr.split(";");
		for (String val : vals) {
			generateXmlFile(Integer.parseInt(val));
		}

	}

	private void generateXmlFile(int dimen) {

		StringBuffer sbForWidth = new StringBuffer();
		sbForWidth.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
		sbForWidth.append("<resources>");
		float cellw = dimen * 1.0f / baseDimen;

		System.out.println("width : " + dimen + "," + baseDimen + "," + cellw);
		for (int i = 1; i < baseDimen; i++) {
			sbForWidth.append(DeminTemplate.replace("{0}", i + "").replace("{1}", change(cellw * i) + ""));
		}
		sbForWidth.append(DeminTemplate.replace("{0}", baseDimen + "").replace("{1}", dimen + ""));
		sbForWidth.append("</resources>");

		File fileDir = new File(dirStr + File.separator + VALUE_TEMPLATE.replace("{0}", dimen + ""));
		fileDir.mkdir();

		File layxFile = new File(fileDir.getAbsolutePath(), "dimen_size.xml");
		try {
			PrintWriter pw = new PrintWriter(new FileOutputStream(layxFile));
			pw.print(sbForWidth.toString());
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static float change(float a) {
		int temp = (int) (a * 100);
		return temp / 100f;
	}

	public static void main(String[] args) {
		int baseDemin = 320;
		String addition = "";
		try {
			if (args.length >= 3) {
				baseDemin = Integer.parseInt(args[0]);
				addition = args[2];
			} else if (args.length >= 2) {
				baseDemin = Integer.parseInt(args[0]);
			} else if (args.length >= 1) {
				addition = args[0];
			}
		} catch (NumberFormatException e) {

			System.err.println("right input params : java -jar xxx.jar width height w,h_w,h_..._w,h;");
			e.printStackTrace();
			System.exit(-1);
		}

		new ScreenAdapterTool(baseDemin, addition).generate();
	}

}