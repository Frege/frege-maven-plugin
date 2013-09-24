package com.theoryinpractise.frege;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteStreamHandler;
import org.apache.commons.exec.Executor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.compiler.util.scan.InclusionScanException;
import org.codehaus.plexus.compiler.util.scan.SimpleSourceInclusionScanner;
import org.codehaus.plexus.compiler.util.scan.SourceInclusionScanner;
import org.codehaus.plexus.compiler.util.scan.StaleSourceScanner;
import org.codehaus.plexus.compiler.util.scan.mapping.SourceMapping;
import org.codehaus.plexus.compiler.util.scan.mapping.SuffixMapping;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class AbstractFregeCompileMojo extends AbstractMojo {

    @Parameter(required = true, readonly = true, property = "project")
    protected MavenProject project;

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

    @Parameter(defaultValue = "false")
    protected boolean includeStale;

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
        cp = cp + File.pathSeparator + getOutputDirectory().getAbsolutePath();

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
        final Set<File> sourceFiles = getSourceFiles();

        if (sourceFiles.isEmpty()) {
            getLog().info("No files to compile, skipping...");
        } else {

            for (File sourceFile : sourceFiles) {
                cl.addArgument(sourceFile.getAbsolutePath());
            }

            getLog().debug("Command line: " + cl.toString());

            Executor exec = new DefaultExecutor();
            Map<String, String> env = new HashMap<>(System.getenv());
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

    }

    protected Set<File> discoverSourceFiles(File sourceDirectory) throws MojoExecutionException {

        if (!sourceDirectory.exists()) return Collections.EMPTY_SET;

        SourceInclusionScanner scanner = getSourceInclusionScanner(includeStale);

        SourceMapping mapping = new SuffixMapping(".fr", new HashSet(Arrays.asList(".java")));

        scanner.addSourceMapping(mapping);

        final Set<File> sourceFiles;
        try {
            sourceFiles = scanner.getIncludedSources(sourceDirectory, getOutputDirectory());
        } catch (InclusionScanException e) {
            throw new MojoExecutionException("Error scanning source path: \'" + sourceDirectory.getPath() + "\' " + "for  files to recompile.", e);
        }

        return sourceFiles;
    }

    protected SourceInclusionScanner getSourceInclusionScanner(boolean includeStale) {
        return includeStale
                ? new SimpleSourceInclusionScanner(Collections.singleton("**/*"), Collections.EMPTY_SET)
                : new StaleSourceScanner(1024);
    }

    public Set<File> getSourceFiles() throws MojoExecutionException {
        return discoverSourceFiles(getSourceDirectory());
    }

    public abstract File getSourceDirectory();

    public abstract File getOutputDirectory();

    public abstract List<String> getClassPathElements();
}
