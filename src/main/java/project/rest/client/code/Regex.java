package project.rest.client.code;

public class Regex {

    // Regex commands
    public static final String allTags = "<.*?>";
    public static final String multiUnderscores = "__+";
    public static final String hashtag = "#";
    public static final String lineBreak = "\n";
    public static final String tabBreak = "\t";
    public static final String breakLine = "<br.*?>";
    public static final String protectedSpace = "&#160;";
    public static final String rowSpan = "rowspan=\".*?\"";
    public static final String tableElements = "<td.*?>(.*?)<\\/td>";
    public static final String tableRows = "<tr.*?>(.*?)<\\/tr>";
    public static final String tableHeaders = "<th.*?>(.*?)<\\/th>";
    public static final String headers = "<h[0-9].*?>(.*?)<\\/h[0-9]>";
    public static final String bannedCharacters = "[^\\w]";
    // For Wikipedia
    public static final String editSection = "<span class=\"mw-editsection\">.*<\\/span>";
    public static final String citations = "<a href=\"#cite_note.*?<\\/a>";
}
