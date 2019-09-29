package contacts;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    static ArrayList sPersonList = new ArrayList<Contact>();
    static Scanner scanner = new Scanner(System.in);
    static File file = new File("phonebook.db");

    public static void main(String[] args) throws IOException {
        System.out.println("open phonebook.db");
//        System.out.println();
        readFromFile();

        System.out.print("[menu] Enter action (add, list, search, count, exit): > ");
        String action = scanner.nextLine();
        while (!action.equalsIgnoreCase("exit")) {
            switch (action) {
                case "add":
                    add();
                    break;
                case "list":
                    info();
                    break;
                case "search":
                    search();
                    break;
                case "count":
                    System.out.println(String.format("The Phone Book has %d records.", sPersonList.size()));
                    break;
            }
            System.out.println();
            System.out.print("[menu] Enter action (add, list, search, count, exit): > ");
            action = scanner.nextLine();
        }

        saveToFile();
    }

    static void saveToFile() {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
//            objectOutputStream.writeObject(sPersonList);
            objectOutputStream.close();
        } catch (Exception e) {
//            System.out.println(e.getClass().getSimpleName());
        }
    }

    static void readFromFile() {
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
//            sPersonList = (ArrayList) objectInputStream.readObject();
        } catch (Exception e) {
//            System.out.println(e.getClass().getSimpleName());
        }
    }

    static void search() throws IOException {
        List foundPersons = performSearch();

        System.out.print("[search] Enter action ([number], back, again): > ");
        String action = scanner.nextLine();
        while (action.matches("again") || action.matches("AGAIN")) {
            foundPersons = performSearch();

            System.out.print("[search] Enter action ([number], back, again): > ");
            action = scanner.nextLine();
        }
        if (action.matches("back") || action.matches("BACK")) {
        } else if (action.matches("\\d*")) {
            int index = Integer.parseInt(action) - 1;
            Contact contact = (Contact) foundPersons.get(index);
            record(contact);
        }
    }

    static List performSearch() {
        System.out.print("Enter search query: > ");
        String keyword = scanner.nextLine();
        Pattern pattern = Pattern.compile(keyword, Pattern.CASE_INSENSITIVE);

        List foundPersons = new ArrayList();
        for (Object person : sPersonList) {
            String text = person.toString();

            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                foundPersons.add(person);
            }
        }

        System.out.println(String.format("Found %d results:", foundPersons.size()));
        listPersons(foundPersons);

        return foundPersons;
    }

    static void add() throws IOException {
        System.out.print("Enter the type (person, organization): >");
        String type = scanner.nextLine();
        Contact contact = null;
        if ("person".equalsIgnoreCase(type)) {
            contact = new Person();
            Person person = (Person) contact;

            System.out.print("Enter the name: > ");
            person.setName(scanner.nextLine());

            System.out.print("Enter the surname: > ");
            person.setSurName(scanner.nextLine());

            System.out.print("Enter the birth date: > ");
            person.setBirthDate(scanner.nextLine());

            System.out.print("Enter the gender (M, F): > ");
            person.setGender(scanner.nextLine());

            System.out.print("Enter the number: > ");
            String num = scanner.nextLine();
            person.setNumber(num);

        } else if ("organization".equalsIgnoreCase(type)) {
            contact = new Organization();
            Organization organization = (Organization) contact;
            System.out.print("Enter the organization name: >  ");
            organization.setOrganizationName(scanner.nextLine());

            System.out.print("Enter the address: > ");
            organization.setOrganizationAddress(scanner.nextLine());

            System.out.print("Enter the number: > ");
            String num = scanner.nextLine();
            organization.setNumber(num);

        }
        if (contact != null) {
            String dateTime = LocalDateTime.now().withSecond(0).withNano(0).toString();
            contact.setCreateDateTime(dateTime);
            contact.setLastEditDateTime(dateTime);
            sPersonList.add(contact);
            saveToFile();
        }
        System.out.println("The record added.");
    }

    static void remove(Contact contact) {
        sPersonList.remove(contact);
        saveToFile();
        System.out.println("The record removed!");
    }

    static void edit(Contact contact) {
        if (contact instanceof Person) {
            Person person = (Person) contact;
            System.out.print("Select a field (name, surname, birth, gender, number): > ");
            String field = scanner.nextLine();
            switch (field) {
                case "name":
                    System.out.print("Enter the name: > ");
                    person.setName(scanner.nextLine());
                    break;
                case "surname":
                    System.out.print("Enter the surname: > ");
                    person.setSurName(scanner.nextLine());
                    break;
                case "birth":
                    System.out.print("Enter the birth date: > ");
                    person.setBirthDate(scanner.nextLine());
                    break;
                case "gender":
                    System.out.print("Enter the gender (M, F): > ");
                    person.setGender(scanner.nextLine());
                    break;
                case "number":
                    System.out.print("Enter the number: > ");
                    person.setNumber(scanner.nextLine());
                    break;
                default:
                    System.out.println("No such field!");
                    break;
            }
        } else if (contact instanceof Organization) {
            Organization organization = (Organization) contact;
            System.out.print("Select a field (name, address, number): > ");
            String field = scanner.nextLine();
            switch (field) {
                case "name":
                    System.out.print("Enter the name: > ");
                    organization.setOrganizationName(scanner.nextLine());
                    break;
                case "address":
                    System.out.print("Enter the surname: > ");
                    organization.setOrganizationAddress(scanner.nextLine());
                    break;
                case "number":
                    System.out.print("Enter the number: > ");
                    organization.setNumber(scanner.nextLine());
                    break;
                default:
                    System.out.println("No such field!");
                    break;
            }
        }
        contact.setLastEditDateTime(LocalDateTime.now().withSecond(0).withNano(0).toString());
        System.out.println("Saved");
    }

    static void info() throws IOException {
        list();
        if (sPersonList.size() > 0) {
            System.out.print("[list] Enter action ([number], back): > ");
            String action = scanner.nextLine();
            if (!"back".equalsIgnoreCase(action)) {
                int index = Integer.parseInt(action) - 1;
                Contact contact = (Contact) sPersonList.get(index);
                record(contact);
            }
        }
    }

    static void record(Contact contact) throws IOException {
        System.out.println(contact);
        System.out.println();
        System.out.print("[record] Enter action (edit, delete, menu): >  ");
        switch (scanner.nextLine()) {
            case "edit":
                edit(contact);
                break;
            case "delete":
                remove(contact);
                break;
            default:
            case "menu":
                break;
        }
    }

    static void list() {
        if (sPersonList.size() <= 0) {
            System.out.println("No records to list!");
        } else {
            listPersons(sPersonList);
        }
    }

    static void listPersons(List persons) {
        for (Object p : persons) {
            if (p instanceof Person) {
                System.out.println(String.format("%d. %s",
                        sPersonList.indexOf(p) + 1, ((Person) p).getFullName()));
            } else if (p instanceof Organization) {
                System.out.println(String.format("%d. %s",
                        sPersonList.indexOf(p) + 1, ((Organization) p).getOrganizationName()));
            }
        }
        System.out.println();
    }

    abstract static class Contact implements Serializable {
        private String createDateTime;
        private String lastEditDateTime;
        private String number = "";
        static Pattern pattern1 = Pattern.compile("\\+?" +
                "[0-9a-zA-Z]+([\\s-]\\([0-9a-zA-Z]+\\))?" +
                "([\\s-][0-9a-zA-Z]{2,})*");

        static Pattern pattern2 = Pattern.compile("\\+?" +
                "\\([0-9a-zA-Z]+\\)([\\s-][0-9a-zA-Z]+)?" +
                "([\\s-][0-9a-zA-Z]{2,})*");

        public String getNumber() {
            if (number.isEmpty()) {
                return "[no number]";
            }
            return number;
        }

        public void setNumber(String number) {
            if (pattern1.matcher(number).matches() ||
                    pattern2.matcher(number).matches()) {
                this.number = number;
            } else {
                System.out.println("Wrong number format!");
                this.number = "";
            }
        }

        public boolean hasNumber() {
            return number.isEmpty();
        }

        public String getCreateDateTime() {
            return createDateTime;
        }

        public void setCreateDateTime(String createDateTime) {
            this.createDateTime = createDateTime;
        }

        public String getLastEditDateTime() {
            return lastEditDateTime;
        }

        public void setLastEditDateTime(String lastEditDateTime) {
            this.lastEditDateTime = lastEditDateTime;
        }

        @Override
        public String toString() {
            return String.format("Number: %s\nTime created: %s\nTime last edit: %s", number, createDateTime, lastEditDateTime);
        }
    }

    static class Person extends Contact implements Serializable {
        private String name;
        private String surName;
        private String birthDate = "";
        private String gender = "";

        Pattern pattern = Pattern.compile("(?i)[MF]");

        public String getFullName() {
            return name + " " + surName;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSurName() {
            return surName;
        }

        public void setSurName(String surName) {
            this.surName = surName;
        }

        public String getBirthDate() {
            if (birthDate.isEmpty()) {
                return "[no data]";
            }
            return birthDate;
        }

        public void setBirthDate(String birthDate) {
            try {
                LocalDateTime.parse(birthDate);
                this.birthDate = birthDate;
            } catch (Exception e) {
                System.out.println("Bad birth date!");
            }
        }

        public String getGender() {
            if (gender.isEmpty()) {
                return "[no data]";
            }
            return gender;
        }

        public void setGender(String gender) {
            if (pattern.matcher(gender).matches()) {
                this.gender = gender;
            } else {
                System.out.println("Bad gender!");
                this.gender = "";
            }
        }

        @Override
        public String toString() {
            return String.format("Name: %s\nSurname: %s\nBirth date: %s\nGender: %s\n%s",
                    name, surName, getBirthDate(), getGender(), super.toString());
        }
    }

    static class Organization extends Contact implements Serializable {

        private String organizationName;
        private String organizationAddress;

        public String getOrganizationName() {
            return organizationName;
        }

        public void setOrganizationName(String organizationName) {
            this.organizationName = organizationName;
        }

        public String getOrganizationAddress() {
            return organizationAddress;
        }

        public void setOrganizationAddress(String organizationAddress) {
            this.organizationAddress = organizationAddress;
        }

        @Override
        public String toString() {
            return String.format("Organization name: %s\nAddress: %s\n%s",
                    getOrganizationName(), getOrganizationAddress(), super.toString());
        }
    }
}
