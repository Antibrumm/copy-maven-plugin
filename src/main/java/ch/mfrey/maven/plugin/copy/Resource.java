package ch.mfrey.maven.plugin.copy;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.model.FileSet;
import org.apache.maven.plugins.annotations.Parameter;

public class Resource extends FileSet {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Parameter(defaultValue = "UTF-8")
    private String charset;

    @Parameter(defaultValue = "false")
    private boolean move;

    @Parameter(required = false)
    private List<Replace> paths;

    @Parameter(required = false)
    private List<Replace> replaces;

    @Parameter(defaultValue = "false")
    private boolean workOnFullPath;

    public Resource addPath(Replace val) {
        getPaths().add(val);
        return this;
    }

    public Resource addReplace(Replace val) {
        getReplaces().add(val);
        return this;
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

    public void setWorkOnFullPath(final boolean workOnFullPath) {
        this.workOnFullPath = workOnFullPath;
    }

    public List<Replace> getReplaces() {
        if (replaces == null) {
            replaces = new ArrayList<Replace>();
        }
        return replaces;
    }

    public void setReplaces(List<Replace> replaces) {
        this.replaces = replaces;
    }

    public List<Replace> getPaths() {
        if (paths == null) {
            paths = new ArrayList<Replace>();
        }
        return paths;
    }

    public void setPaths(List<Replace> paths) {
        this.paths = paths;
    }

    public String getCharset() {
        if (charset == null) {
            charset = "UTF-8";
        }
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

}