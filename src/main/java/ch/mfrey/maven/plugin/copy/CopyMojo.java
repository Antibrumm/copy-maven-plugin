package ch.mfrey.maven.plugin.copy;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.maven.model.FileSet;
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

    public static class Replace {
        @Parameter(required = true)
        private String from;

        @Parameter(required = true)
        private String to;

        public String getFrom() {
            return from;
        }

        public String getTo() {
            return to;
        }

        public void setFrom(final String from) {
            this.from = from;
        }

        public void setTo(final String to) {
            this.to = to;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("Replace [from=").append(from).append(", to=").append(to).append("]");
            return builder.toString();
        }
    }

    public static class Resource extends FileSet {

        /**
         *
         */
        private static final long serialVersionUID = 1L;

        @Parameter(defaultValue = "false")
        private boolean move;

        @Parameter(required = false)
        private Replace[] paths;

        @Parameter(required = false)
        private Replace[] replaces;

        @Parameter(defaultValue = "false")
        private boolean workOnFullPath;

        public Replace[] getPaths() {
            return paths == null ? new Replace[0] : paths;
        }

        public Replace[] getReplaces() {
            return replaces == null ? new Replace[0] : replaces;
        }

        public boolean isMove() {
            return move;
        }

        public boolean isWorkOnFullPath() {
            return workOnFullPath;
        }

        public void setMove(final boolean move) {
            this.move = move;
        }

        public void setPaths(final Replace[] paths) {
            this.paths = paths;
        }

        public void setReplaces(final Replace[] replaces) {
            this.replaces = replaces;
        }

        public void setWorkOnFullPath(final boolean workOnFullPath) {
            this.workOnFullPath = workOnFullPath;
        }

    }

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
        Replace[] replaces = resource.getReplaces();
        if (replaces.length == 0) {
            if (srcFile.getAbsolutePath().equals(destFile.getAbsolutePath())) {
                return;
            } else if (resource.isMove()) {
                FileUtils.moveFile(srcFile, destFile);
            } else {
                FileUtils.copyFile(srcFile, destFile);
            }
        } else {
            String content = FileUtils.readFileToString(srcFile, "UTF-8");
            for (Replace replace : replaces) {
                content = content.replaceAll(replace.from, replace.to);
            }
            FileUtils.writeStringToFile(destFile, content, "UTF-8");
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
                if (getLog().isInfoEnabled()) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Resource:\n").append("WorkingDir: ").append(workingDir.getAbsolutePath()).append("\n");
                    sb.append("  Paths:\n");
                    for (Replace r : resource.getPaths()) {
                        sb.append("    ").append(r.getFrom()).append(" -> ").append(r.getTo()).append("\n");
                    }
                    sb.append("  Replaces:\n");
                    for (Replace r : resource.getReplaces()) {
                        sb.append("    ").append(r.getFrom()).append(" -> ").append(r.getTo()).append("\n");
                    }
                    getLog().info(sb);
                }
                for (File srcFile : getFiles(workingDir, resource)) {
                    String destPath = getNewPath(resource, workingDir, srcFile);
                    File destFile = new File(destPath);
                    if (isShowfiles() && getLog().isInfoEnabled()) {
                        getLog().info(
                                System.lineSeparator() + " - " + srcFile.getAbsolutePath() + System.lineSeparator()
                                        + " + " + destFile.getAbsolutePath());
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
        Replace[] renames = resource.getPaths();
        if (renames.length == 0) {
            return file.getAbsolutePath();
        }
        String path = resource.isWorkOnFullPath() ? file.getAbsolutePath() : file.getAbsolutePath().substring(
                workingDir.getAbsolutePath().length());
        for (int i = 0; i < renames.length; i++) {
            if (renames[i].getFrom() == null || renames[i].getTo() == null) {
                throw new MojoExecutionException("From and To cannot be NULL: " + renames[i]);
            } else if (!renames[i].getFrom().equals(renames[i].getTo())) {
                path = path.replace(renames[i].getFrom(), renames[i].getTo());
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
