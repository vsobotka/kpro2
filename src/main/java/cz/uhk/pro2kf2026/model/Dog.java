package cz.uhk.pro2kf2026.model;

public class Dog {
    private String name;
    private int age;

    //Pomocí AltGr + Insert vygenerujte gettery, settery a konstruktor

    public Dog(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
