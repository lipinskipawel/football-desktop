package com.github.lipinskipawel;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = "windows")
public class WindowsBuild extends AbstractMojo {

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Windows build");
        getLog().info("TODO");
        getLog().info("start jdk-13.0.1+9-jre/bin/javaw -jar --enable-preview football-game.jar");
    }
}
