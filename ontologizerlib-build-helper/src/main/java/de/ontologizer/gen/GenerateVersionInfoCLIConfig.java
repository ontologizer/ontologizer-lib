package de.ontologizer.gen;

import com.beust.jcommander.Parameter;

public class GenerateVersionInfoCLIConfig
{
	@Parameter(names={"--version"}, required=true, description="The version that should be put into the generated Java file")
	public String version;

	@Parameter(names={"--output"}, required=true, description="Defines the filename of the Java source to be written. Non-existent directories are created.")
	public String output;

	@Parameter(names={"--java-package"}, required=true, description="Defines the name of the package in which the class resides")
	public String javaPackage;

	@Parameter(names={"--force"}, description="Forces the overwritting of the output file if it exists")
	boolean force;

	@Parameter(names={"--help"}, description="Shows this help", help=true)
	boolean help;
}
