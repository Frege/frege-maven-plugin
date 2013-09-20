package com.theoryinpractise.frege;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import java.io.File;
import java.util.List;

@Mojo(name = "test-compile", defaultPhase = LifecyclePhase.TEST_COMPILE, requiresDependencyResolution = ResolutionScope.TEST)
public class FregeTestCompileMojo extends AbstractFregeCompileMojo {

    @Parameter(defaultValue = "src/test/frege")
    protected File testSourceDirectory;

    @Parameter
    protected String[] testSourceFiles;

    @Parameter(required = true, defaultValue = "${project.build.directory}/generated-test-sources/frege")
    protected File testOutputDirectory;

    @Parameter(required = true, readonly = true, property = "project.testClasspathElements")
    protected List<String> testClasspathElements;

    public File getOutputDirectory() {
        return testOutputDirectory;
    }

    public File getSourceDirectory() {
        return testSourceDirectory;
    }

    public String[] getSourceFiles() {
        return testSourceFiles;
    }

    public List<String> getClassPathElements() {
        return testClasspathElements;
    }

}
