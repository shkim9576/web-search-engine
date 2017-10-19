package usc.edu;

import org.jsoup.Jsoup;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;

public class App {

    private static final String DATA_PATH = "/Users/skim/Documents/skim/usc/csci572-info/hw5/crawl_data/LATimesDownloadData/";

    public static void main(String[] args) throws IOException, SAXException {
        System.out.println("Generating big.txt file. It will take a few minutes.");
        File dir = new File(DATA_PATH);
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            new File("big.txt").delete();

            for (File file : directoryListing) {
                FileReader reader = new FileReader(DATA_PATH + file.getName());
                String result = extractText(reader);
                writeToFile(result);
            }
        } else {
            System.out.println("Not a valid directory!");
        }
        System.out.println("Program is done.");
    }

    public static String extractText(Reader reader) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(reader);
        String line;
        while ( (line=br.readLine()) != null) {
            sb.append(line);
        }
        String textOnly = Jsoup.parse(sb.toString()).text();
        return textOnly;
    }

    private static void writeToFile(String content) {
        BufferedWriter bw = null;
        FileWriter fw = null;

        try {
            fw = new FileWriter("big.txt", true);
            bw = new BufferedWriter(fw);
            bw.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bw != null) bw.close();
                if (fw != null) fw.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
