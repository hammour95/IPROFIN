import javafx.scene.control.ChoiceDialog;
import model.mmseq;
import model.diamond;
import model.processExecutor;
import model.readFile;

import java.awt.*;
import java.io.*;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.Executors;

/**
 * Start the selected tool for clustering and show the results
 */
public class startClustering {
    public startClustering(WindowController controller, int spec,
                           String method, ArrayList<String> input) throws IOException {
        float minSeqId = (float) controller.getMinSeqIdSlider().getValue();
        float coverage = (float) controller.getCoverageSlider().getValue();

        if (method.equals("linclust") || method.equals("cluster")) {
            int covMode = 0;
            int alnMode = 0;
            int alnOutMode = 0;
            int seqIdMode = 0;
            int clusterMode = 0;
            double eValue = Double.parseDouble(controller.geteValueTextField().getText());
            int minAlnLen = Integer.parseInt(controller.getMinAlnLenTextField().getText());
            int kmerPerSeq = Integer.parseInt(controller.getKmerPerSeqTextField().getText());
            int threads = Integer.parseInt(controller.getThreadsTextField().getText());

            // coverage mode
            switch (controller.getCovModeChoiceBox().getValue()) {
                case "coverage of query and target":
                    break;
                case "coverage of target":
                    covMode = 1;
                    break;
                case "coverage of query":
                    covMode = 2;
                    break;
                case "target seq. length has to be at least x% of query length":
                    covMode = 3;
                    break;
                case "query seq. length has to be at least x% of target length":
                    covMode = 4;
                    break;
                case "short seq. needs to be at least x% of the other seq. length":
                    covMode = 5;
                    break;
            }

            // alignment mode
            switch (controller.getAlnModeChoiceBox().getValue()) {
                case "automatic":
                    break;
                case "only score and end_pos":
                    alnMode = 1;
                    break;
                case "also start_pos and cov":
                    alnMode = 2;
                    break;
                case "also seq.id":
                    alnMode = 3;
                    break;
                case "only ungapped alignment":
                    alnMode = 4;
                    break;
            }

            // alignment output mode
            switch (controller.getAlnOutputModeChoiceBox().getValue()) {
                case "automatic":
                    break;
                case "only score and end_pos":
                    alnOutMode = 1;
                    break;
                case "also start_pos and cov":
                    alnOutMode = 2;
                    break;
                case "also seq.id":
                    alnOutMode = 3;
                    break;
                case "only ungapped alignment":
                    alnOutMode = 4;
                    break;
                case "score only (output) cluster format":
                    alnOutMode = 5;
                    break;
            }

            // sequence id mode
            switch (controller.getSeqIdModeChoiceBox().getValue()) {
                case "alignment length":
                    break;
                case "shorter sequence":
                    seqIdMode = 1;
                    break;
                case "longer sequence":
                    seqIdMode = 2;
                    break;
            }

            // cluster mode
            switch (controller.getClusterModeChoiceBox().getValue()) {
                case "Set-Cover (greedy)":
                    break;
                case "Connected component (BLASTclust)":
                    clusterMode = 1;
                    break;
                case "Greedy clustering by sequence length (CDHIT)":
                    clusterMode = 2;
                    break;
            }

            if (input.isEmpty()) {
                controller.getInfoLabel().setText("please open the FastA files directory!");
            } else {
                // make the results folder if it not exists
                File resultsDir = new File("Results/");
                if (!resultsDir.exists()) {
                    resultsDir.mkdirs();
                }

                // loading the command arguments
                mmseq mmseq = new mmseq(input.toArray(new String[input.size()]), minSeqId, coverage, covMode,
                        alnMode, alnOutMode, seqIdMode, clusterMode, eValue,
                        minAlnLen, kmerPerSeq, threads);

                if (method.equals("linclust")) {

                    controller.getInfoLabel().setText(
                            "Running mmseq2 linclust     Parameters:  minSeqId: " + minSeqId + "  coverage: " +
                            coverage + "  covMode: " + covMode);

                    // changing the mode
                    mmseq.setMode("easy-linclust");
                }

                else {
                    controller.getInfoLabel().setText(
                            "Running mmseq2 cluster // Parameters: minSeqId: " + minSeqId + " coverage: " +
                                    coverage + " covMode: " + covMode);

                    // changing the mode
                    mmseq.setMode("easy-cluster");
                }
                // preparing the process
                processExecutor processExecutor = new processExecutor(mmseq.getCommand());

                // running mmseqs2 and generating the result files
                processExecutor.setOnSucceeded(e->{
                    controller.getStdOutTextArea().setText(processExecutor.getValue());
                });
                processExecutor.run();

                controller.getInfoLabel().setText("Finished mmseqs2 analysis // Spec.: " + spec +
                        " Parameters: minSeqId: " + minSeqId + " coverage: " +
                        coverage + " covMode: " + covMode);

                showMMseqsPlainResults(controller);
            }
        }
        else if(method.equals("diamond")) {
            String db;
            File resultsDir = new File("Results/diamond");
            if (!resultsDir.exists()) {
                resultsDir.mkdirs();
            }

            var dmnd = new diamond();

            if (input.isEmpty()) {
                controller.getInfoLabel().setText("please open the FastA files directory!");
            } else {
                var dialog = new ChoiceDialog<String>(input.get(0), input);
                dialog.setTitle("Choose a species");
                dialog.setHeaderText("Choose a species to make the database from it");

                Optional<String> result = dialog.showAndWait();
                db = "cancelled.";

                if (result.isPresent()) {
                    db = result.get();
                }

                if(db.equals("cancelled.")) {
                    controller.getInfoLabel().setText("Process cancelled.");
                }
                else {
                    // first the makedb command will be executed
                    controller.getStdOutTextArea().clear();

                    String[] makeDB = {dmnd.getPath(), "makedb", "--db", "Results/db", "--in", db};

                    processExecutor processExecutor = new processExecutor(makeDB);
                    processExecutor.setOnSucceeded(e->{
                        controller.getStdOutTextArea().setText(processExecutor.getValue());
                    });
                    processExecutor.run();


                    // then diamond blastp will be executed with every query file
                    dmnd.setQueryCov(controller.getQueryCoverCheckBox().isSelected());
                    dmnd.setSubjectCov(controller.getSubjectCoverCheckBox().isSelected());
                    dmnd.setCov(String.valueOf(coverage * 100));
                    dmnd.setId(String.valueOf(minSeqId * 100));
                    if(controller.geteValueDCheckBox().isSelected()) {
                        dmnd.seteValue(controller.geteValueDTextField().getText());
                    }
                    if(controller.getGabOpenCheckBox().isSelected()) {
                        dmnd.setGabOpen(controller.getGabOpenTextField().getText());
                    }
                    if(controller.getGabExtendCheckBox().isSelected()){
                        dmnd.setGabExtend(controller.getGabExtendTextField().getText());
                    }
                    if(controller.getMatrixCheckBox().isSelected()){
                        dmnd.setMatrix(controller.getMatrixChoiceBox().getValue());
                    }
                    if(controller.getThreadsDCheckBox().isSelected()){
                        dmnd.setThreads(controller.getTreadsDTextField().getText());
                    }

                    for(String query: input) {
                        dmnd.setQuery(query);
                        String specName = query.split("/")[query.split("/").length-1].split(".fa")[0];
                        dmnd.setOut("Results/diamond/" + specName);
                        processExecutor Diamond = new processExecutor(dmnd.getCommand());
                        Diamond.setOnSucceeded(e-> {
                            controller.getStdOutTextArea().appendText("\n" + Diamond.getValue());
                        });
                        Diamond.run();
                    }
                    convertDResults("Results/diamond/");
                }
            }
        }
    }

