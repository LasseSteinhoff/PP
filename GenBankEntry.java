package com.example.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

class GenBankEntry {

    String definition = "", accession = "", keywords = "", organism = "", author = "";
    Fasta fasta;
    File internFormat = null;
    BufferedReader buffFileReader = null;
    BufferedWriter buffFileWriter = null;

    public void initFileWriterReader () throws IOException {
        buffFileWriter = new BufferedWriter(new FileWriter(internFormat));
        buffFileReader = new BufferedReader(new FileReader(internFormat));
    }
    public void closeFileWriter () throws IOException {
        if(buffFileWriter != null) buffFileWriter.close();
    }
    public void closeFileReader () throws IOException {
        if(buffFileReader != null) buffFileReader.close();
    }
    private boolean extract(String data, String def, String acc, String key, String org, String aut, String src) {
        Scanner s = new Scanner(data);

        String line, dna = "";
        int mode = 0;

        while (s.hasNext()) {
            line = s.next();
             if(line.startsWith(def)) {
                continue;
            }else if(line.startsWith(acc)) {
                mode = 1;
                continue;
            }else if(line.startsWith(key)) {
                 mode = 2;
                 continue;
            }else if(line.startsWith(org)) {
                 mode = 3;
                 continue;
            }else if(line.startsWith(aut)) {
                 mode = 4;
                 continue;
            }else if(line.startsWith(src)) {
                 mode = 5;
                 continue;
            }

             switch(mode) {
                 case 0:
                     definition += line;
                     break;
                 case 1:
                     accession += line;
                     break;
                 case 2:
                     keywords += line;
                     break;
                 case 3:
                     organism += line;
                     break;
                 case 4:
                     author += line;
                     break;
                 case 5:
                     dna += getFormattedDnaRow(line);
                     break;
             }
        }
        s.close();

        fasta = new Fasta(">" + definition, dna);
        return fasta.checkDNA() & fasta.checkHeader();
    }
    public void convertAsInternFormat(String data) {
        extract(data,"def", "acc", "key", "org", "aut", "src");
    }
    public void convertGenBank(String data) {
        extract(data,"DEFINITION", "ACCESSION", "KEYWORDS", "AUTHORS", "ORGANISM", "ORIGIN");
    }
    public void convertEMBL(String data) {
        extract(data,"ID", "AC", "KW", "OS", "RA", "SQ");
    }


    private String getFormattedDnaRow(String line) {
        line = line.toUpperCase();
        if(!line.matches("[A,C,G,T]*")) return "";
        line = line.replaceAll(" ",  "");
        line = line.replaceAll("[0:9]", "");
        return line;
    }

    public  Boolean checkDNA(String dna) {
        Boolean allnucleotides = true;
        char[] Nukleotide = dna.toCharArray();
        char A = 'A', T = 'T', C = 'C', G = 'G';
        for(char X: Nukleotide) {
            if(X != A & X != T & X != C & X != G) {
                allnucleotides = false;
            }
        }
        return allnucleotides;
    }

    public String getFormat(){
        return  " def: \n" + definition +
                "\nacc: \n" + accession +
                "\nkey: \n" + keywords +
                "\norg: \n" + organism +
                "\naut: \n" + author +
                "\nsrc: \n" + fasta.dna;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getAccession() {
        return accession;
    }

    public void setAccession(String accession) {
        this.accession = accession;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getOrganism() {
        return organism;
    }

    public void setOrganism(String organism) {
        this.organism = organism;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Fasta getFasta() {
        return fasta;
    }

    public void setFasta(Fasta fasta) {
        this.fasta = fasta;
    }

    public File getInternFormat() {
        return internFormat;
    }

    public void setInternFormat(File internFormat) {
        this.internFormat = internFormat;
    }

    public BufferedReader getBuffFileReader() {
        return buffFileReader;
    }

    public void setBuffFileReader(BufferedReader buffFileReader) {
        this.buffFileReader = buffFileReader;
    }

    public BufferedWriter getBuffFileWriter() {
        return buffFileWriter;
    }

    public void setBuffFileWriter(BufferedWriter buffFileWriter) {
        this.buffFileWriter = buffFileWriter;
    }
}