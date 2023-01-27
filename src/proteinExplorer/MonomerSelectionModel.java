package proteinExplorer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import proteinExplorer.model.PdbMonomer;
import proteinExplorer.model.SelectionModel;

import java.util.Collection;
import java.util.HashSet;

public class MonomerSelectionModel implements SelectionModel<PdbMonomer> {

    private final ObservableSet<PdbMonomer> selection = FXCollections.observableSet(new HashSet<>());

    @Override
    public boolean select(PdbMonomer o) {
        return selection.contains(o);
    }

    @Override
    public boolean setSelected(PdbMonomer o, boolean select) {
        if(!select)
            selection.add(o);
        else
            selection.remove(o);

        return true;
    }

    @Override
    public boolean selectall(Collection list) {
        for(Object o: list)
            select((PdbMonomer) o);
        return true;
    }

    @Override
    public void clearSelection() {
        selection.clear();
    }

    @Override
    public boolean clearSelection(PdbMonomer o) {
        selection.remove(o);
        return true;
    }

    @Override
    public boolean clearSelection(Collection list) {
        for(Object o: list)
            selection.remove((PdbMonomer) o);
        return true;
    }

    @Override
    public ObservableSet getSelectedItems() {
        return selection;
    }
}
