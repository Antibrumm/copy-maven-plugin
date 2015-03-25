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

    @Parameter(required = false)
    private String id;

    @Parameter(defaultValue = "false")
    private boolean move = false;

    @Parameter(defaultValue = "true")
    private boolean normalizePath = true;

    @Parameter(required = false)
    private List<Replace> paths;

    @Parameter(required = false)
    private List<Replace> replaces;

    @Parameter(defaultValue = "false")
    private boolean workOnFullPath = false;

    public Resource addPath(final Replace val) {
        getPaths().add(val);
        return this;
    }

    public Resource addReplace(final Replace val) {
        getReplaces().add(val);
        return this;
    }

    public String getCharset() {
        if (charset == null) {
            charset = "UTF-8";
        }
        return charset;
    }

    public String getId() {
        if (id == null) {
            id = "undefined";
        }
        return id;
    }

    public List<Replace> getPaths() {
        if (paths == null) {
            paths = new ArrayList<Replace>();
        }
        return paths;
    }

    public List<Replace> getReplaces() {
        if (replaces == null) {
            replaces = new ArrayList<Replace>();
        }
        return replaces;
    }

    public boolean isMove() {
        return move;
    }

    public boolean isNormalizePath() {
        return normalizePath;
    }

    public boolean isWorkOnFullPath() {
        return workOnFullPath;
    }

    public void setCharset(final String charset) {
        this.charset = charset;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public void setMove(final boolean move) {
        this.move = move;
    }

    public void setNormalizePath(final boolean normalizePath) {
        this.normalizePath = normalizePath;
    }

    public void setPaths(final List<Replace> paths) {
        this.paths = paths;
    }

    public void setReplaces(final List<Replace> replaces) {
        this.replaces = replaces;
    }

    public void setWorkOnFullPath(final boolean workOnFullPath) {
        this.workOnFullPath = workOnFullPath;
    }

}