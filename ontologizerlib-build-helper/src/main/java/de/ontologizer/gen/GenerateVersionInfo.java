package de.ontologizer.gen;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import com.beust.jcommander.JCommander;

/**
 * A simple program to generate the version info.
 *
 * @author Sebastian Bauer
 */
public class GenerateVersionInfo
{
	public static void main(String[] args) throws IOException
	{
		GenerateVersionInfoCLIConfig cliConfig = new GenerateVersionInfoCLIConfig();
		JCommander jc = new JCommander(cliConfig);
		jc.parse(args);
		jc.setProgramName(GenerateVersionInfo.class.getSimpleName());

		if (cliConfig.help)
		{
			jc.usage();
			System.exit(0);
		}

		File output = new File(cliConfig.output);
		if (output.exists() && !cliConfig.force)
		{
			System.err.println("Specified file \"" + cliConfig.output + "\" exists. Aborting.");
			System.exit(1);
		}

		File parent = output.getParentFile();
		if (!parent.exists())
		{
			parent.mkdirs();
		}

		String previousContents = null;
		if (output.exists())
		{
			/* If output exists, read it contents so we can determine if the actual contents
			 * has changed. Only if it is changed we will write it. If not, the previous time
			 * stamp is kept. */
			previousContents = new String(Files.readAllBytes(output.toPath()), StandardCharsets.UTF_8);
		}

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos, false, "UTF-8");
		ps.println("/* Auto-generated file. Please don't edit! */");
		ps.println();
		ps.println("package " + cliConfig.javaPackage + ";");
		ps.println();
		ps.println("public final class VersionInfo");
		ps.println("{");
		ps.println("\t/**");
		ps.print("\t * @return the version of this module. ");
		ps.println("Includes <code>-SNAPSHOT</code> suffix on snapshot builds.");
		ps.println("\t */");
		ps.println("\tpublic static String getVersion()");
		ps.println("\t{");
		ps.println("\t\treturn \"" + cliConfig.version + "\";");
		ps.println("\t}");
		ps.println("}");
		ps.close();

		String newContents = new String(baos.toByteArray(), StandardCharsets.UTF_8);
		if (previousContents == null || !newContents.equals(previousContents))
		{
			/* If new contents is different from old one, write it */
			OutputStream out = new FileOutputStream(output);
			out.write(baos.toByteArray());
			out.close();
		}
	}
}
