package proteinExplorer;

import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import proteinExplorer.model.Command;

/**
 * Basic Undo Redo Manger
 */
public class UndoRedoManger {
    private final ObservableList<Command> undoStack = FXCollections.observableArrayList();
    private final ObservableList<Command> redoStack = FXCollections.observableArrayList();

    private final StringProperty undoLabel = new SimpleStringProperty("Undo");
    private final StringProperty redoLabel = new SimpleStringProperty("Redo");

    private final BooleanProperty canUndo = new SimpleBooleanProperty(false);
    private final BooleanProperty canRedo = new SimpleBooleanProperty(false);

    private final BooleanProperty inUndoRedo = new SimpleBooleanProperty(false);

    public UndoRedoManger() {
        undoStack.addListener((InvalidationListener) e-> {
            undoLabel.set("Undo " + (undoStack.size() == 0 ? "": undoStack.get(undoStack.size()-1).name()));
        });

        redoStack.addListener((InvalidationListener) e-> {
            redoLabel.set("Redo " + (redoStack.size() == 0 ? "": redoStack.get(redoStack.size()-1).name()));
        });

        canUndo.bind(Bindings.isNotEmpty(undoStack));
        canRedo.bind(Bindings.isNotEmpty(redoStack));
    }

    public void undo() {
        inUndoRedo.set(true);
        try {
            if(canUndo.get()) {
                var command = undoStack.remove(undoStack.size()-1);
                command.undo();
                if(command.canRedo()) {
                    redoStack.add(command);
                } else
                    redoStack.clear();
            }
        } finally {
            inUndoRedo.set(false);
        }
    }

    public void redo() {
        inUndoRedo.set(true);
        try {
            if(canRedo.get()) {
                var command = redoStack.remove(redoStack.size()-1);
                command.redo();
                if(command.canUndo())
                    undoStack.add(command);
                else
                    undoStack.clear();
            }
        }finally {
            inUndoRedo.set(false);
        }
    }

    public void add(Command command) {
        if(!inUndoRedo.get()) {
            if(command.canUndo())
                undoStack.add(command);
            else
                undoStack.clear();
        }
    }

    public ObservableList<Command> getUndoStack() {
        return undoStack;
    }

    public ObservableList<Command> getRedoStack() {
        return redoStack;
    }

    public String getUndoLabel() {
        return undoLabel.get();
    }

    public StringProperty undoLabelProperty() {
        return undoLabel;
    }

    public String getRedoLabel() {
        return redoLabel.get();
    }

    public StringProperty redoLabelProperty() {
        return redoLabel;
    }

    public boolean isCanUndo() {
        return canUndo.get();
    }

    public BooleanProperty canUndoProperty() {
        return canUndo;
    }

    public boolean isCanRedo() {
        return canRedo.get();
    }

    public BooleanProperty canRedoProperty() {
        return canRedo;
    }

    public boolean isInUndoRedo() {
        return inUndoRedo.get();
    }

    public BooleanProperty inUndoRedoProperty() {
        return inUndoRedo;
    }
}