    /**
     * Showing the result files in the plain results tab
     */
    public void showMMseqsPlainResults(WindowController controller) throws IOException {
        var serviceRep = new readFile("Results/cluster_rep_seq.fasta");
        serviceRep.setOnSucceeded(e -> {
            var results = serviceRep.getValue();
            for (String s : results.split("\n")) {
                controller.getRepresentativesTextArea().getItems().add(s);
            }
        });

        var serviceAdj = new readFile("Results/cluster_cluster.tsv");
        serviceAdj.setOnSucceeded(e -> {
            var results = serviceAdj.getValue();
            for (String s : results.split("\n")) {
                controller.getAdjecencyListTextArea().getItems().add(s);
            }
        });

        var serviceFlc = new readFile("Results/cluster_all_seqs.fasta");
        serviceFlc.setOnSucceeded(e -> {
            var results = serviceFlc.getValue();
            for (String s : results.split("\n")) {
                controller.getFlcTextArea().getItems().add(s);
            }
        });
        Executors.newSingleThreadExecutor().submit(serviceFlc);
        Executors.newSingleThreadExecutor().submit(serviceRep);
        Executors.newSingleThreadExecutor().submit(serviceAdj);
    }

    /**
     * convert the output of diamond to match the output of mmseqs
     */
    public void convertDResults(String Path) throws IOException {
        BufferedWriter br = new BufferedWriter(new FileWriter("Results/cluster_cluster.tsv"));

        File results = new File(Path);
        File[] files = results.listFiles();
        StringBuilder text = new StringBuilder();

        assert files != null;
        for(File file:files) {

            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;

            while((line = reader.readLine()) != null) {
                String[] parts = line.split("\t");
                String querySeq = parts[0].substring(4);
                String targetSeq = parts[1].substring(4);
                text.append(targetSeq + "\t" + querySeq + "\n");
            }
        }
        br.write(text.toString());
        br.close();
    }
}
