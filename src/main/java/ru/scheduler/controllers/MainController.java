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

    private final OperationSystem operationSystem = new OperationSystem();

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
    private void openQueue(MouseEvent event) {
        VBox vbox = (VBox) event.getSource();
        switch (((Text) vbox.getChildren().get(0)).getText()) {
            case "ZERO" -> openQueueWindow(operationSystem.getSchedulerReadyTasks().get(Priority.ZERO));
            case "FIRST" -> openQueueWindow(operationSystem.getSchedulerReadyTasks().get(Priority.FIRST));
            case "SECOND" -> openQueueWindow(operationSystem.getSchedulerReadyTasks().get(Priority.SECOND));
            case "THIRD" -> openQueueWindow(operationSystem.getSchedulerReadyTasks().get(Priority.THIRD));

        }
    }

    private void openQueueWindow(Queue<Task> tasks) {
        System.out.println(tasks);

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
        String taskTitle = titleField.getText();

        TaskType taskType;
        if ((typeGroup.getSelectedToggle()).equals(mainRadioBtn)) {
            taskType = TaskType.MAIN;
        } else {
            taskType = TaskType.EXTENDED;
        }

        State taskState = State.READY;

        Priority taskPriority = Priority.values()[Integer.parseInt(prioritiesList.getValue() + "")];

        int taskDuration = 3;

        try {
            taskDuration = Integer.parseInt(durationField.getText());
            durationFieldError.setText("");

            Task task = new Task(taskTitle, taskType, taskState, taskPriority, taskDuration);

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
        operationSystem.launchOperationSystem();

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                Runnable updater = new Runnable() {
                    @Override
                    public void run() {
                        finishedTasksPanel.getChildren().clear();
                        Queue<Task> finishedTasks = operationSystem.getSchedulerFinishedTasks();
                        for (Task task : finishedTasks) {
                            addToFinishedTasks(task);
                        }
                    }
                };

                while (true) {
                    // Priority Queues
                    updateQueues();

                    // Running task in the processor

                    Task runningTask = operationSystem.getExecutionTask();
                    if (runningTask != null) {
                        runningTaskTitle.setText(runningTask.toString());
                    } else {
                        runningTaskTitle.setText("No task");
                    }

                    // Waiting tasks
                    Map<Priority, Queue<Task>> waitingTasks = operationSystem.getSchedulerWaitingTasks();
                    waitingThird.setText(waitingTasks.get(Priority.THIRD).size() + "");
                    waitingSecond.setText(waitingTasks.get(Priority.SECOND).size() + "");
                    waitingFirst.setText(waitingTasks.get(Priority.FIRST).size() + "");
                    waitingZero.setText(waitingTasks.get(Priority.ZERO).size() + "");

                    // Finished tasks
                    Platform.runLater(updater);

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
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
