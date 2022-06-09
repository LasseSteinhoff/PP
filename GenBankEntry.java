package com.example.database;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
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
    public boolean extract(String data, String def, String acc, String key, String org, String aut, String src) {
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
    public void extractAsInternFormat(String data) throws IOException {
        transform(data,"definition", "accession", "keywords", "organism", "author", "source");
    }
    public void convertGenBank(String data) {
        transform(data,"DEFINITION", "ACCESSION", "KEYWORDS", "  AUTHORS", "  ORGANISM", "ORIGIN");
    }
    public void convertEMBL(String data) {
        transform(data,"DE", "AC", "KW", "OS", "RA", "SQ");
    }

    public void transform(String data, String definition, String accession, String keywords, String organism, String author, String source) {


        String line, dna = "";
        Boolean ending = false;

        Scanner s = new Scanner(data);

        while(s.hasNextLine()) {
            line = s.nextLine();
            System.out.println(line);
            if(ending) {
                dna += getFormattedDnaRow(line);
            }else if(line.startsWith(definition)) {
                definition = line.substring(4);
            }else if(line.startsWith(accession)) {
                accession = line.substring(4);
            }else if(line.startsWith(keywords)) {
                keywords = line.substring(4);
            }else if(line.startsWith(organism)) {
                organism = line.substring(4);
            }else if(line.startsWith(author)) {
                author = line.substring(4);
            }else if(line.startsWith(source)) {
                ending = true;
                dna = line.substring(4);
            }

        }
        s.close();
        System.out.println("fdg" );
        fasta = new Fasta(">" + definition, dna);
        fasta.dna = fasta.printDNAsubstrings(80);

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
        return  " def:" + definition +
                "\nacc: " + accession +
                "\nkey: " + keywords +
                "\norg: " + organism +
                "\naut:" + author +
                "\ndna:" + fasta.dna;
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