/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 *
 * @author s107200
 */
public class Highscore {

    final static String FILE_NAME = "C:\\bridgebuilder\\out.txt";
    final static Charset ENCODING = StandardCharsets.UTF_8;
    private int[] scores;

    public Highscore() throws IOException {
        scores = new int[4];
        readScores();
    }

    public void readScores() throws IOException {
        Path path = Paths.get(FILE_NAME);
        Scanner scanner;
        scanner = new Scanner(path, ENCODING.name());
        String s = scanner.nextLine();
        String s1 = scanner.nextLine();
        String s2 = scanner.nextLine();
        String s3 = scanner.nextLine();
        scores[0] = Integer.parseInt(s);
        scores[1] = Integer.parseInt(s1);
        scores[2] = Integer.parseInt(s2);
        scores[3] = Integer.parseInt(s3);
    }

    public void writeScores() throws IOException {
        FileWriter outFile;
        outFile = new FileWriter(FILE_NAME);
        PrintWriter out = new PrintWriter(outFile);
        out.println(scores[0]);
        out.println(scores[1]);
        out.println(scores[2]);
        out.println(scores[3]);
        out.close();
    }

    public void newScore(int level, int score) throws IOException {
        if (score > scores[level]) {
            scores[level] = score;
            writeScores();
        }
    }

    public int getScore(int x) {
        return scores[x];
    }
}
