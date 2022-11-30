package model;

import java.util.ArrayList;

public class diamond {

    String path = "src/model/Diamond/diamond";
    String mode = "blastp";
    String db = "Results/db.dmnd";
    String query;
    String eValue;
    String out;
    String threads;
    String cov;
    String id;
    boolean subjectCov = false;
    boolean queryCov = false;
    String gabOpen;
    String gabExtend;
    String matrix;


    public String[] getCommand(){
        ArrayList<String> command = new ArrayList<>();

        command.add(path);
        command.add(mode);
        command.add("-q");
        command.add(query);
        command.add("--db");
        command.add(db);
        command.add("--out");
        command.add(out);
        command.add("--id");
        command.add(id);
        if(subjectCov){
            command.add("--subject-cover");
            command.add(cov);
        }
        if(queryCov){
            command.add("--query-cover");
            command.add(cov);
        }
        if(eValue != null){
            command.add("--evalue");
            command.add(eValue);
        }
        if(gabOpen != null) {
            command.add("--gapopen");
            command.add(gabOpen);
        }
        if(gabExtend != null) {
            command.add("--gapextend");
            command.add(gabExtend);
        }
        if(matrix != null){
            command.add("--matrix");
            command.add(matrix);
        }
        if(threads != null){
            command.add("--threads");
            command.add(threads);
        }


        return command.toArray(new String[0]);
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

    public String getDb() {
        return db;
    }

    public void setDb(String db) {
        this.db = db;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String geteValue() {
        return eValue;
    }

    public void seteValue(String eValue) {
        this.eValue = eValue;
    }

    public String getOut() {
        return out;
    }

    public void setOut(String out) {
        this.out = out;
    }

    public String getThreads() {
        return threads;
    }

    public void setThreads(String threads) {
        this.threads = threads;
    }

    public String getCov() {
        return cov;
    }

    public void setCov(String cov) {
        this.cov = cov;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isSubjectCov() {
        return subjectCov;
    }

    public void setSubjectCov(boolean subjectCov) {
        this.subjectCov = subjectCov;
    }

    public boolean isQueryCov() {
        return queryCov;
    }

    public void setQueryCov(boolean queryCov) {
        this.queryCov = queryCov;
    }

    public String getGabOpen() {
        return gabOpen;
    }

    public void setGabOpen(String gabOpen) {
        this.gabOpen = gabOpen;
    }

    public String getGabExtend() {
        return gabExtend;
    }

    public void setGabExtend(String gabExtend) {
        this.gabExtend = gabExtend;
    }

    public String getMatrix() {
        return matrix;
    }

    public void setMatrix(String matrix) {
        this.matrix = matrix;
    }
}
