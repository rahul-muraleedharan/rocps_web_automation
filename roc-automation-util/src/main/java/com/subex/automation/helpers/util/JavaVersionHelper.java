package com.subex.automation.helpers.util;

import com.subex.automation.helpers.data.ValidationHelper;

public class JavaVersionHelper {

	private JavaVersionHelper() {
	}

	/**
	 * Remote shell snippet that switches the system 'java' alternative to the
	 * given major version ("8", "11", ...). The remote box may use Debian
	 * update-alternatives or Red Hat alternatives; both accept `--display java`
	 * and `--set java <path>` (Red Hat rejects `--list java`). Path naming of
	 * JDKs varies, so candidate paths are pulled from the `... - priority N`
	 * lines and each is matched on the version it actually reports
	 * (`java -version`: "11.0.20" -> 11, legacy "1.8.0_x" -> 8). The switch is
	 * applied with `sudo -S` so the alternatives update is non-interactive.
	 */
	public static String selectRemoteJavaCommand(String javaVersion, String remotePassword) throws Exception {
		if (ValidationHelper.isEmpty(javaVersion))
			throw new IllegalArgumentException("javaVersion is required");
		if (remotePassword == null)
			remotePassword = "";

		return
			"ALT=$(command -v update-alternatives 2>/dev/null || command -v alternatives 2>/dev/null); "
			+ "if [ -z \"$ALT\" ]; then echo 'No update-alternatives/alternatives on remote server' >&2; exit 1; fi; "
			+ "JAVA_BIN=''; "
			+ "for J in $(\"$ALT\" --display java 2>/dev/null | awk '/^\\/.*priority/{print $1}'); do "
			+ "V=$(\"$J\" -version 2>&1 | awk -F'\"' '/ version /{print $2; exit}'); "
			+ "M=$(echo \"$V\" | awk -F. '{if ($1==1) print $2; else print $1}'); "
			+ "if [ \"$M\" = \"" + javaVersion + "\" ]; then JAVA_BIN=\"$J\"; break; fi; "
			+ "done; "
			+ "if [ -z \"$JAVA_BIN\" ]; then echo \"No Java " + javaVersion + " among: $(\"$ALT\" --display java 2>&1 | tr '\\n' ' ')\" >&2; exit 1; fi; "
			+ "echo '" + shellSingleQuote(remotePassword) + "' | sudo -S \"$ALT\" --set java \"$JAVA_BIN\"";
	}

	/**
	 * Remote shell snippet that ensures SPARK_TEMP is exported and the
	 * backing directory exists. Returns an empty string when sparkTempPath
	 * is blank so callers can safely concatenate the result.
	 */
	public static String exportSparkTempCommand(String sparkTempPath) throws Exception {
		if (ValidationHelper.isEmpty(sparkTempPath))
			return "";
		return "export SPARK_TEMP=\"" + sparkTempPath + "\" && mkdir -p \"" + sparkTempPath + "\"";
	}

	/** Escapes a value so it is safe inside a single-quoted shell token. */
	private static String shellSingleQuote(String value) {
		return value.replace("'", "'\\''");
	}
}
