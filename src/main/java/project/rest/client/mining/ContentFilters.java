package project.rest.client.mining;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static project.rest.client.code.Regex.*;

public class ContentFilters {

    public static String extract(String target) {
        // Remove all tags
        String noEdit = target.replaceAll(editSection, "");
        String noCite = noEdit.replaceAll(citations, "");
        return noCite.replaceAll(allTags, "");
    }

    public static int rowspan(String target) {
        // Get rowspan
        Pattern pRS = Pattern.compile(rowSpan);
        Matcher mRS = pRS.matcher(target);
        while (mRS.find()) {
            String rowspanElement = mRS.group();
            return Integer.parseInt(rowspanElement.replace("rowspan=\"", "").replace("\"", ""));
        }
        return 0;
    }

    public static String clean(String target) {
        // Clean content
        String preClean = target.replaceAll(hashtag, "no").replaceAll(bannedCharacters, "_").replaceAll(multiUnderscores, "_");
        if (preClean.endsWith("_")) {
            // Remove _ if at end
            return preClean.substring(0, preClean.length() - 1);
        }
        return preClean;
    }

}
