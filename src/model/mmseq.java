package model;

import java.util.ArrayList;
import java.util.Collections;

public class mmseq {
    String path = "./src/model/mmseqs/bin/mmseqs";
    String mode = "easy-linclust";
    String tmp = "tmp";
    String results = "Results/cluster";
    String[] input;
    float minSeqId;
    float coverage;
    int covMode;
    int alnMode;
    int alnOutMode;
    int seqIdMode;
    int clusterMode;
    double eValue;
    int minAlnLen;
    int kmerPerSeq;
    int threads;

    public mmseq(String[] input, float minSeqId, float coverage, int covMode,
                 int alnMode, int alnOutMode, int seqIdMode, int clusterMode,
                 double eValue, int minAlnLen, int kmerPerSeq, int threads) {
        this.input = input;
        this.minSeqId = minSeqId;
        this.coverage = coverage;
        this.covMode = covMode;
        this.alnMode = alnMode;
        this.alnOutMode = alnOutMode;
        this.seqIdMode = seqIdMode;
        this.clusterMode = clusterMode;
        this.eValue = eValue;
        this.minAlnLen = minAlnLen;
        this.kmerPerSeq = kmerPerSeq;
        this.threads = threads;
    }

    public String[] getCommand(){
        ArrayList<String> command = new ArrayList<>();

        command.add(path);
        command.add(mode);
        Collections.addAll(command, input);
        command.add(results);
        command.add(tmp);
        command.add("--min-seq-id");
        command.add(String.valueOf(minSeqId));
        command.add("-c");
        command.add(String.valueOf(coverage));
        command.add("--cov-mode");
        command.add(String.valueOf(covMode));
        command.add("--alignment-mode");
        command.add(String.valueOf(alnMode));
        command.add("--alignment-output-mode");
        command.add(String.valueOf(alnOutMode));
        command.add("--seq-id-mode");
        command.add(String.valueOf(seqIdMode));
        command.add("--cluster-mode");
        command.add(String.valueOf(clusterMode));
        command.add("-e");
        command.add(String.valueOf(eValue));
        command.add("--min-aln-len");
        command.add(String.valueOf(minAlnLen));
        command.add("--kmer-per-seq");
        command.add(String.valueOf(kmerPerSeq));
        command.add("--threads");
        command.add(String.valueOf(threads));


        return command.toArray(new String[command.size()]);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public float getMinSeqId() {
        return minSeqId;
    }

    public void setMinSeqId(float minSeqId) {
        this.minSeqId = minSeqId;
    }

    public float getCoverage() {
        return coverage;
    }

    public void setCoverage(float coverage) {
        this.coverage = coverage;
    }

    public int getCovMode() {
        return covMode;
    }

    public void setCovMode(int covMode) {
        this.covMode = covMode;
    }
}
