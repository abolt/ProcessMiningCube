package application.utils;

public class LpSolveUtils {

	private static final String BASE_PATH = "lpsolve/resources/";

	public static final String LPSOLVE_JAR = "lpsolve55j.jar";
	public static final String BINARIES_FOLDER = "lib";

	/**
	 * Returns the path to the binaries and jar file of LpSolve, based on OS and
	 * JVM Architecture. To get the jar file, use: String jar =
	 * getOSBasedLpSolvePath + LPSOLVE_JAR;.
	 * 
	 * @param os
	 * @param jvmArch
	 * @return
	 */
	public static String getOSBasedLpSolvePath(String os, String jvmArch) {
		return BASE_PATH + getOSFolder(os, jvmArch);
	}

	public static String getOSFolder(String os, String jvmArch) {
		String result = "";
		switch (os) {
		case "OSX":
			result = "mac" + jvmArchToString(jvmArch) + "/";
			break;
		case "WINDOWS":
			result = "win" + jvmArchToString(jvmArch) + "/";
			break;
		case "OTHER":
		case "SOLARIS":
		case "UNIX":
		default:
			result = "ux" + jvmArchToString(jvmArch) + "/";
			break;
		}
		return result;
	}

	public static String jvmArchToString(String jvmArch) {
		String res = "";
		switch (jvmArch) {
		case "SIXTY_FOUR":
			res = "64";
			break;
		case "THIRTY_TWO":
		default:
			res = "32";
			break;
		}
		return res;
	}
}
