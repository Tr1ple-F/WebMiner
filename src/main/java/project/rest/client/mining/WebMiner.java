package project.rest.client.mining;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static project.rest.client.code.Regex.*;
import static project.rest.client.mining.ContentFilters.*;

public class WebMiner {

    public static List<Table> parse(String content) {
        // Get every table and remove confusion
        String[] splitByTables = content.replaceAll(lineBreak, "")
                .replaceAll(tabBreak, "")
                .replaceAll(protectedSpace, " ")
                .replaceAll(breakLine, " ")
                .split("<table");
        // Headlines Map
        Map<String, Integer> headlineMap = new HashMap<>();

        // Parse tables
        List<Table> tables = new ArrayList<>();
        for (int i = 1; i < splitByTables.length; i++) {
            // Parse columns
            List<String> columns = new ArrayList<>();
            Pattern pColumn = Pattern.compile(tableHeaders);
            Matcher mColumn = pColumn.matcher(splitByTables[i]);
            while (mColumn.find()) {
                String column = mColumn.group();
                columns.add(clean(extract(column)));
            }
            if (columns.isEmpty()) {
                continue;
            }

            // Parse rows
            Map<Integer, Map<Integer, String>> rows = new HashMap<>();
            Pattern pRow = Pattern.compile(tableRows);
            Matcher mRow = pRow.matcher(splitByTables[i]);
            int rFI = 0;
            while (mRow.find()) {
                String row = mRow.group();

                // Parse items
                Map<Integer, String> itemMap = rows.containsKey(rFI) ? rows.get(rFI) : new HashMap<>();
                Pattern pItems = Pattern.compile(tableElements);
                Matcher mItems = pItems.matcher(row);
                for (int iFI = 0; iFI < columns.size(); iFI++) {
                    if (!itemMap.containsKey(iFI)) {
                        if (mItems.find()) {
                            String item = mItems.group();
                            if (item.contains("rowspan=")) {
                                // Item stretches multiple rows
                                int rowspan = rowspan(item);
                                if (rowspan > 1) {
                                    for (int rSI = 1; rSI < rowspan; rSI++) {
                                        Map<Integer, String> futureItemRow = rows.containsKey(rFI + rSI) ? rows.get(rFI + rSI) : new HashMap<>();
                                        futureItemRow.put(iFI, extract(item).replace("'", "''"));
                                        rows.put(rFI + rSI, futureItemRow);
                                    }
                                }
                            }
                            itemMap.put(iFI, extract(item).replace("'", "''"));
                        }
                    }
                }
                if (!itemMap.isEmpty()) {
                    rows.put(rFI, itemMap);
                }
                rFI++;
            }

            // Parse Headers
            int j = i - 1;
            String lastHeadline = "noname";
            String headline = "noname";
            boolean exit = false;
            while (j >= 0 && !exit) {
                Pattern p = Pattern.compile(headers);
                Matcher m = p.matcher(splitByTables[j]);
                if (!m.find()) {
                    j--;
                } else {
                    boolean found = true;
                    while (found) {
                        lastHeadline = m.group();
                        found = m.find();
                    }
                    String cleanHL = clean(extract(lastHeadline));
                    if (headlineMap.containsKey(cleanHL)) {
                        int usages = headlineMap.get(cleanHL);
                        if (usages == 1) {
                            for (Table targetTable : tables) {
                                if (targetTable.getName().equalsIgnoreCase(cleanHL)) {
                                    targetTable.setName(cleanHL + 1);
                                }
                            }
                        }
                        headlineMap.put(cleanHL, usages + 1);
                        headline = cleanHL + (usages + 1);
                    } else {
                        headlineMap.put(cleanHL, 1);
                        headline = cleanHL;
                    }
                    exit = true;
                }
            }

            // Stream to Lists
            List<List<String>> rowList = rows.values().stream().map((map) -> new ArrayList<>(map.values())).collect(Collectors.toList());

            // Type Map
            Map<Integer, String> typeMap = new HashMap<>();

            for (int k = 0; k < columns.size(); k++) {
                boolean exitInteger = false;
                boolean exitDouble = false;
                // Test for Integer
                for (int l = 0; l < rowList.size() && !exitInteger; l++) {
                    try {
                        // Use comma for thousand notation
                        Integer.parseInt(rowList.get(l).get(k).replace(",", ""));
                    } catch (Exception e) {
                        exitInteger = true;
                    }
                }
                // Test for Double
                for (int l = 0; l < rowList.size() && !exitDouble; l++) {
                    try {
                        // Use comma for thousand notation
                        Double.parseDouble(rowList.get(l).get(k).replace(",", ""));
                    } catch (Exception e) {
                        exitDouble = true;
                    }
                }
                if (exitDouble && exitInteger) {
                    typeMap.put(k, "VARCHAR(600)");
                } else if (!exitInteger) {
                    typeMap.put(k, "INTEGER");
                } else {
                    typeMap.put(k, "DOUBLE");
                }
            }

            Table t = new Table(headline, columns, rowList, typeMap);
            tables.add(t);
        }
        return tables;
    }

}
