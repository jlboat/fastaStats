/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.jlboat.fastastats;

import com.github.jlboat.fastautils.Fasta;
import com.github.jlboat.fastautils.FastaUtils;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 *
 * @author lucas
 */
public class main {

    public static void main(String[] args) throws IOException {
        try {
            Options options = new Options();

            options.addOption("o", "output",
                    true, "Output file name (default: stdout)");
            options.addOption("h", "help",
                    false, "Print this help message");
            options.addOption("i", "input",
                    true, "Input FASTA file");

            HelpFormatter formatter = new HelpFormatter();

            CommandLineParser parser = new DefaultParser();

            CommandLine cmd;
            Fasta fasta;
            String output;

            cmd = parser.parse(options, args);
            if (cmd.getOptions().length == 0 || cmd.hasOption("help")) {
                formatter.printHelp("java -jar fastaStats-1.0.jar -i <input> -o <output>",
                        options);
                System.exit(0);
            }
            if (cmd.hasOption("input")) {
                fasta = new Fasta(cmd.getOptionValue("input"), "DNA");
                if (cmd.hasOption("output")) {
                    output = cmd.getOptionValue("output");
                    LinkedHashMap<String, Number> lm = FastaUtils.stats(fasta);
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(output))) {
                        for (Map.Entry<String, Number> en : lm.entrySet()) {
                            writer.write(String.format("%s: %s%n",en.getKey(), 
                                    en.getValue().toString()));                                                 
                        }
                        writer.write(String.format("%nSequenceID: Length%n"));
                        int[] lengths = FastaUtils.seqLengths(fasta);
                        Object[] keys = fasta.getKeys().toArray();
                        for (int i = 0; i < lengths.length; i++) {
                            int length = lengths[i];
                            writer.write(String.format("%s: %d%n",keys[i], length));
                        }
                    }
                } else {
                    LinkedHashMap<String, Number> lm = FastaUtils.stats(fasta);
                    for (Map.Entry<String, Number> en : lm.entrySet()) {
                        System.out.printf("%s: %s%n",en.getKey(), en.getValue());
                        //Number value = en.getValue();                       
                    }
                    System.out.println("");
                    System.out.println("SequenceID: Length");
                    int[] lengths = FastaUtils.seqLengths(fasta);
                    Object[] keys = fasta.getKeys().toArray();
                    for (int i = 0; i < lengths.length; i++) {
                        int length = lengths[i];
                        System.out.printf("%s: %d%n",keys[i], length);
                    }
                    
                }
            } else {
                System.err.println("Input parameter ('i','input') required.");
                formatter.printHelp("java -jar fastaStats-1.0.jar -i <input> -o <output>",
                        options);
                System.exit(0);
            }

        } catch (ParseException ex) {
            System.err.println("Parsing exception");
        }
    }
}
