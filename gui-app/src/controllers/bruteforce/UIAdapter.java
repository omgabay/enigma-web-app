package controllers.bruteforce;

import javafx.application.Platform;

import java.util.function.Consumer;

public class UIAdapter {

    Consumer<BruteforceSolution> addSolutionToTable;
    Consumer<Long> updateProgress;

    public UIAdapter(Consumer<BruteforceSolution> addSolutionToTable, Consumer<Long> updateProgress) {
        this.addSolutionToTable = addSolutionToTable;
        this.updateProgress = updateProgress;
    }

    public void updateProgress(Long taskCompleted){
        Platform.runLater( ()->
                updateProgress.accept(taskCompleted)
        );
    }

    public void solutionFound(BruteforceSolution solution){
        Platform.runLater( ()->
                addSolutionToTable.accept(solution)
        );
    }

}


