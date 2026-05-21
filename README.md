# CPU Scheduling Simulator
## Operating Systems 1 – Final Project
### Priority Scheduling vs SRTF

This project is a CPU Scheduling Simulator developed for the Operating Systems course.

The simulator compares two preemptive CPU scheduling algorithms:

- Preemptive Priority Scheduling
- Shortest Remaining Time First (SRTF)

The system allows dynamic process input, validates all entered data, simulates both algorithms step-by-step,
and compares their performance using multiple scheduling metrics.

## Project Objectives

- Understand CPU scheduling techniques
- Compare fairness and efficiency between algorithms
- Analyze starvation risks
- Study scheduling performance metrics
- Visualize process execution using Gantt Charts

  ## Features

- Dynamic process input
- Input validation
- Preemptive Priority Scheduling
- Shortest Remaining Time First (SRTF)
- Automatic Gantt Chart generation
- Metrics calculation:
- Waiting Time (WT)
- Turnaround Time (TAT)
- Response Time (RT)
- Average metrics calculation
- GUI implementation using JavaFX
- Starvation and fairness analysis

## Scheduling Rules

### Priority Scheduling
- Lower priority number means higher priority
- Preemptive scheduling
- Tie-breaking:
  1. Higher priority
  2. Earlier arrival time
  3. Alphabetical PID order

### SRTF
- Process with shortest remaining burst time executes first
- Preemptive scheduling
- Tie-breaking:
  1. Shortest remaining time
  2. Earlier arrival time
  3. Alphabetical PID order
 
## Technologies Used

- Java
- JavaFX
- Object-Oriented Programming (OOP)
- Collections Framework
