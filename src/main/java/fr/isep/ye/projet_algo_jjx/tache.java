package fr.isep.ye.projet_algo_jjx;

import java.time.LocalDate;

import java.util.List;

public class tache {
    private int id;
    private String name;
    private LocalDate deadline;
    private String category;
    private String description;
    private List<String> employeeName; // 存储分配的员工名称
    private List<String> projetName;

    public tache(int id, String name, LocalDate deadline,String category,String description,List<String> employeeName,List<String> projetName) {
        this.id = id;
        this.name = name;
        this.deadline = deadline;
        this.category=category;
        this.description=description;
        this.employeeName=employeeName;
        this.projetName = projetName;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDate getDdl() {
        return deadline;
    }

    public String getCategory(){
        return category;
    }

    public String getDescription(){
        return description;
    }

    public List<String> getEmployeeName() {
        return employeeName;
    }

    public List<String> getProjetName() {
        return projetName;
    }

}