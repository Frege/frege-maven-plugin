package com.theoryinpractise.frege;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import java.io.File;
import java.util.List;

@Mojo(name = "compile", defaultPhase = LifecyclePhase.COMPILE, requiresDependencyResolution = ResolutionScope.COMPILE)
public class FregeCompileMojo extends AbstractFregeCompileMojo {

    @Parameter(defaultValue = "src/main/frege")
    protected File sourceDirectory;

    @Parameter
    protected String[] sourceFiles;

    @Parameter(required = true, defaultValue = "${project.build.directory}/generated-sources/frege")
    protected File outputDirectory;

    @Parameter(required = true, readonly = true, property = "project.compileClasspathElements")
    protected List<String> classpathElements;

    public File getOutputDirectory() {
        return outputDirectory;
    }

    public File getSourceDirectory() {
        return sourceDirectory;
    }

    public String[] getSourceFiles() {
        return sourceFiles;
    }

    public List<String> getClassPathElements() {
        return classpathElements;
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        project.addCompileSourceRoot(outputDirectory.getAbsolutePath());
        super.execute();
    }
}
