package com.theoryinpractise.frege;

import org.apache.commons.exec.*;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractFregeCompileMojo extends AbstractMojo {

    @Parameter(defaultValue = "false")
    protected Boolean hints;

    @Parameter(defaultValue = "false")
    protected Boolean verbose;

    @Parameter(defaultValue = "true")
    protected Boolean inline;

    @Parameter(defaultValue = "true")
    protected Boolean make;

    @Parameter(defaultValue = "false")
    protected Boolean skipCompile;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        try {
            File outputDir = getOutputDirectory().getCanonicalFile();
            if (!outputDir.exists()) {
                Files.createDirectories(outputDir.toPath());
            }
        } catch (IOException e) {
            throw new MojoFailureException(e.getMessage(), e);
        }

        CommandLine cl = new CommandLine("java");

        String cp = "";
        for (Object classpathElement : getClassPathElements()) {
            cp = cp + File.pathSeparator + classpathElement;
        }
        cl.addArgument("-cp").addArgument(cp);

        cl.addArgument("frege.compiler.Main");
        cl.addArgument("-sp").addArgument(getSourceDirectory().getAbsolutePath());

        // output dir
        cl.addArgument("-d").addArgument(getOutputDirectory().getAbsolutePath());

        if (hints) {
            cl.addArgument("-hints");
        }
        if (inline) {
            cl.addArgument("-inline");
        }
        if (make) {
            cl.addArgument("-make");
        }
        if (verbose) {
            cl.addArgument("-v");
        }
        if (skipCompile) {
            cl.addArgument("-j");
        }

        // source files
        for (String sourceFile : getSourceFiles()) {
            cl.addArgument(new File(getSourceDirectory(), sourceFile).getAbsolutePath());
        }

        getLog().debug("Command line: " + cl.toString());

        Executor exec = new DefaultExecutor();
        Map<String, String> env = new HashMap<String, String>(System.getenv());
        ExecuteStreamHandler handler = new PumpStreamHandler(System.out, System.err, System.in);
        exec.setStreamHandler(handler);

        int status;
        try {
            status = exec.execute(cl, env);
        } catch (ExecuteException e) {
            status = e.getExitValue();
        } catch (IOException e) {
            status = 1;
        }

        if (status != 0) {
            throw new MojoExecutionException("Frege compilation failed.");
        }

    }

    public abstract String[] getSourceFiles();

    public abstract File getSourceDirectory();

    public abstract File getOutputDirectory();

    public abstract List<String> getClassPathElements();
}
