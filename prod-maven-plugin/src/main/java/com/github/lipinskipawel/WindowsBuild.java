package com.github.lipinskipawel;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Mojo(name = "windows")
public class WindowsBuild extends AbstractMojo {

    @Parameter(property = "linux.builtFinalNameOfJar", required = true)
    private String builtFinalNameOfJar;

    @Parameter(property = "linux.buildInstallerDirectory", required = true)
    private String directory;

    @Parameter(property = "linux.jdkOfNeed", required = true)
    private File jdkOfNeed;


    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        final var jarInTarget = findJarInTarget(builtFinalNameOfJar);
        final var linuxPathJdk = findLinuxJdk(jdkOfNeed.toPath());

        final var directory = createInstallerDirectory(this.directory);
        createExecutableFile(directory, "play.bat", linuxPathJdk.getFileName().toString());

        copyJdkAndJar(directory, jarInTarget, linuxPathJdk);
    }

    private void copyJdkAndJar(final Path destination,
                               final Path jarInTarget,
                               final Path jdk) {
        try {
//            printInfo(destination.getFileName().toAbsolutePath().toString());
//            printInfo(jdk.getFileName().toString());
//            printInfo(destination.resolve(jdk.getFileName()).toAbsolutePath().toString());

            Files.copy(jarInTarget, destination.resolve(jarInTarget.getFileName()));
            FileUtils.copyDirectory(jdk.toFile(), destination.resolve(jdk.getFileName()).toFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Path findLinuxJdk(final Path pathToJdk) {
        return pathToJdk;
    }

    private void createExecutableFile(final Path destination,
                                      final String name,
                                      final String jdkPrefixToBinary) throws MojoExecutionException {
        try {
            final var executable = Files.createFile(destination.resolve(name));
            Files.writeString(executable, "start " +
                    jdkPrefixToBinary + "/bin/javaw " +
                    "-jar --enable-preview football-game.jar\n\n");
        } catch (IOException e) {
            throw new MojoExecutionException("Not able to create " + name + " file");
        }
    }

    private Path createInstallerDirectory(final String directoryName) throws MojoExecutionException {
        try {
            return Files.createDirectory(Paths.get(directoryName));
        } catch (IOException e) {
            throw new MojoExecutionException("Not able to create " + directoryName + " directory");
        }
    }

    private Path findJarInTarget(final String applicationName) throws MojoExecutionException {
        try {
            return Files
                    .find(
                            Paths.get("target").toAbsolutePath(),
                            1,
                            ((path, basicFileAttributes) -> path.toFile().getName().endsWith(".jar")),
                            FileVisitOption.FOLLOW_LINKS
                    )
                    .filter(path -> path.getFileName().toString().startsWith(applicationName))
                    .findFirst()
                    .orElseThrow(
                            () -> new MojoExecutionException(
                                    "Not able to find " + applicationName + ".jar in the target directory"
                            )
                    );
        } catch (IOException e) {
            throw new MojoExecutionException("Not able to find " + applicationName + ".jar in the target directory");
        }
    }

    private void printInfo(final String message) {
        getLog().info(message);
    }
}
