package ch.mfrey.maven.plugin.copy;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

@Mojo(name = "copy", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class CopyMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project.build.directory}", readonly = true)
    private String defaultDir;

    @Component
    private MavenProject project;

    @Parameter
    private Resource[] resources;

    @Parameter(defaultValue = "false")
    private boolean showfiles;

    private void cleanupEmptyDirs(final File directory) throws IOException {
        if (directory.isDirectory()) {
            for (File file : directory.listFiles()) {
                if (file.isDirectory()) {
                    cleanupEmptyDirs(file);
                }
            }
            if (directory.list().length == 0) {
                FileUtils.deleteDirectory(directory);
            }
        }
    }

    private void copyFile(final Resource resource, final File srcFile, final File destFile) throws IOException {
        List<Replace> replaces = resource.getReplaces();
        if (replaces.isEmpty()) {
            if (srcFile.getAbsolutePath().equals(destFile.getAbsolutePath())) {
                return;
            } else if (resource.isMove()) {
                FileUtils.moveFile(srcFile, destFile);
            } else {
                FileUtils.copyFile(srcFile, destFile);
            }
        } else {
            String content = FileUtils.readFileToString(srcFile, resource.getCharset());
            for (Replace replace : replaces) {
                content = content.replace(replace.getFrom(), replace.getTo());
            }
            FileUtils.writeStringToFile(destFile, content, resource.getCharset());
            if (resource.isMove() && !srcFile.getAbsolutePath().equals(destFile.getAbsolutePath())) {
                FileUtils.deleteQuietly(srcFile);
            }
        }
    }

    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            for (Resource resource : getResources()) {

                File workingDir = new File(resource.getDirectory() == null ? defaultDir : resource.getDirectory());
                if (!workingDir.isAbsolute()) {
                    workingDir = new File(project.getBasedir(), resource.getDirectory() == null ? defaultDir
                            : resource.getDirectory());
                }
                if (isShowfiles() && getLog().isInfoEnabled()) {
                    logResource(resource, workingDir);
                }
                for (File srcFile : getFiles(workingDir, resource)) {
                    String destPath = getNewPath(resource, workingDir, srcFile);
                    File destFile = new File(destPath);
                    if (isShowfiles() && getLog().isInfoEnabled()) {
                        StringBuilder sb = new StringBuilder();
                        sb.append(resource.isMove() ? " mv " : " cp ").append(srcFile.getAbsolutePath())
                                .append(System.lineSeparator());
                        sb.append("        -> ").append(destFile.getAbsolutePath());
                        getLog().info(sb);
                    }
                    copyFile(resource, srcFile, destFile);
                }
                try {
                    cleanupEmptyDirs(workingDir);
                } catch (IOException ex) {
                    throw new MojoExecutionException("Could not cleanup empty directories", ex);
                }
            }
        } catch (IOException ex) {
            throw new MojoExecutionException("Could not rename file", ex);
        }

    }

    private void logResource(Resource resource, File workingDir) {
        StringBuilder sb = new StringBuilder();
        sb.append("----------").append(System.lineSeparator());
        sb.append("      WorkingDir: ").append(workingDir.getAbsolutePath()).append(System.lineSeparator());
        sb.append("         Charset: ").append(resource.getCharset()).append(System.lineSeparator());
        sb.append("            Move: ").append(resource.isMove()).append(System.lineSeparator());
        sb.append("  WorkOnFullPath: ").append(resource.isWorkOnFullPath()).append(System.lineSeparator());
        sb.append("        Includes:").append(System.lineSeparator());
        for (String include : resource.getIncludes()) {
            sb.append("                  ").append(include).append(System.lineSeparator());
        }
        sb.append("        Excludes:").append(System.lineSeparator());
        for (String exclude : resource.getExcludes()) {
            sb.append("                  ").append(exclude).append(System.lineSeparator());
        }
        sb.append("          Paths:").append(System.lineSeparator());
        for (Replace r : resource.getPaths()) {
            sb.append("                  ").append(r.getFrom()).append(" -> ").append(r.getTo())
                    .append(System.lineSeparator());
        }
        sb.append("        Replaces:").append(System.lineSeparator());
        for (Replace r : resource.getReplaces()) {
            sb.append("                  ").append(r.getFrom()).append(" -> ").append(r.getTo())
                    .append(System.lineSeparator());
        }
        getLog().info(sb);
    }

    @SuppressWarnings("unchecked")
    public List<File> getFiles(final File workingDir, final Resource resource) throws MojoExecutionException {
        try {
            String includes = !resource.getIncludes().isEmpty() ? String.join(",", resource.getIncludes()) : null;
            String excludes = !resource.getExcludes().isEmpty() ? String.join(",", resource.getExcludes()) : null;
            return org.codehaus.plexus.util.FileUtils.getFiles(workingDir, includes, excludes);
        } catch (IOException e) {
            throw new MojoExecutionException("Unable to get paths to reprots", e);
        }
    }

    private String getNewPath(final Resource resource, final File workingDir, final File file)
            throws MojoExecutionException {
        List<Replace> renames = resource.getPaths();
        if (renames.isEmpty()) {
            return file.getAbsolutePath();
        }
        String path = resource.isWorkOnFullPath() ? file.getAbsolutePath() : file.getAbsolutePath().substring(
                workingDir.getAbsolutePath().length());
        for (Replace rename : renames) {
            if (rename.getFrom() == null || rename.getTo() == null) {
                throw new MojoExecutionException("From and To cannot be NULL: " + rename);
            } else if (!rename.getFrom().equals(rename.getTo())) {
                path = path.replace(rename.getFrom(), rename.getTo());
            }
        }
        return resource.isWorkOnFullPath() ? path : workingDir.getAbsolutePath() + path;
    }

    public Resource[] getResources() {
        return resources;
    }

    public boolean isShowfiles() {
        return showfiles;
    }

    public void setResources(final Resource[] resources) {
        this.resources = resources;
    }

    public void setShowfiles(final boolean showfiles) {
        this.showfiles = showfiles;
    }
}