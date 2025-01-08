package fr.isep.ye.projet_algo_jjx;

public class employee {
    private int id;
    private String name;
    private String sex;
    private int age;
    private String email;

    public employee(int id, String name, String sex, int age, String email) {
        this.id = id;
        this.name = name;
        this.sex = sex;
        this.age = age;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSex(){
        return sex;
    }

    public int getAge(){
        return age;
    }

    public String getEmail() {
        return email;
    }
}