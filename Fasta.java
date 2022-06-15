package com.example.server;

class Fasta {

    String header = ">";
    String dna = "";

    public Fasta(String header, String dna) {
        this.header = header;
        this.dna = dna;
    }

    // Kopierkonstruktors
    public Fasta(Fasta fasta) {
        this.header = fasta.header;
        this.dna = fasta.dna;
    }

    public String printDNAsubstrings(Integer substringlength) {
        String s = "";
        int i = 0;
        try {
            while (true) {
                s += dna.substring(i * substringlength, i * substringlength + substringlength) + " " + String.valueOf((i + 1) * substringlength)+ "\n";
                ++i;
            }
        } catch (StringIndexOutOfBoundsException e) {
            s += dna.substring(i * substringlength, dna.length()) + " " + String.valueOf(dna.length());
        }
        return s;
    }

    public Boolean checkHeader() {
        if (header.startsWith(">")) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean checkDNA() {
        return dna.matches("[A,C,G,T]*");
    }

    public void reset() {
        header = "";
        dna = "";
    }

    public String getHeader() {
        return header;
    }

    public String getDNA() {
        return dna;
    }

    public Integer getDNALength() {
        return dna.length();
    }

    public String mergetwoDNAStrings(String a, String b) {
        /* Consenus-Sequenz
         *		 Match || Kompromiss (x/y)
         */

        String ConsensusSequenz = "";
        char[] achars = new char[a.length()], bchars = new char[b.length()];
        achars = a.toCharArray();
        bchars = b.toCharArray();

        for (int i = 0; i < a.length(); i++) {
            if (achars[i] == bchars[i]) {
                ConsensusSequenz += a.charAt(i);
            } else ConsensusSequenz += "(" + a.charAt(i) + b.charAt(i) + ")";
        }
        return ConsensusSequenz;
    }
}