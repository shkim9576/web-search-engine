package util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import model.All;
import model.Fetch;
import model.Visit;

/**
 * Created by skim on 2/1/17.
 */
public class CSVUtil {

    private static final char DEFAULT_SEPARATOR = ',';
    private static final String FETCH_FILE = "fetch_LATimes.csv";
    private static final String VISIT_FILE = "visit_LATimes.csv";
    private static final String ALL_FILE = "urls_LATimes.csv";

    public static void reset() throws IOException {
        FileWriter w1 = new FileWriter(FETCH_FILE, false);
        FileWriter w2 = new FileWriter(VISIT_FILE, false);
        FileWriter w3 = new FileWriter(ALL_FILE, false);
        w1.flush();
        w2.flush();
        w3.flush();
        w1.close();
        w2.close();
        w3.close();
    }

    public static void writeToFile(Fetch fetch) {
        List<String> list = new ArrayList<String>();
        list.add(fetch.getUrl());
        list.add(String.valueOf(fetch.getStatus()));

        write(FETCH_FILE, list);
    }

    public static void writeToFile(Visit visit) {
        List<String> list = new ArrayList<String>();
        list.add(visit.getUrl());
        list.add(String.valueOf(visit.getFileSize()));
        list.add(String.valueOf(visit.getOutLinkCount()));
        list.add(visit.getContentType());

        write(VISIT_FILE, list);
    }

    public static void writeToFile(All all) {
        List<String> list = new ArrayList<String>();
        list.add(all.getUrl());
        list.add(all.getStatus().name());

        write(ALL_FILE, list);
    }

    private static void write(String fn, List list) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(fn, true);
            CSVUtil.writeLine(writer, list);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void writeLine(FileWriter w, List<String> values) throws IOException {
        writeLine(w, values, DEFAULT_SEPARATOR, ' ');
    }

    private static void writeLine(Writer w, List<String> values, char separators, char customQuote) throws IOException {

        boolean first = true;

        //default customQuote is empty

        if (separators == ' ') {
            separators = DEFAULT_SEPARATOR;
        }

        StringBuilder sb = new StringBuilder();
        for (String value : values) {
            if (!first) {
                sb.append(separators);
            }
            if (customQuote == ' ') {
                sb.append(followCVSformat(value));
            } else {
                sb.append(customQuote).append(followCVSformat(value)).append(customQuote);
            }

            first = false;
        }
        sb.append("\n");
        w.append(sb.toString());
    }

    private static String followCVSformat(String value) {

        String result = value;
        if (result.contains("\"")) {
            result = result.replace("\"", "\"\"");
        }
        return result;
    }
}
