import javafx.scene.control.TreeItem;
import model.fastaParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class magicRun {

    WindowPresenter presenter;
    WindowController controller;


    public magicRun(WindowPresenter presenter, WindowController controller) {
        this.presenter = presenter;
        this.controller = controller;
    }

    public int testParameter(double id, double cov) throws IOException {
        var count = 0;
        var input = presenter.getInput();
        var spec = presenter.getSpec();
        var method = presenter.getMethod();

        controller.getMinSeqIdSlider().setValue(id);
        controller.getCoverageSlider().setValue(cov);

        new startClustering(controller, presenter, spec, method, input);
        presenter.summary(controller, spec);

        var root = controller.getResultsTextArea().getRoot();
        var children = root.getChildren();

        for(var ch: children) {
            if(ch.getChildren().size() == spec + 1) {
                count += 1;
            }
        }

        return count;
    }

    public void findOptimalParameters(double accuracy) throws IOException {
        double id = 0.5;
        double cov = 0.5;

        double newId = 0;
        double newCov = 0;

        int count;

        boolean found = false;
        double acc = 0.2;
        int max = 0;

        while(!found) {

            for(double i = id-acc; i <= id+acc; i += acc) {
                for(double j = cov-acc; j <= cov+acc; j += acc ){
                    count = testParameter(i, j);
                    if(count > max) {
                        max = count;
                        newId = i;
                        newCov = j;
                    }
                }
            }
            if((newId == id && newCov == cov) || acc <= accuracy) {
                controller.getMinSeqIdSlider().setValue(id);
                controller.getCoverageSlider().setValue(cov);
                found = true;
                System.out.println("acc = " + acc + " ID= " + newId + " cov= " + newCov + " count= " + max);
            } else{
                id = newId;
                cov = newCov;
                acc = acc/2;
            }
        }
    }

    public void kmerSearch(int kmerSize, int tolerance) {

        var root = controller.getResultsTextArea().getRoot();
        var children = root.getChildren();

        ArrayList<TreeItem<String>> toRemove = new ArrayList<>(); // The clusters that have no kmers together
        HashMap<String, Integer> hash = new HashMap<>();

        for(var cluster: children) {

            ArrayList<String> seqs = new ArrayList<>();

            // save the sequences in ArrayList
            for(var ch: cluster.getChildren()) {
                if (ch.isLeaf())
                    continue;

                String seq = ch.getChildren().get(0).getValue();
                seqs.add(seq);
            }

            for(var sequence: seqs) {
                for(int i= 0; i < sequence.length()-kmerSize; i++) {
                    String kmer = sequence.substring(i, i+kmerSize);

                    if(hash.containsKey(kmer)) {
                        hash.replace(kmer, hash.get(kmer)+1);
                    }
                    else{
                        hash.put(kmer, 1);
                    }
                }
            }
            boolean remove = true;
            for(int i = 0; i <= tolerance; i++) {
                if (hash.containsValue(presenter.getSpec() - i)) {
                    remove = false;
                    break;
                }
            }
            if(remove)
                toRemove.add(cluster);
            hash.clear();
        }
        for(var ch:toRemove){
            children.remove(ch);
        }
        presenter.numberOfClusters(controller);
    }


    public void checkMSA(int tolerance) throws IOException {
        var root = controller.getResultsTextArea().getRoot();
        var children = root.getChildren();

        TreeItem<String> results = new TreeItem<>("Results");

        ArrayList<TreeItem> toRemove = new ArrayList<>();

        for(var cluster: children) {
            // generating the MSA
            controller.getResultsTextArea().getSelectionModel().select(cluster);

            // looking for conserved regions
            var alignment = new fastaParser();
            alignment.read("src/aligned.fasta");

            TreeItem<String> header = new TreeItem<>(alignment.get(0).header());
            header.getChildren().add(new TreeItem<>(alignment.get(0).sequence()));
            header.getChildren().add(new TreeItem<>("Conserved regions: "));

            char[] hits = new char[alignment.get(0).sequence().length()];

            for(int i = 0; i < alignment.get(0).sequence().length(); i++) {
                char aminoAcid = alignment.get(0).sequence().charAt(i);
                int counter = 1;
                for(int j = 1; j < alignment.size(); j++) {
                    char aminoAcid2 = alignment.get(j).sequence().charAt(i);

                    if(aminoAcid == aminoAcid2 && aminoAcid != '-') {
                        counter++;
                    }
                }

                boolean broken = true;
                for(int tol = 0; tol <= tolerance; tol++) {
                    if(counter == presenter.spec - tol) {
                        broken = false;
                        break;
                    }
                }
                if(!broken) {
                    hits[i] = aminoAcid;
                } else {
                    hits[i] = 'B';
                }
            }
            StringBuilder pos = new StringBuilder();
            StringBuilder epi = new StringBuilder();

            for(int z = 0; z < hits.length; z++) {
                if(hits[z] == 'B') {
                    pos.append(" - ").append(z);
                    if(epi.length() >= 4) {
                        TreeItem<String> treeItem = new TreeItem<>(epi.append("\t").append(pos).toString());
                        header.getChildren().add(treeItem);
                    }
                    pos = new StringBuilder();
                    epi = new StringBuilder();
                }
                else{
                    if(pos.isEmpty()){
                        pos.append(z+1);
                    }
                    epi.append(hits[z]);
                }
            }
            if(epi.length() != 0){
                pos.append(" - ").append(hits.length);
                if(epi.length() >= 4) {
                    TreeItem<String> treeItem = new TreeItem<>(epi.append("\t").append(pos).toString());
                    header.getChildren().add(treeItem);
                }
            }
            if(header.getChildren().size() > 2) {
                header.valueProperty().bind(cluster.valueProperty());
                results.getChildren().add(header);
            }
            else {
                toRemove.add(cluster);
            }
        }
        for(var cluster:toRemove) {
            children.remove(cluster);
        }
        presenter.numberOfClusters(controller); // update the Number of clusters label

        results.setExpanded(true);

        controller.getConservedTab().setDisable(false);
        controller.getConservedTreeView().setRoot(results);
        controller.getResultsPane().getSelectionModel().select(controller.getConservedTab());

        controller.getFilterButton().setDisable(true);
        controller.getSequenceCheckBox().setDisable(true);
        controller.getClearFiltersButton().setDisable(true);
        controller.getShowButton().setDisable(true);
        controller.getRemoveButton().setDisable(true);
    }
}


// changing the other parameters manually
// ID= 0.4 cov= 0.7 count= 1648
// ID= 0.4 cov= 0.35 count= 1654
// ID= 0.4 cov= 0.35 count= 1652
// ID= 0.4 cov= 0.35 count= 1656
// ID= 0.4 cov= 0.65 count= 1653
// ID= 0.4 cov= 0.35 count= 1652
// ID= 0.4 cov= 0.3 count= 1806    (kmer per seq 100)
// ID= 0.4 cov= 0.4 count= 1436    (kmer per seq 10)



// acc = 0.05 ID= 0.45 cov= 0.2 count= 371  linClust
// acc = 0.05 ID= 0.4 cov= 0.35 count= 582  MMseqs
// acc = 0.05 ID= 0.4 cov= 0.25 count= 551  DIAMOND