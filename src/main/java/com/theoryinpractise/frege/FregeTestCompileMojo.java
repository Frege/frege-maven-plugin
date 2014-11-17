package com.theoryinpractise.frege;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Mojo(name = "test-compile", defaultPhase = LifecyclePhase.TEST_COMPILE, requiresDependencyResolution = ResolutionScope.TEST)
public class FregeTestCompileMojo extends AbstractFregeCompileMojo {

    @Parameter(defaultValue = "src/test/frege")
    protected File testSourceDirectory;

    @Parameter(required = true, defaultValue = "${project.build.directory}/generated-test-sources")
    protected File generatedTestSourcesDirectory;

    @Parameter(required = true, defaultValue = "${project.build.directory}/generated-test-sources/frege")
    protected File testOutputDirectory;

    @Parameter(required = true, readonly = true, property = "project.testClasspathElements")
    protected List<String> testClasspathElements;

    public File getOutputDirectory() {
        return testOutputDirectory;
    }

    public List<String> getClassPathElements() {
        return testClasspathElements;
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        project.addTestCompileSourceRoot(testOutputDirectory.getAbsolutePath());
        super.execute();
    }

    @Override
    public List<File> getAllSourceDirectories() {
        List<File> sourceDirectories = new ArrayList<>();
        for (File file : generatedTestSourcesDirectory.listFiles()) {
            if (file.isDirectory()) {
                sourceDirectories.add(file);
            }
        }
        if (testSourceDirectory.exists()) {
            sourceDirectories.add(testSourceDirectory);
        }
        return sourceDirectories;
    }


}
