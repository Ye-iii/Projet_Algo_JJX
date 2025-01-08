package fr.isep.ye.projet_algo_jjx;

import java.util.Date;
import java.util.List;

public class projet {
    private int id;
    private String name;
    private String group;
    private Date deadline;
    private String status;
    private List<String> members;

    public projet(int id, String name, String group, Date deadline, String status,List<String> members) {
        this.id = id;
        this.name = name;
        this.group = group;
        this.deadline = deadline;
        this.status = status;
        this.members = members;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getGroup() {
        return group;
    }

    public Date getDdl() {
        return deadline;
    }

    public String getStatus() {
        return status;
    }

    public List<String> getMembers() {
        return members;
    }
}