package ru.scheduler.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import ru.scheduler.models.OperationSystem;
import ru.scheduler.models.Task;
import ru.scheduler.enums.Priority;
import ru.scheduler.enums.State;
import ru.scheduler.enums.TaskType;

import java.util.Map;
import java.util.Queue;

public class MainController {

    private OperationSystem operationSystem;

    private static int customTaskNumber = 0;

    @FXML
    private Text thirdPriorityNumber;

    @FXML
    private Text secondPriorityNumber;

    @FXML
    private Text firstPriorityNumber;

    @FXML
    private Text zeroPriorityNumber;

    @FXML
    private Text runningTaskTitle;

    @FXML
    private Text waitingTaskTitle;

    @FXML
    private Text waitingThird;

    @FXML
    private Text waitingSecond;

    @FXML
    private Text waitingFirst;

    @FXML
    private Text waitingZero;

    @FXML
    private VBox finishedTasksPanel;

    @FXML
    private TextField titleField;

    @FXML
    private ToggleGroup typeGroup;

    @FXML
    private RadioButton mainRadioBtn;

    @FXML
    private RadioButton extendedRadioBtn;

    @FXML
    private ComboBox<Integer> prioritiesList;

    @FXML
    private TextField durationField;

    @FXML
    private Text durationFieldError;

    @FXML
    private CheckBox systemCheckBox;

    @FXML
    private Text processorTimer;

    @FXML
    public void initialize() {
        operationSystem = new OperationSystem();
    }

    @FXML
    private void openQueue(MouseEvent event) {
        VBox vbox = (VBox) event.getSource();
        switch (((Text) vbox.getChildren().getFirst()).getText()) {
            case "ZERO" -> openQueueWindow(operationSystem.getSchedulerReadyTasks().get(Priority.ZERO));
            case "FIRST" -> openQueueWindow(operationSystem.getSchedulerReadyTasks().get(Priority.FIRST));
            case "SECOND" -> openQueueWindow(operationSystem.getSchedulerReadyTasks().get(Priority.SECOND));
            case "THIRD" -> openQueueWindow(operationSystem.getSchedulerReadyTasks().get(Priority.THIRD));

        }
    }

    private void openQueueWindow(Queue<Task> tasks) {
        VBox tasksBox = new VBox();
        tasksBox.setSpacing(5);
        int i = 0;
        for (Task task : tasks) {
            Text text = new Text("#" + i + ": " + task.toString());
            tasksBox.getChildren().add(text);
            i++;
        }

        ScrollPane scrollPane = new ScrollPane(tasksBox);

        Stage stage = new Stage();
        Scene scene = new Scene(scrollPane);
        stage.setScene(scene);
        stage.setWidth(600);
        stage.setHeight(400);
        stage.setResizable(false);
        stage.show();
    }

    @FXML
    private void createTask() {
        String taskTitle = titleField.getText() + "#" + customTaskNumber;
        customTaskNumber++;

        TaskType taskType;
        if ((typeGroup.getSelectedToggle()).equals(mainRadioBtn)) {
            taskType = TaskType.MAIN;
        } else {
            taskType = TaskType.EXTENDED;
        }

        State taskState = State.READY;

        Priority taskPriority = Priority.values()[Integer.parseInt(prioritiesList.getValue() + "")];

        int taskDuration;

        try {
            taskDuration = Integer.parseInt(durationField.getText());
            durationFieldError.setText("");

            boolean system = systemCheckBox.isSelected();

            Task task = new Task(taskTitle, taskType, taskState, taskPriority, taskDuration, system);

            operationSystem.addCustomTask(task);
            updateQueues();
        } catch (Exception ex) {
            durationFieldError.setText("Only int!");
        }
    }

    private void updateQueues() {
        Map<Priority, Queue<Task>> readyTasks = operationSystem.getSchedulerReadyTasks();
        zeroPriorityNumber.setText(readyTasks.get(Priority.ZERO).size() + "");
        firstPriorityNumber.setText(readyTasks.get(Priority.FIRST).size() + "");
        secondPriorityNumber.setText(readyTasks.get(Priority.SECOND).size() + "");
        thirdPriorityNumber.setText(readyTasks.get(Priority.THIRD).size() + "");
    }

    @FXML
    private void generateTasks() {
        operationSystem.generateTasks(5);
        updateQueues();
    }

    @FXML
    private void launchOperationSystem() {
        operationSystem.launchOperationSystem(1000);

        Thread thread = new Thread(() -> {
            Runnable updater = () -> {
                finishedTasksPanel.getChildren().clear();
                Queue<Task> finishedTasks = operationSystem.getSchedulerFinishedTasks();
                //System.out.println(finishedTasks);
                for (Task task : finishedTasks) {
                    if (task != null) {
                        addToFinishedTasks(task);
                    }
                }
            };

            while (true) {
                // Priority Queues
                updateQueues();

                // Running task in the processor
                Task runningTask = operationSystem.getExecutionTask();
                if (runningTask != null && runningTask.getState() != State.SUSPENDED) {
                    runningTaskTitle.setText(runningTask.getDescription());
                    processorTimer.setText(operationSystem.getProcessorTimer().get() + "");
                } else {
                    runningTaskTitle.setText("Context \nswitching");
                    processorTimer.setText("X");
                }

                // Waiting tasks
                Task waitingTask = operationSystem.getSchedulerWaitingTask();
                if (waitingTask != null && waitingTask.getState() != State.READY) {
                    waitingTaskTitle.setText(waitingTask.getDescription());
                } else {
                    waitingTaskTitle.setText("No task");
                }

                // Finished tasks
                Platform.runLater(updater);

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    public void addToFinishedTasks(Task task) {
        Text text = new Text(task.toString());
        finishedTasksPanel.getChildren().add(text);
    }
}
