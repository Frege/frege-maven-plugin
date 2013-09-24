frege-maven-plugin
==================

`frege-maven-plugin` is a basic plugin for the Apache Maven build tool for compiling [frege-lang](http://www.frege-lang.org/) source code into java and adding it to the maven build chain.          
           
    <plugin>
      <groupId>com.theoryinpractise.frege</groupId>
      <artifactId>frege-maven-plugin</artifactId>
      <version>${frege.plugin.version}</version>
      <executions>
        <execution>
          <id>compile</id>
          <phase>generate-sources</phase>
          <goals>
            <goal>compile</goal>
          </goals>
        </execution>
        <execution>
          <id>test-compile</id>
          <phase>generate-test-sources</phase>
          <goals>
            <goal>test-compile</goal>
          </goals>
        </execution>
      </executions>
      <configuration>
        <inline>true</inline>
        <hints>true</hints>
        <sourceDirectory>src/main/frege</sourceDirectory>
        <testSourceDirectory>src/test/frege</testSourceDirectory>
        <outputDirectory>target/generated-sources/frege</outputDirectory>
        <testOutputDirectory>target/generated-test-sources/frege</testOutputDirectory>
      </configuration>
    </plugin>

For an example project, please check out [frege-testing](https://github.com/talios/frege-testing).

