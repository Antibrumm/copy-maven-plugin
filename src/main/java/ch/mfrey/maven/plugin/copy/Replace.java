package ch.mfrey.maven.plugin.copy;

import org.apache.maven.plugins.annotations.Parameter;

public class Replace {

    @Parameter(required = true)
    private String from;

    @Parameter(required = false, defaultValue = "false")
    private boolean regex;

    @Parameter(required = true)
    private String to;

    public Replace() {
        super();
    }

    public Replace(final String from, final String to) {
        super();
        this.from = from;
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public boolean isRegex() {
        return regex;
    }

    public void setFrom(final String from) {
        this.from = from;
    }

    public void setRegex(final boolean regex) {
        this.regex = regex;
    }

    public void setTo(final String to) {
        this.to = to;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Replace [from=").append(from).append(", to=").append(to).append(", regex=").append(regex)
                .append("]");
        return builder.toString();
    }

}