package aydin.firebasedemo;

public class Person {
    private String name;
    private int age;
    private String phoneNumber;

    public Person(String name, int age)//, String phoneN)
    {
        this.name = name;
        this.age = age;
        //this.phoneNumber = phoneN;
    }

    public String getName() {
        return name;
    }

    public String getphoneNumber() {
        return phoneNumber;
    }

    public int getAge() {
        return age;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setphoneNumber(String phoneNum)
    {
        this.phoneNumber = phoneNum;
    }

    public void setAge(int age) {
        this.age = age;
    }

}
