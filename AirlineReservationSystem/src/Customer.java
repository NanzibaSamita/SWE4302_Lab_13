import java.util.*;

public class Customer {
    // ************************************************************ Fields
    // ************************************************************
    private final String userID;
    private String email;
    private String name;
    private String phone;
    private final String password;
    private String address;
    private int age;
    private List<Flight> flightsRegisteredByUser = new ArrayList<>();
    private List<Integer> numOfTicketsBookedByUser = new ArrayList<>();
    public static final List<Customer> customerCollection = User.getCustomersCollection();

    // ************************************************************
    // Constructors
    // ************************************************************
    Customer() {
        this.userID = null;
        this.name = null;
        this.email = null;
        this.password = null;
        this.phone = null;
        this.address = null;
        this.age = 0;
    }

    Customer(String name, String email, String password, String phone, String address, int age) {
        RandomGenerator random = new RandomGenerator();
        random.generateRandomCustomerId();
        this.name = name;
        this.userID = random.getRandomNumber();
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.address = address;
        this.age = age;
    }

    // ************************************************************
    // Public Methods
    // ************************************************************
    public void addNewCustomer() {
        System.out.printf("\n\n\n%60s ++++++++++++++ Welcome to the Customer Registration Portal ++++++++++++++", "");
        Scanner read = new Scanner(System.in);
        System.out.print("\nEnter your name :\t");
        String name = read.nextLine();
        System.out.print("Enter your email address :\t");
        String email = read.nextLine();
        while (isUniqueData(email)) {
            System.out.println(
                    "ERROR!!! User with the same email already exists... Use new email or login using the previous credentials....");
            System.out.print("Enter your email address :\t");
            email = read.nextLine();
        }
        System.out.print("Enter your Password :\t");
        String password = read.nextLine();
        System.out.print("Enter your Phone number :\t");
        String phone = read.nextLine();
        System.out.print("Enter your address :\t");
        String address = read.nextLine();
        System.out.print("Enter your age :\t");
        int age = read.nextInt();
        customerCollection.add(new Customer(name, email, password, phone, address, age));
    }

    public void searchUser(String ID) {
        boolean isFound = false;
        Customer customerWithTheID = customerCollection.get(0);
        for (Customer c : customerCollection) {
            if (ID.equals(c.getUserID())) {
                System.out.printf("%-50sCustomer Found...!!!Here is the Full Record...!!!\n\n\n", " ");
                displayHeader();
                isFound = true;
                customerWithTheID = c;
                break;
            }
        }
        if (isFound) {
            System.out.println(customerWithTheID.toString(1));
            System.out.printf(
                    "%10s+------------+------------+----------------------------------+---------+-----------------------------+-------------------------------------+-------------------------+\n",
                    "");
        } else {
            System.out.printf("%-50sNo Customer with the ID %s Found...!!!\n", " ", ID);
        }
    }

    public boolean isUniqueData(String emailID) {
        for (Customer c : customerCollection) {
            if (emailID.equals(c.getEmail())) {
                return true;
            }
        }
        return false;
    }

    public void editUserInfo(String ID) {
        boolean isFound = false;
        Scanner read = new Scanner(System.in);
        for (Customer c : customerCollection) {
            if (ID.equals(c.getUserID())) {
                isFound = true;
                System.out.print("\nEnter the new name of the Passenger:\t");
                String name = read.nextLine();
                c.setName(name);
                System.out.print("Enter the new email address of Passenger " + name + ":\t");
                c.setEmail(read.nextLine());
                System.out.print("Enter the new Phone number of Passenger " + name + ":\t");
                c.setPhone(read.nextLine());
                System.out.print("Enter the new address of Passenger " + name + ":\t");
                c.setAddress(read.nextLine());
                System.out.print("Enter the new age of Passenger " + name + ":\t");
                c.setAge(read.nextInt());
                displayCustomersData(false);
                break;
            }
        }
        if (!isFound) {
            System.out.printf("%-50sNo Customer with the ID %s Found...!!!\n", " ", ID);
        }
    }

