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

    public static Replace asRegex(final String from, final String to) {
        return new Replace().setFrom(from).setTo(to).setRegex(true);
    }

    public static Replace asReplace(final String from, final String to) {
        return new Replace().setFrom(from).setTo(to);
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

    public Replace setFrom(final String from) {
        this.from = from;
        return this;
    }

    public Replace setRegex(final boolean regex) {
        this.regex = regex;
        return this;
    }

    public Replace setTo(final String to) {
        this.to = to;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Replace [from=").append(from).append(", to=").append(to).append(", regex=").append(regex)
                .append("]");
        return builder.toString();
    }

}