package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by skim on 2/1/17.
 */
public class ReportGenerator {

    private static final String DEFAULT_SEPARATOR = ",";
    private static final String FETCH_FILE = "fetch_LATimes.csv";
    private static final String VISIT_FILE = "visit_LATimes.csv";
    private static final String ALL_FILE = "urls_LATimes.csv";

    public static void main(String[] args) {
        generateReport();
    }

    private static int getFetchSuccess() {
        FileReader fr;
        int count = 0;

        try {
            fr = new FileReader(FETCH_FILE);
            BufferedReader reader = new BufferedReader(fr);
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fetch = line.split(DEFAULT_SEPARATOR);
                int status = Integer.valueOf(fetch[1]);
                if (status >= 200 && status <= 299) {
                    count++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return count;
    }

    private static int getFetchFailed() {
        FileReader fr;
        int count = 0;

        try {
            fr = new FileReader(FETCH_FILE);
            BufferedReader reader = new BufferedReader(fr);
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fetch = line.split(DEFAULT_SEPARATOR);
                int status = Integer.valueOf(fetch[1]);
                if (status >= 300 && status <= 599) {
                    count++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return count;
    }

    private static int getFetchAttempt() {
        return simpleCount(FETCH_FILE);
    }

    private static int getFetchAborted() {
        return getFetchAttempt() - getFetchSuccess() - getFetchFailed();
    }

    private static int getTotalURL() {
        return simpleCount(ALL_FILE);
    }

    private static int simpleCount(String fileName) {
        FileReader fr;
        int count = 0;

        try {
            fr = new FileReader(fileName);
            BufferedReader reader = new BufferedReader(fr);
            while (reader.readLine() != null) {
                count++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return count;

    }

    private static int getUniqueURL() {
        FileReader fr;
        Set<String> urls = new HashSet<String>();

        try {
            fr = new FileReader(ALL_FILE);
            String line;
            BufferedReader reader = new BufferedReader(fr);
            while ((line = reader.readLine()) != null) {
                String[] fetch = line.split(DEFAULT_SEPARATOR);
                urls.add(fetch[0]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return urls.size();
    }

    private static int getSiteWithin() {
        return getSiteRegion("OK");
    }

    private static int getSiteOutside() {
        return getSiteRegion("N_OK");
    }

    private static int getSiteRegion(String reg) {
        FileReader fr;
        Map<String, String> urls = new HashMap<String, String>();

        try {
            fr = new FileReader(ALL_FILE);
            String line;
            BufferedReader reader = new BufferedReader(fr);
            while ((line = reader.readLine()) != null) {
                String[] fetch = line.split(DEFAULT_SEPARATOR);
                if (!urls.containsKey(fetch[0])) {
                    urls.put(fetch[0], fetch[1]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        int count = 0;
        for (String region : urls.values()) {
            if (reg.equals(region)) {
                count++;
            }
        }

        return count;
    }

    private static int getFileSize(int low, int high) {
        FileReader fr;
        int count = 0;

        try {
            fr = new FileReader(VISIT_FILE);
            String line;
            BufferedReader reader = new BufferedReader(fr);
            while ((line = reader.readLine()) != null) {
                String[] fetch = line.split(DEFAULT_SEPARATOR);
                if (Integer.valueOf(fetch[1]) >= low && Integer.valueOf(fetch[1]) < high) {
                    count++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return count;
    }

    private static Map<String, Integer> getStatusMap() {
        FileReader fr;
        Map<String,Integer> map = new HashMap<String, Integer>();

        try {
            fr = new FileReader(FETCH_FILE);
            String line;
            BufferedReader reader = new BufferedReader(fr);
            while ((line = reader.readLine()) != null) {
                String[] fetch = line.split(DEFAULT_SEPARATOR);
                if (!map.containsKey(fetch[1])) {
                    map.put(fetch[1], 1);
                } else {
                    map.put(fetch[1], map.get(fetch[1]) + 1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }

    private static Map<String, Integer> getContentTypeMap() {
        FileReader fr;
        Map<String,Integer> map = new HashMap<String, Integer>();

        try {
            fr = new FileReader(VISIT_FILE);
            String line;
            BufferedReader reader = new BufferedReader(fr);
            while ((line = reader.readLine()) != null) {
                String[] fetch = line.split(DEFAULT_SEPARATOR);
                String[] keys = fetch[3].split("(/)|(;)|( )");
                String key = keys[0].toLowerCase() + "/" + keys[1].toLowerCase();
                if (!map.containsKey(key)) {
                    map.put(key, 1);
                } else {
                    map.put(key, map.get(key) + 1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }

    private static void generateReport() {
        try {
            FileWriter fw = new FileWriter("CrawlReport_LATimes.txt", false);
            BufferedWriter writer = new BufferedWriter(fw);

            writer.write("Name: Kim, Sang Hyun");
            writer.newLine();
            writer.write("USC ID: 5541 7777 08");
            writer.newLine();
            writer.write("News site crawled: latimes.com");
            writer.newLine();
            writer.newLine();

            writer.write("Fetch Statistics:");
            writer.newLine();
            writer.write("================");
            writer.newLine();
            writer.write("# fetches attempted: " + getFetchAttempt());
            writer.newLine();
            writer.write("# fetches succeeded: " + getFetchSuccess());
            writer.newLine();
            writer.write("# fetches aborted: " + getFetchAborted());
            writer.newLine();
            writer.write("# fetches failed: " + getFetchFailed());
            writer.newLine();
            writer.newLine();

            writer.write("Outgoing URLs:");
            writer.newLine();
            writer.write("==============");
            writer.newLine();
            writer.write("# total URLs extracted: " + getTotalURL());
            writer.newLine();
            writer.write("# unique URLs extracted: " + getUniqueURL());
            writer.newLine();
            writer.write("# unique URLs within News Site: " + getSiteWithin());
            writer.newLine();
            writer.write("# unique URLs outside News Site: " + getSiteOutside());
            writer.newLine();
            writer.newLine();

            writer.write("Status Codes:");
            writer.newLine();
            writer.write("=============");
            writer.newLine();
            for (Map.Entry<String, Integer> entry : getStatusMap().entrySet()) {
                writer.write("# " + entry.getKey() + ": " + entry.getValue());
                writer.newLine();
            }
            writer.newLine();
            writer.newLine();

            writer.write("File Sizes:");
            writer.newLine();
            writer.write("===========");
            writer.newLine();
            writer.write("< 1KB: " + getFileSize(0, 1024));
            writer.newLine();
            writer.write("1KB ~ <10KB: " + getFileSize(1024, 10 * 1024));
            writer.newLine();
            writer.write("10KB ~ <100KB: " + getFileSize(10 * 1024, 100 * 1024));
            writer.newLine();
            writer.write("100KB ~ <1MB: " + getFileSize(100 * 1024, 1024 * 1024));
            writer.newLine();
            writer.write(">= 1MB: " + getFileSize(1024 * 1024, Integer.MAX_VALUE));
            writer.newLine();
            writer.newLine();

            writer.write("Content Types:");
            writer.newLine();
            writer.write("==============");
            writer.newLine();
            for (Map.Entry<String, Integer> entry : getContentTypeMap().entrySet()) {
                writer.write("# " + entry.getKey() + ": " + entry.getValue());
                writer.newLine();
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