    public void deleteUser(String ID) {
        Iterator<Customer> iterator = customerCollection.iterator();
        while (iterator.hasNext()) {
            Customer customer = iterator.next();
            if (ID.equals(customer.getUserID())) {
                iterator.remove();
                System.out.printf("\n%-50sPrinting all Customer's Data after deleting Customer with the ID %s.....!!!!\n",
                        "", ID);
                displayCustomersData(false);
                return;
            }
        }
        System.out.printf("%-50sNo Customer with the ID %s Found...!!!\n", " ", ID);
    }

    public void displayCustomersData(boolean showHeader) {
        displayHeader();
        Iterator<Customer> iterator = customerCollection.iterator();
        int i = 0;
        while (iterator.hasNext()) {
            i++;
            Customer c = iterator.next();
            System.out.println(c.toString(i));
            System.out.printf(
                    "%10s+------------+------------+----------------------------------+---------+-----------------------------+-------------------------------------+-------------------------+\n",
                    "");
        }
    }

    // ************************************************************
    // Flight Management Methods
    // ************************************************************
    public void addFlight(Flight flight) {
        this.flightsRegisteredByUser.add(flight);
        this.numOfTicketsBookedByUser.add(1);
    }

    public boolean addTicketsToFlight(Flight flight, int numOfTickets) {
        int index = flightsRegisteredByUser.indexOf(flight);
        if (index >= 0) {
            int currentTickets = numOfTicketsBookedByUser.get(index);
            numOfTicketsBookedByUser.set(index, currentTickets + numOfTickets);
            return true;
        }
        return false;
    }

    public boolean removeFlight(Flight flight) {
        int index = flightsRegisteredByUser.indexOf(flight);
        if (index >= 0) {
            flightsRegisteredByUser.remove(index);
            numOfTicketsBookedByUser.remove(index);
            return true;
        }
        return false;
    }

    public int getTicketsForFlight(Flight flight) {
        int index = flightsRegisteredByUser.indexOf(flight);
        return index >= 0 ? numOfTicketsBookedByUser.get(index) : -1;
    }

    // ************************************************************
    // Helper Methods
    // ************************************************************
    private String toString(int i) {
        return String.format("%10s| %-10d | %-10s | %-32s | %-7s | %-27s | %-35s | %-23s |", "", i,
                randomIDDisplay(userID), name, age, email, address, phone);
    }

    void displayHeader() {
        System.out.println();
        System.out.printf(
                "%10s+------------+------------+----------------------------------+---------+-----------------------------+-------------------------------------+-------------------------+\n",
                "");
        System.out.printf(
                "%10s| SerialNum  |   UserID   | Passenger Names                  | Age     | EmailID\t\t       | Home Address\t\t\t     | Phone Number\t       |%n",
                "");
        System.out.printf(
                "%10s+------------+------------+----------------------------------+---------+-----------------------------+-------------------------------------+-------------------------+\n",
                "");
        System.out.println();
    }

    String randomIDDisplay(String randomID) {
        StringBuilder newString = new StringBuilder();
        for (int i = 0; i <= randomID.length(); i++) {
            if (i == 3) {
                newString.append(" ").append(randomID.charAt(i));
            } else if (i < randomID.length()) {
                newString.append(randomID.charAt(i));
            }
        }
        return newString.toString();
    }

    // ************************************************************
    // Getters and Setters
    // ************************************************************
    public List<Flight> getFlightsRegisteredByUser() {
        return Collections.unmodifiableList(flightsRegisteredByUser);
    }

    public List<Integer> getNumOfTicketsBookedByUser() {
        return Collections.unmodifiableList(numOfTicketsBookedByUser);
    }

    public String getPassword() {
        return password;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getEmail() {
        return email;
    }

    public int getAge() {
        return age;
    }

    public String getUserID() {
        return userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setAge(int age) {
        this.age = age;
    }

}